'use strict';
var mb={};
var window={};
if((""+ini.get("engine_name"))=="Mozilla Rhino"){
    //Java7加载Java类
    var Java={
	    type:function(str){
	        return eval("Packages."+str);
	    }
	};
    //只支持interface。
    mb.Java_new=function(cls,params,obj){
        if(typeof(cls)=='string'){
            cls=Java.type(cls);
        }
        return new JavaAdapter(cls,obj);
    };
}else{
    mb.Java_new=function(cls,params,obj){
        if(typeof(cls)=='string'){
            cls=Java.type(cls);
        }
        if(obj){
            var Class=Java.extend(cls,obj);
        }else{
            var Class=cls;
        }
        if(params&&params.length!=0){
	        var ps=[];
	        for(var i=0;i<params.length;i++){
	            ps.push("params["+i+"]");
	        }
            return eval("new Class("+ps.join(",")+");");
        }else{
            return new Class();
        }
    };
}
mb.String={
    /*
     * x:重复的字眼
     * length:长度
     * split:分割
     */
    repeat:function(x,length,split){
        var xs=x;
        if(split){
            xs=x+split;
        }
        var s="";
        var len=length-1;  
        if(len>0){
            for(var i=0;i<len;i++){
                s=s+xs;
            }
        }
        s=s+x;
        return s;
    }
};
mb.Function=(function(){
    var quote=function(){
        return function(a){
            return a;
        }
    };
    quote.one=quote();
    var as_null=function(){
        return function(){
            return null;
        };
    };
    as_null.one=as_null();
    return {
        quote:quote,
        as_null:as_null
    };
})();
mb.log=(function(){
    var Stringify=function(o){
        if(typeof(o)=='object'){
            var ret={};
            for(var k in o){
                ret[k]=""+o[k];
            }
            return JSON.stringify(ret,"",2);
        }else{
            return ""+o;
        }
    };
    var loadder=function(prefix){
        var toPrint;
        var toLog;
        var System=Java.type("java.lang.System");
        var _print_=function(a){
            System.out.print(a);
        };
        if(prefix){
            toPrint=function(str){
                return prefix+":\t"+str;
            };
            toLog=function(){
                _print_(prefix+":");
                _print_("\t");
            };
        }else{
            toPrint=function(str){return str;};
            toLog=function(){};
        }
        var log=function(){
            toLog();
            for(var i=0;i<arguments.length;i++){
                _print_(arguments[i]);
                _print_('\t');
            }
            _print_('\n');
        };
        var common=function(key){
            return function(o){
                var str=Stringify(o);
                log(key,str);
                //如果有定义输出到日志文件的
                var sl=ini.get("log");
                if(sl){
                    sl[key](toPrint(str));
                }
            };
        };
        log.stringifyError=Stringify;
        log.info=common("info");
        log.warn=common("warn");
        log.error=common("error");
        log.debug=common("debug");
        return log;
    };
    var log=loadder();
    log.loadder=loadder;
    return log;
})();
mb.Exception=function(s){
    return new (Java.type("java.lang.Exception"))(s);
};
mb.charAt=(function(){
    var me=ini.get("me");
    return function(s,i){
	    return me.charAt(s,i);
	}
})();
mb.Object={
	forEach:function(obj,func){
		for(var key in obj){
			func(obj[key],key);
		}
	},
	map:function(obj,func){
		var o={};
		for(var key in obj){
			o[key]=func(obj[key],key);
		}
		return o;
	}
};
mb.Array={
    toSet:function(arr){
        var ret={};
        arr.forEach(function(ar,i){
            ret[ar]=true;
        });
        return ret;
    },
    forEach:function(array,func){
        for(var i=0;i<array.length;i++){
            func(array[i],i);
        }
    },
    isArray:function(array){
        return Array.isArray(array);
    },
    map:function(array,func){
        var ret=[];
        for(var i=0;i<array.length;i++){
            ret[i]=func(array[i],i);
        }
        return ret;
    }
};
mb.time=(function(){
	var cache={};
	var getfmt=function(fmt_key){
		var fmt=cache[fmt_key];
		if(!fmt){
			fmt=new (Java.type("java.text.SimpleDateFormat"))(fmt_key);
			cache[fmt_key]=fmt;
		}
		return fmt;
	};
	return {
		now:function(fmt_key){
			return ""+getfmt(fmt_key).format(new (Java.type("java.util.Date"))());
		},
        from_millis:function(mills,fmt_key){
            var cal=Java.type("java.util.Calendar").getInstance();
            cal.setTimeInMillis(mills);
            return ""+getfmt(fmt_key).format(cal.getTime());
        }
	};
})();
mb.exist=function(v){
	return (v!=null && v!="")
};
mb.load=(function(){
    var sp=ini.get("file_sp");
    var base_path=ini.get("server_path")+sp;
    var me=ini.get("me");
    var cache={};
    var load=function(path,servlet,getFun){
        var o=cache[path];
        if(!o){
            var pkg=getFun(path);
            if(pkg){
	            o={
	                success:pkg.body.delay?pkg.body.success():pkg.body.success,
	                out:pkg.body.out
	            };
	            cache[path]=o;
                
                var loadPath=function(v){
                    var url;
                    if(v[0]=='.'){
                        url=mb.load.calAbsolutePath(path,v);
                    }else{
                        url=v;
                    }
                    return url;
                };
                var loadFun=function(url,fun){
                    if(url){
                        /*使用结果*/
                        return fun(loadPath(url));
                    }else{
                        /*延迟加载*/
                        return function(url){
                            return fun(loadPath(url));
                        };
                    }
                };
	            mb.Object.forEach(pkg.body.data,function(v,k){
		           var r;
		           if(typeof(v)=='string'){
	                    r=load(loadPath(v),false,getFun);
		           }else
		           if(typeof(v)=='object'){
		                if(typeof(v.type)=='string'){
		                    if(v.type=="path"){
                                r=loadFun(v.url,mb.load.path);
		                    }else
		                    if(v.type=="file"){
                                r=loadFun(v.url,mb.load.file);
		                    }else
		                    if(v.type=="text"){
                                r=loadFun(v.url,mb.load.text);
		                    }else{
		                        throw "未支持类型"+v;
		                        r=url;
		                    }
		                }else{
		                    /*本来是同步的，可以加载*/
		                    v.type(url,function(success){
	                            r=success;
	                        });
		                }
		           }else
		           if(typeof(v)=='function'){
		                v(function(success){
		                    r=success;
		                });
		           }else{
		                throw "未支持类型"+v;
		           }
		           pkg.lib[k]=r;
	            });
            }
        }
        if(o){
	        if(servlet){
	            if(o.out){
	                return o.success;
	            }else{
	                return null;
	            }
	        }else{
	            return o.success;
	        }
        }else{
            return null;
        }
    };
    /*计算绝对路径*/
    load.calAbsolutePath=function(base_url,url){
        var base=base_url.substr(0,base_url.lastIndexOf('/'))+'/'+url;
        var nodes=base.split('/');
        var rets=[];
        var last=null;
        for(var i=0;i<nodes.length;i++){
            var node=nodes[i];
            if(node=='..'){
                if(last=='..'){
                    rets.push(node);
                }else
                if(last==null){
                    rets.push(node);
                    last=node;
                }else{
                    rets.pop();
                    last=rets[rets.length-1];
                }
            }else
            if(node=='.'){
                //忽略
            }else{
                rets.push(node);
                last=node;
            }
        }
        return rets.join('/');
    };
    load.path=function(path){
        return base_path+sp+path;
    };
    /**
     * 返回JAVA的File实例，传入js为根的任何路径。
     */
    load.file=function(path){
        return me.fileFromPath(load.path(path));
    };
    load.text=function(path){
        return me.readTxt(load.path(path));
    };
    return load;
})();
mb.compile=(function(){
    //importPackage(java.io);
    var sp=""+ini.get("file_sp");
    var base_path=""+ini.get("server_path");
    
    var escapeStr=function(str){
        return "\""+str.replace(/"/g,"\\\"")+"\"";
    };
    /**
     * 保存文件
     */
    var saveTxt=function(content,path){
    	ini.get("me").saveText(content,path);
    };
    var loadTxt=function(f){
        return ini.get("me").readTxt(f);
    };
    /**
     * 用mb.load.require写的文件，其包含的内外都不能写代码。因为用toString对函数转化后会引用无效。
     * 表达式不能直接使用库，因为在用表达式同步化时使用eval，已经将表达式化简，必须保持为函数或字典等任一结构。
     * 
     * 对mb.ajax.require的启示：
     * 1，require区域外的代码无效，而且因为无法使用库，所以没有意义。
     * 2，success即使是数组字典，也可以使用库，只要引用库的叶子节点是函数。
     * 3，用delay来使用库，不能有任何表达式或立即执行的函数
     * 4，不使用名字，只使用({})就行了
     * 
     * 其实有重复计算的问题，每次返回的函数都不一样。可以缓存一下。
     * 都是delay的循环引用问题，引用时可能在未来，即区分构建时库和运行时库。
     */
    var loadRequire=(function(){
        var regx=(function(){
            if('/'==sp){
                return /\//g;
            }else
            if('\\'==sp){
                return /\\/g;
            }else
            {
                return eval("/"+sp+"/g");
            }
        })();
        var circleLoad=function(parent,name,root){
            var children=parent.listFiles();
            if(children==null)return;
            for(var i=0;i<children.length;i++){
                var child=children[i];
                var childName=name+child.getName();
                if(child.isDirectory()){
                    circleLoad(child,childName+sp,root);
                }else
                {
                    var suffix = childName.substring(childName.lastIndexOf(".") + 1).toLowerCase();
                    if("js"==suffix){
                        root[childName.replace(regx,'/')]=loadTxt(child);
                    }
                }
            }
        };
        var singLoad=function(folderName,root){
            circleLoad(mb.load.file(folderName),folderName,root);
        };
        return function(){
            var root={};
            for(var i=0;i<arguments.length;i++){
                singLoad(arguments[i],root);
            }
            return  [
                    "var cache={};",
                    (function(){
                            var core=[];
                            for(var key in root){
                                var e_key=escapeStr(key);
                                var txt=[
                                    "cache["+e_key+"]=function(){",
                                    "   var lib={};",
                                    "   var body="+root[key]+";",
                                    "   return {lib:lib,body:body};",
                                    "};"
                                ].join("\r\n");
                                core.push(txt);
                            }
                            return core.join("\r\n");
                    })()
            ].join("\r\n");
        };
    })();
    
    var loadMB=function(array,name){
         array.push(loadTxt(base_path+sp+"mb"+sp+name));
    };
    /**
     * 编译输出成字典
     */
    var ret=function(){
        
		var ret=["'use strict';"];
		//基础库包
		loadMB(ret,"lib.js");
		//兼容包
		loadMB(ret,ini.get("engine_name")+".js");
		//项目共性包
		loadMB(ret,"common.js");
        ret.push("(function(){");
        
        var getLib;
        if(ini.get("package")){
            //打包成一个文件
            var rqs=loadRequire(
                "act"+sp,
                "util"+sp,
                "ext"+sp);
                
            ret.push(rqs);
            /*打包文件*/
            getLib=function(path,servlet){
                return mb.load(path,servlet,function(url){
                    var v=cache[url];
                    if(v){
                        return v();
                    }else{
                        return null;
                    }
                });
            };
       }else{
           //不打包文件
           getLib=function(path,servlet){
	           return mb.load(path,servlet,function(url){
                    var txt=mb.load.text(url);
                    if(txt==null){
                        return null;
                    }else{
	                    var lib={};
	                    var body=eval(txt);
	                    return{
	                        lib:lib,
	                        body:body
	                    };
                    }
	           });
	       };
        }
        ret.push("var getLib="+getLib.toString()+";");
        
        
        /**对servlet不同的加载方案**/
        ret.push("(");
        ret.push((function(){
	        getLib("ext/index.js")(mb);
	        if(ini.get("servlet")==true){
	            mb.init(function(path){
	                return getLib(path,true);
	            });
	        }else{
	            mb.init(function(path){
	                return getLib(path);
	            });
	        }
        }).toString());
        ret.push("())");
        /****/
        ret.push("}())");
        return ret.join('\r\n');
    };
    //缓存到文件
    ret.save=function(){
        var x=ret();
        saveTxt(x,base_path+sp+"out.jsx");
        return x;
    };
    return ret;
})();

/**
 * java7兼容库
 */


/**
 * 项目共性库
 */

mb.request=(function(){
	var get=function(key){
		return ini.get("request").get(key);
	};
	return {
	    get:function(key){
	        var ret=get(key);
	        if(ret==null){
	            return "";
	        }else{
	            return ""+ret;
	        }
	    },
	    json:function(key){
	    	var ret=get(key);
	    	if(ret!=null){
	    		return JSON.parse(ret);
	    	}else{
	    		return null;
	    	}
	    }
	};
})();
mb.response=(function(){
    var write=function(code,description,obj){
        var res=ini.get("response");
        res.put("code",code);
        res.put("description",description);
        res.put("data",obj);
    };
    return {
        object:write,
        json:function(obj){
            write(0,"操作成功",JSON.stringify(obj));
        },
        success:function(msg){
            write(0,""+(msg||"操作成功"));
        },
        error:function(msg,code){
            write(code||-1,""+msg);
        }
    };
})();
mb.init=(function(){
    if(ini.get("servlet")==true){
        var initFunc=function(succ){
	        /**
	         * 新式的调用JS
	         */
	        //从servlet直接调用到这里。
	        var req=ini.get("request");
	        var res=ini.get("response");
	        var servlet=mb.servlet(req,res);
	        var request=(function(){
	            var url=(""+req.getRequestURI());
	            var url_prefix=""+ini.get("url_prefix");
	            var act=url.substring(url.indexOf(url_prefix)+url_prefix.length,url.length);
	            act=""+decodeURI(act);
	            if(url_prefix!="/"){
	                var log_prefix=url_prefix.substring(0,url_prefix.indexOf("/"));
	                ini.put("log",ini.get("me").getLogger(log_prefix));
	            }
	            var me={
	                getAct:function(){
	                    return act;
	                },
	                session:servlet.session //不同项目不同实现
	            };
	            var parseJSON=function(str){
	                return JSON.parse(str.replace(/&quot;/g,"\""));
	            };
	            me.p_str=function(key){
	                var v=req.getParameter(key);
	                if(v){
	                    return ""+v;
	                }else{
	                    return null;
	                }
	            };
	            me.p_strs=function(key){
	                //返回多维数组
	                var vs=req.getParameterValues(key);
	                var ret=[];
	                for(var i=0;i<vs.length;i++){
	                    ret.push(""+vs[i]);
	                }
	                return ret;
	            };
	            me.p_json=function(key){
	                var v=me.p_str(key);
	                if(v==null){
	                    return v;
	                }else{
	                    return parseJSON(v);
	                }
	            };
	            me.p_jsons=function(key){
	                var vs=me.p_strs(key);
	                for(var i=0;i<vs.length;i++){
	                    vs[i]=parseJSON(vs[i]);
	                }
	                return vs;
	            };
	            servlet.request(me);//扩展实现
	            return me;
	        })();
	        
	        var response=(function(){
	            var w=res.getWriter();
	            var isWrite=false;
	            var write=function(txt){
	                isWrite=true;
	                res.setHeader("Content-type", "text/html;charset=UTF-8");
	                res.setCharacterEncoding("utf-8");
	                w.append(txt);
	            };
	            var writeJSON=function(obj){
	                write(JSON.stringify(obj));
	            };
	            var me={
	                json:function(o){
	                    writeJSON({
	                        code:0,
	                        description:"操作成功",
	                        data:o
	                    });
	                },
	                success:function(msg){
	                    writeJSON({
	                        code:0,
	                        description:msg||"操作成功"
	                    });
	                },
	                error:function(msg,code){
	                    writeJSON({
	                        code:code||-1,
	                        description:msg
	                    });
	                },
	                text:function(txt){
	                    write(txt);
	                },
	                isWrite:function(){
	                    return isWrite;
	                }
	            };
	            //扩展实现
	            servlet.response(me,function(v){
	                isWrite=v;
	            })
	            return me;
	        })();
	        /**
	         * 为什么要将request/response作为单独的参数传递而不是全局的常量？
	         * 未来可能做到js不是处理单线程，而是web服务本身的多线程。
	         * request还带有session，可以查询操作员、权限
	         * web本身是无状态的
	         */
	        var doServlet=function(servlet,str){
	            var will=true;
	            servlet(request,response);
	            if(response.isWrite()){
	                will=false;
	            }
	            return will;
	        };
            var path=request.getAct();
            var nodes=path.split("/");
            var substr="";
            var will=true;
            var has=false;
            var already=[];
            var work_base_path=ini.get("work_base_path");
            if(work_base_path!=""){
                work_base_path="/"+work_base_path;
            }
            already.push("act"+work_base_path);
            try{
                while(nodes.length!=0 && will){
                    already.push(nodes.shift());
                    var substr=already.join("/");
                    /**
                     * //先查找代表目录的文件，如果有，一般都不用继续查找，因为它是文件夹不是叶子文件。
                     */
                    var servlet=succ(substr+"/_.js");
                    if(servlet){
                        /**
                         * 可能是过滤器，作为枝节点，但并无处理，转由下一个处理。
                         */
                        will=doServlet(servlet);
                    }else{
                        /**
                         * 叶子结点，如果有与文件夹同名的怎么办？有点该着被同名文件拦截了的味道，本来同名文件和文件夹并不冲突，因为有js后缀，同外文件更应该作为根结点。
                         */
                        servlet=succ(substr+".js");
                        if(servlet){
                            has=true;
                            will=doServlet(servlet);
                        }
                    }
                }
                if(will){
                    //没有找到
                    if(has){
                        mb.log.error("经过具体页面，但并未处理");
                    }
                    //有过默认的处理方法，可能是根处理器，还是不返回吧，报个404，当未处理。
                    response.error("未找到对应处理的方法",404);
                }
            }catch(ex){
                mb.log.error(ex);
                response.error(""+ex,500);
            }
        };
    }else{
        /**
         * 传统的间接调用JS
         */
	    var initFunc=function(succ){
	        var act=""+ini.get("act");
	        if(act!=""){
	            var path=("act>"+act+".js").replace(/>/g,"/");
                var action= succ(path);
                var e=null;
                if(action){
                    try{
                        action();
                    }catch(ex){
                        e=ex;
                    }
                }else{
                    e="未找到指定页面"+path;
                }
                if(e){
                    mb.log(act,e,"这里");
                    mb.log.error(e);
                    mb.response.error(e,404);
                }
	        }
	    };
    }
    return initFunc;
})();

(function(){
var getLib=function(path,servlet){
	           return mb.load(path,servlet,function(url){
                    var txt=mb.load.text(url);
                    if(txt==null){
                        return null;
                    }else{
	                    var lib={};
	                    var body=eval(txt);
	                    return{
	                        lib:lib,
	                        body:body
	                    };
                    }
	           });
	       };
(
function(){
	        getLib("ext/index.js")(mb);
	        if(ini.get("servlet")==true){
	            mb.init(function(path){
	                return getLib(path,true);
	            });
	        }else{
	            mb.init(function(path){
	                return getLib(path);
	            });
	        }
        }
())
}())