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
    var list=function(){
        return function(){
            var r=[];
            for(var i=0;i<arguments.length;i++){
                r.push(arguments[i]);
            }
            return r;
        };
    };
    list.one=list();
    return {
        quote:quote,
        list:list,
        as_null:as_null
    };
})();
mb.log=(function(){
    var System=Java.type("java.lang.System");
    var _print_=function(a){
        System.out.print(a);
    };
    return function(){
        for(var i=0;i<arguments.length;i++){
            _print_(arguments[i]);
            _print_('\t');
        }
        _print_('\n');
    };
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
	},
    toArray:function(obj,func){
        var r=[];
        for(var k in obj){
            r.push(func(obj[k],k,obj));
        }
        return r;
    },
    reduce:function(obj,func,init){
        for(var k in obj){
            init=func(init,obj[k],k,obj);
        }
        return init;
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
    },
    reduce:function(array,func,init){
        for(var i=0;i<array.length;i++){
            init=func(init,array[i],i,array);
        }
        return init;
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
/**
 * 路径转化成文件
 * @param {} path
 * @return {}
 */
mb.fileFromPath=function(path){
    return ini.get("me").fileFromPath(path);
};
/**
 * 读取文件
 * @param {} pathOrFile 可为路径或文件
 * @return {}
 */
mb.readText=function(pathOrFile){
    var txt=ini.get("me").readTxt(pathOrFile);
    if(txt!=null){
        txt=txt+"";//转化成内置字符串
    }
    return txt;
};
/**
 * 保存文本文件
 * @param {} path
 * @param {} content
 */
mb.saveText=function(path,content){
    ini.get("me").saveText(path,content);
};
mb.load=(function(){
    var base_path=ini.get("server_path");
    var cache={};
    /*转成绝对的key。按理说只有绝对路径，但打包只取相对路径*/
    var path_Key=function(path,v){
        if(v){
            return mb.load.calAbsolutePath(path,v);
        }else{
            return path;
        }
    };
    var load=function(path,deal,getFun){
        var pkg=cache[path];
        if(!pkg){
            pkg=getFun(path);
            if(pkg){
                if(pkg.body.delay){
                    pkg.body.success=pkg.body.success();
                    delete pkg.body.delay;
                }
	            cache[path]=pkg;
	            mb.Object.forEach(pkg.body.data,function(v,k){
		           var r;
		           if(typeof(v)=='string'){
	                    r=load(path_Key(path,v),false,getFun);
		           }else{
		                throw "未支持类型"+v;
		           }
		           pkg.lib[k]=r;
	            });
            }
        }
        /**
        pkg:{
            lib
            success
            out
            ...
        }
         */
        if(pkg){
	        if(deal){
	            return deal(pkg);
	        }else{
	            return pkg.body.success;
	        }
        }else{
            return null;
        }
    };
    /*计算绝对路径*/
    load.calAbsolutePath=function(base_url,url){
        if(url[0]=='.'){
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
        }else{
            return url;
        }
    };
    /*从根路径开始计算->绝对路径*/
    load.path=function(path){
        return base_path+"/"+path;
    };
    /*相对路径->绝对路径*/
    load.pathOf=function(path,v){
        return load.path(path_Key(path,v));
    };
    return load;
})();
mb.compile=(function(){
    var base_path=ini.get("server_path");
    var escapeStr=function(str){
        return "\""+str.replace(/"/g,"\\\"")+"\"";
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
        var circleLoad=function(parent,name,root){
            var children=parent.listFiles();
            if(children==null)return;
            for(var i=0;i<children.length;i++){
                var child=children[i];
                var childName=name+child.getName();
                if(child.isDirectory()){
                    circleLoad(child,childName+"/",root);
                }else
                {
                    var suffix = childName.substring(childName.lastIndexOf(".") + 1).toLowerCase();
                    if("js"==suffix){
                        root[childName.replace(/\\/g,'/')]=mb.readText(child);
                    }
                }
            }
        };
        var singLoad=function(folderName,root){
            circleLoad(mb.fileFromPath(mb.load.path(folderName)),folderName,root);
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
                                    "   var pathOf=function(url){return mb.load.pathOf("+e_key+",url);};",
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
         array.push(mb.readText(base_path+"/mb/"+name));
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
        var getLib;
        if(ini.get("package")==true){
            //打包成一个文件
            var rqs=loadRequire(
                "act/",
                "util/",
                "ext/");
                
            ret.push(rqs);
            /*打包文件*/
            getLib=function(path,deal){
                return mb.load(path,deal,function(url){
                    var v=cache[url];
                    if(v){
                        return v();
                    }else{
                        return null;
                    }
                });
            };
       }else{
           /*不打包文件*/
           getLib=function(path,deal){
	           return mb.load(path,deal,function(url){
                    var file=mb.fileFromPath(mb.load.path(url));
                    if(file.exists() && file.isFile()){
                        var txt=mb.readText(file);
                        var lib={};
                        var pathOf=function(v){
                            return mb.load.pathOf(url,v);
                        };
                        var body=eval(txt);
                        return{
                            lib:lib,
                            body:body
                        };
                    }else{
                        return null;
                    }
	           });
	       };
        }
        ret.push("mb.getLib="+getLib.toString().trim()+";mb.getLib(\"ext/index.js\")(mb);");
        /****/
        ret.push("("+(function(){
            return mb.Java_new("mb.JSBridge.JSMethod",[],{
                run:function(map){
                    var url="act/"+map.get("type")+"/index.js";
                    var act=mb.getLib(url);
                    if(act!=null){
                        act(map);
                    }else{
                        mb.log("未找到处理模块！"+url);
                    }
                }
            });
        }).toString().trim()+"());");
        return ret.join('\r\n');
    };
    //缓存到文件
    ret.save=function(){
        var x=ret();
        mb.saveText(ini.get("jsx_path"),x);
        return x;
    };
    return ret;
})();

/**
 * java7兼容库
 */

var cache={};
cache["act/map/index.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("act/map/index.js",url);};
   var body=({
    data:{},
    success:function(map){
        
	    var request=(function(){
	        var get=function(key){
	            return map.get("request").get(key);
	        };
	        return {
                /*获得实体*/
	            get:get,
                /*获得字符串，默认空*/
                get_str:function(key){
                    var ret=get(key);
                    if(ret==null){
                        return "";
                    }else{
                        return ""+ret;
                    }
                },
                /*获得字符串，并转为JSON*/
	            get_json:function(key){
	                var ret=get(key);
	                if(ret!=null){
	                    return JSON.parse(ret);
	                }else{
	                    return null;
	                }
	            }
	        };
	    })();
	    var response=(function(){
	        var write=function(code,description,obj){
	            var res=map.get("response");
	            res.put("code",code);
	            res.put("description",description);
	            res.put("data",obj);
	        };
	        return {
	            object:write,
                /*data部分是实体*/
                write:function(obj){
                    write(0,"操作成功",obj);
                },
                /*data部分是空*/
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
	    
	    /**
	     * 传统的间接调用JS
	     */
	
	    var act=""+map.get("act");
	    if(act!=""){
	        var path=("act/map/"+act+".js").replace(/>/g,"/");
	        var action=mb.getLib(path);
	        var e=null;
	        if(action){
	            try{
	                action(request,response);
	            }catch(ex){
	                e=ex;
	                e.printStackTrace();
	            }
	        }else{
	            e="未找到指定页面"+path;
	        }
	        if(e){
	            var log=map.get("log");
	            if(log){
	                log.error(act+"出错"+e);
	            }
	            response.error(e,404);
	        }
	    }
    }
})
;
   return {lib:lib,body:body};
};
cache["act/map/js/run.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("act/map/js/run.js",url);};
   var body=({
    success:function(){
        
    }
})
;
   return {lib:lib,body:body};
};
cache["act/map/js/shell.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("act/map/js/shell.js",url);};
   var body=({
    data:{
        shell:"util/shell.js"
    },
    delay:true,
    success:function(){
        var serialize_fun=function(fun){
            return "<Function>";//obj.toString();
        };
        return function(){
	        var log;
	        lib.shell({
	            end:"//",
	            shell:function(log_f){
	                log=function(){
	                    for(var i=0;i<arguments.length;i++){
	                        log_f(arguments[i]);
	                        log_f("\t");
	                    }
	                };
	                return eval;
	            },
	            toString:function(obj){
	                if(obj==null){
	                    return "null";
	                }else
                    if(typeof(obj)=="function"){
                        return serialize_fun(obj);
                    }else{
	                    return JSON.stringify(
                            obj,
                            function(key,value){
                                if(typeof(value)=="function"){
                                    return serialize_fun(value);
                                }else{
                                    return value;
                                }
                            },
                            2
                        );
	                }
	            }
	        })();  
        };
    }
})
;
   return {lib:lib,body:body};
};
cache["act/map/run.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("act/map/run.js",url);};
   var body=({
    data:{
        S_Lisp:"util/back-s-lisp/index.js"
    },
    success:function(req,res){
        var path=""+req.get("shell");
        path=path.replace(/\\/g,'/');
        var S_Lisp=lib.S_Lisp();
        S_Lisp.run(path,req.get("args"));
    }
});
;
   return {lib:lib,body:body};
};
cache["act/map/shell.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("act/map/shell.js",url);};
   var body=({
    data:{
        S_Lisp:"util/back-s-lisp/index.js",
        shell:"util/shell.js"
    },
    success:function(){
        var S_Lisp=lib.S_Lisp();
        lib.shell({
            end:"``",
            shell:function(log){
                return S_Lisp.shell(log,'\n');
            },
            toString:function(obj){
                return S_Lisp.toString(obj,true).toString();
            }
        })();
    }
});
;
   return {lib:lib,body:body};
};
cache["act/web/index.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("act/web/index.js",url);};
   var body=({
    delay:true,
    success:function(){
	    var parseJSON=function(str){
	        return JSON.parse(str.replace(/&quot;/g,"\""));
	    };
	    var webPackageDeal=function(pkg){
	        if(pkg.body.out){
	            return pkg.body.success;
	        }else{
	            return null;
	        }
	    };
	    return function(map){
	        /**
	         * 新式的调用JS
	         */
	        //从servlet直接调用到这里。
	        var req=map.get("request");
	        var res=map.get("response");
	        var servlet=mb.servlet(req,res);
	        var url_prefix=""+map.get("url_prefix");
	        
	        var log_prefix="js";
	        if(url_prefix!="/"){
	            log_prefix=url_prefix.substring(0,url_prefix.indexOf("/"));
	        }
	        var log=ini.get("me").getLogger(log_prefix);
	        
	        var request=(function(){
	            var url=decodeURI((""+req.getRequestURI()));
	            var act=url.substring(url.indexOf(url_prefix)+url_prefix.length,url.length);
	            map.put("log",log);
	            var me={
	                getAct:function(){
	                    return act;
	                },
	                original:req,
	                session:servlet.session //不同项目不同实现
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
	        var work_base_path=map.get("work_base_path");
	        if(work_base_path!=""){
	            work_base_path="/"+work_base_path;
	        }
	        already.push("act/web"+work_base_path);
	        var log=map.get("log")||mb.log;
	        try{
	            while(nodes.length!=0 && will){
	                already.push(nodes.shift());
	                var substr=already.join("/");
	                /**
	                 * //先查找代表目录的文件，如果有，一般都不用继续查找，因为它是文件夹不是叶子文件。
	                 */
	                var servlet=mb.getLib(substr+"/_.js",webPackageDeal);
	                if(servlet){
	                    /**
	                     * 可能是过滤器，作为枝节点，但并无处理，转由下一个处理。
	                     */
	                    will=doServlet(servlet);
	                }else{
	                    /**
	                     * 叶子结点，如果有与文件夹同名的怎么办？有点该着被同名文件拦截了的味道，本来同名文件和文件夹并不冲突，因为有js后缀，同外文件更应该作为根结点。
	                     */
	                    servlet=mb.getLib(substr+".js",webPackageDeal);
	                    if(servlet){
	                        has=true;
	                        will=doServlet(servlet);
	                    }
	                }
	            }
	            if(will){
	                //没有找到
	                if(has){
	                    log.error("经过具体页面，但并未处理");
	                }
	                //有过默认的处理方法，可能是根处理器，还是不返回吧，报个404，当未处理。
	                response.error("未找到对应处理的方法",404);
	            }
	        }catch(ex){
	            log.error(ex);
	            response.error(""+ex,500);
	        }
	    }
    }
})
;
   return {lib:lib,body:body};
};
cache["util/back-s-lisp/index.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("util/back-s-lisp/index.js",url);};
   var body=({
    data:{
       library:"./library.js",
       s:"./s.js"
    },
    success:function(){
        var path=ini.get("server_path")+"/../";
        var QueueRun=Java.type("s.QueueRun");
        var Load=Java.type("s.library.Load");
        var library=lib.library();
        
        var line_split=mb.charAt("\n",0);
        var scope=library.library;//在js中定义的库
        var scope_extend=Load.run_e(library.S_Root+"index.lisp",scope,line_split);
        scope_extend=lib.s.reverse(scope_extend);
        for(var t=scope_extend;t!=null;t=t.Rest()){
            var value=t.First();
            t=t.Rest();
            var key=t.First();
            scope=lib.s.kvs_extend(key,value,scope);
        }
        
        return {
            run:function(x_path,args){
                var o=Load.run_e(x_path,scope,line_split);
                if(o){
                    o.exec(args);
                }else{
                    mb.log(x_path);
                }
            },
            shell:function(log,split){
                var qr=new QueueRun(
	                lib.s.kvs_extend(
	                    "log",
	                    library.buildFunc(
                            "log",
	                        library.log_factory(log)
	                    ),
                        scope
	                )
	            );
                return function(str){
                    return qr.exec(str,line_split);
                }
            },
            toString:library.toString
        }
    }
})
;
   return {lib:lib,body:body};
};
cache["util/back-s-lisp/library - 副本.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("util/back-s-lisp/library - 副本.js",url);};
   var body=({
    data:{
        s:"./s.js"
    },
    success:function(){
        var Fun=Java.type("s.Function");
        var Parse=Java.type("s.library.Parse");
        var System=Java.type("java.lang.System");
        var mb_Util=Java.type("mb.Util");
        var Java_String=Java.type("java.lang.String");
        var S_Root=System.getenv().get("S_LISP")||"D:/S-Lisp";
        mb.log(S_Root);
        
        var double_quotes=mb.charAt('"',0);
        var line_split=mb.charAt("\n",0);
        
        var s_trans=function(v){
            if(v==null){
                v="[]";
            }else
            if(v instanceof Java_String){
                v=mb_Util.string_to_trans(v,double_quotes,double_quotes,null);
            }else{
                v=v.toString();
            }
            return v;
        };
        
        var or=function(a,b){
            return a||b;
        };
        var and=function(a,b){
            return a&&b;
        };
        var reduce=function(args,func,init) {
            for(var t=args;t!=null;t=t.Rest()){
                init=func(init,t.First());
            }
            return init;
        };
        var compare=function(args,check,func){
            var last=args.First();
            var init=true;
            check(last);
            //mb.log(args.toString())
            for(var t=args.Rest();t!=null;t=t.Rest()){
                var now=t.First();
                check(now);
                init=and(init,func(last,now));
                last=now;
            }
            return init;
        };
        var check_is_number=function(s){
            if(s==0){
                return true;
            }else
            if(s && s.constructor==Number){
                return true;
            }else{
                mb.log(s+"不是合法的数字类型"+s.constructor);
                return false;
            }
        };
        var kvs_path=function(kvs,paths){
            var value=null;
            while(paths!=null){
                var path=paths.First();
                value=lib.s.kvs_find1st(kvs,path);
                paths=paths.Rest();
                kvs=value;
            }
            return value;
        };
        var eq=function(args,check){
            //可用于数字，字符串，实体
            return compare(args,check,function(last,now){
                return (last==now);
            });
        };

        var log_factory=function(append){
            return function(args){
                for(var t=args;t!=null;t=t.Rest()){
                    append(s_trans(t.First()));
                    append("\t");
                }
                append("\n");
                return null;
            };
        };
		var library={
            "false":false,
            "true":true,
            first:function(args){
                var v=args.First();
                return v.First();
            },
            rest:function(args){
                var v=args.First();
                return v.Rest();
            },
            extend:function(args){
                return lib.s.extend(args.First(),args.Rest().First());
            },
            length:function(args){
                return args.First().Length();
            },
            "+":function(args){
                return reduce(args,function(last,now){
                    return last+now;
                },0);
            },
            "-":function(args){
                var r=args.First();
                return reduce(args.Rest(),function(last,now){
                    return last-now;
                },r);
            },
            "*":function(args){
                return reduce(args,function(last,now){
                    return last*now;
                },1);
            },
            "/":function(args){
                var r=args.First();
                return reduce(args.Rest(),function(last,now){
                    return last/now;
                },r);
            },
            "parseInt":function(args){
                return parseInt(args.First());
            },
            ">":function(args){
                //数字
                return compare(args,check_is_number,function(last,now){
                    return (last>now);
                });
            },
            "<":function(args){
                //数字
                return compare(args,check_is_number,function(last,now){
                    return (last<now);
                });
            },
            "=":function(args){
                return eq(args,check_is_number);
            },
            and:function(args){
                return reduce(args,function(init,v) {
                    return and(init,v);
                },true);
            },
            or:function(args){
                return reduce(args,function(init,v) {
                    return or(init,v);
                },false);
            },
            not:function(args){
                return !args.First();
            },
            "empty?":function(args){
                return (args.First()==null);
            },
            "exist?":function(args) {
                return (args.First()!=null);
            },
            log:log_factory(function(v){
                System.out.print(v);
            }),
            //a?b:default(null)
            "if":function(args){
                if(args.First()==true){
                    return args.Rest().First();
                }else{
                    args=args.Rest().Rest();
                    if(args){
                        return args.First();
                    }else{
                        return null;
                    }
                }
            },
            eq:function(args){
                return eq(args,function(){return true;});
            },
            stringify:function(args){
                //类似于JSON.stringify，没想好用toString还是stringify;
                return args.First().toString();  
            },
            type:function(args){
                var n=args.First();
                if(n==null){
                    return "list";
                }else{
                    if(n instanceof Node){
                        return "list";
                    }else
                    if(n instanceof Fun){
                        return "function";
                    }else{
                        var t=typeof(n);
                        if(t=="string"){
                            return "string";
                        }else
                        if(t=="boolean"){
                            return "bool";
                        }else
                        if(t=="number"){
                            if(n%1===0){
                                return "int";
                            }else{
                                return "float";
                            }
                        }else{
                            return t;
                        }
                    }
                }
            },
            "str-eq":function(args){
                return eq(args,function(s){
                    if(s && s.constructor==String){
                        return true;
                    }else{
                        return false;
                    }
                });
            },
            "str-length":function(args) {
                var str=args.First();
                return str.length;
            },
            "str-charAt":function(args){
                var str=args.First();
                args=args.Rest();
                var index=args.First();
                return str.charAt(index);
            },
            "str-join":function(args){
                //字符串
                var array=args.First();
                var split="";
                if(args.Rest()!=null){
                    split=args.Rest().First();
                }
                var r="";
                for(var t=array;t!=null;t=t.Rest()){
                    r=r+t.First()+split;
                }
                return r.substr(0,r.length-split.length);
            },
            "str-trim":function(args) {
                var str=args.First();
                return str.trim();
            },
            "str-startsWith":function(args) {
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.startsWith(v);
            },
            "str-endsWith":function(args) {
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.endsWith(v);
            },
            "str-indexOf":function(args) {
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.indexOf(v);
            },
            "str-lastIndexOf":function(args) {
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.lastIndexOf(v);
            },
            "str-split":function(args){
                var str=args.First();
                args=args.Rest();
                var split=args.First();
                var array=str.split(split);
                var r=null;
                for(var i=array.length-1;i>-1;i--){
                    r=lib.s.extend(array[i],r);
                }
                return r;
            },
            "str-upper":function(args){
                return args.First().toUpperCase();
            },
            "str-lower":function(args){
                return args.First().toLowerCase();
            },


            
            quote:function(args){
                return args.First();
            },
            /*主要用于用闭包构建参数*/
            list:function(args){
                return args;
            },
            call:function(args){
                var run=args.First();
                args=args.Rest();
                return run.exec(args);
            },
            reverse:function(args){
                var v=args.First();
                return lib.s.reverse(v);
            },
            "kvs-find1st":function(args){
                var kvs=args.First();
                args=args.Rest();
                var key=args.First();
                return lib.s.kvs_find1st(kvs,key);
            },
            "kvs-extend":function(args) {
                var key=args.First();
                args=args.Rest();
                var value=args.First();
                args=args.Rest();
                var kvs=args.First();
                return lib.s.kvs_extend(key,value,kvs);
            },
            "kvs-path":function(args){
                var kvs=args.First();
                args=args.Rest();
                var paths=args.First();
                return kvs_path(kvs,paths);
            },
            "kvs-path-run":function(args){
                var kvs=args.First();
                args=args.Rest();
                var paths=args.First();
                args=args.Rest();
                return kvs_path(kvs,paths).exec(args);
            },
            /*计算绝对路径*/
            "path-resolve":function(args){
                var base_path=args.First();
                args=args.Rest();
                var relative_path=args.First();
                return mb.load.calAbsolutePath(base_path,relative_path);
            },
            read:function(args){
                var path=args.First();
                args=args.Rest();
                var lineSplit="\n";
                var charsetName="UTF-8";
                if(args){
                    lineSplit=args.First()||lineSplit;
                    args=args.Rest();
                    if(args){
                        charsetName=args.First()||charsetName;
                    }
                }
                return mb_Util.readTxt(path,lineSplit,charsetName);  
            },
            write:function(args){
                var path=args.First();
                args=args.Rest();
                var content=args.First();
                args=args.Rest();
                var charsetName="UTF-8";
                if(args){
                    charsetName=args.First();
                }
                return mb_Util.saveTxt(path,content,charsetName);
            },
            "lib-path":function(args){
                return S_Root+"/"+args.First();  
            },
            /**
             * 返回类型
             * String//type类型
             */
            Java_type:function(args){
                return Java.type(args.First());
            },
            /**
             * 创建实例
             * type//Java_type返回的类型，或类型名
             * [params]//构造参数
             * [kvs]//重载
             */
            Java_new:function(args){
                var Class=args.First();
                if(typeof(Class)=='string'){
                    Class=Java.type(Class);
                }
                args=args.Rest();
                if(args){
                    //有参
                    var params=args.First();
                    args=args.Rest();
                    if(args){
                        //重写
                        var kvs=args.First();
                        var os=lib.s.o_from_kvs(kvs);
                        var o=mb.Object.map(os,function(v,k){
                            return function(){
                                var r=null;
                                for(var i=arguments.length-1;i>-1;i--){
                                    r=lib.s.extend(arguments[i],r);
                                }
                                return v.exec(r);
                            }
                        });
                        Class=Java.extend(Class,o);
                    }
                    var ps=[];
                    return eval("new Class"+exec(params,ps,"ps"));
                }else{
                    //无参
                    params=[];
                    return new Class();
                }
            },
            /**
             * 兼容实例方法与静态方法
             * Object:obj
             * String:method
             * []:params
             */
            Java_call:function(args){
                var obj=args.First();
                args=args.Rest();
                var method=args.First();
                args=args.Rest();
                if(args){
                    //有参
                    var params=args.First();
                    var ps=[];
                    return eval("obj."+method+exec(params,ps,"ps"));
                }else{
                    //无参
                    return obj[method]();
                }
            },
            /**
             * 读写属性
             * Object
             * key
             * value
             */
            Java_attr:function(args){
                var obj=args.First();
                args=args.Rest();
                var key=args.First();
                args=args.Rest();
                if(args){
                    //设值
                    obj[key]=args.First();
                    return null;
                }else{
                    //取值
                    return obj[key];
                }
            },
            apply:function(args){
                var func=args.First();
                var args=args.Rest().First();
                return func.exec(args);
            }
        };
        var exec=function(params,ps,ps_name){
            var i=0;
            var str="(";
            for(var t=params;t!=null;t=t.Rest()){
                var v=t.First();
                ps.push(v);
                str=str+ps_name+"["+i+"],";
                i++;
            }
            if(i!=0){
                str=str.substr(0,str.length-1);
            }
            str=str+");";
            return str;
        };
        var buildFunc=function(k,v){
            if(typeof(v)=='function'){
                return mb.Java_new(Fun,[],{
                    exec:v,
                    toString:function(){
                        return k;
                    },
                    ftype:function(){
                        return Fun.Type.buildIn;
                    }
                });
            }else{
                return v;
            }
        };
        var r=null;
        mb.Object.forEach(library,function(v,k){
            r=lib.s.kvs_extend(k,buildFunc(k,v),r);
        });
        var library=r;
        r=lib.s.kvs_extend("parse",new Parse(r,line_split),r);
        r=lib.s.kvs_extend("base-scope",r,r);
        r=lib.s.kvs_extend(
            "cache",
            buildFunc("cache",function(args){
                var v=args.First();
                /*cache返回这个函数是特殊的*/
                return mb.Java_new(Fun,[],{
                    exec:function(args){
	                    if(args==null){
	                        return v;
	                    }else{
	                        v=args.First();
	                    }
	                },
                    toString:function(){
                        return "[]";
                    },
                    ftype:function(){
                        return Fun.Type.cache;
                    }
                });
            }),
        r);
        
        return {
            library:r,
            buildFunc:buildFunc,
            log_factory:log_factory,
            s_trans:s_trans
        };
    }
});
;
   return {lib:lib,body:body};
};
cache["util/back-s-lisp/library.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("util/back-s-lisp/library.js",url);};
   var body=({
    data:{
        s:"./s.js",
        System:"./System.js"
    },
    success:function(){
        var Fun=Java.type("s.Function");
        var Node=Java.type("s.Node");
        var Parse=Java.type("s.library.Parse");
        var System=Java.type("java.lang.System");
        var mb_Util=Java.type("mb.Util");
        var Java_String=Java.type("java.lang.String");
        var S_Root=System.getenv().get("S_LISP");
        if(S_Root!=null && S_Root!=""){
            S_Root=""+S_Root;
        }else{
            S_Root="D:/S-Lisp";
        }
        S_Root=""+S_Root.replace(/\\/g,'/');
        if(!S_Root.endsWith("/")){
            S_Root=S_Root+"/";
        }
        
        var double_quotes=mb.charAt('"',0);
        var line_split=mb.charAt("\n",0);
        
        var toString=function(v,trans){
            if(v==null){
                v="[]";
            }else
            if(trans){
	            if(v instanceof Java_String){
	                v=mb_Util.string_to_trans(v,double_quotes,double_quotes,null);
	            }else{
                    v=v.toString();
                }
            }else{
                v=v.toString();
            }
            return ""+v;
        };

        /*打印S列表*/
        var log_factory=function(log){
            return function(args){
                for(var t=args;t!=null;t=t.Rest()){
                    log(toString(t.First(),true));
                    log("\t");
                }
                log("\n");
                return null;
            };
        };
		var library={
            /*计算绝对路径*/
            "path-resolve":function(args){
                var base_path=args.First();
                args=args.Rest();
                var relative_path=args.First();
                return mb.load.calAbsolutePath(base_path,relative_path);
            },
            read:function(args){
                var path=args.First();
                args=args.Rest();
                var lineSplit="\n";
                var charsetName="UTF-8";
                if(args){
                    lineSplit=args.First()||lineSplit;
                    args=args.Rest();
                    if(args){
                        charsetName=args.First()||charsetName;
                    }
                }
                return mb_Util.readTxt(path,lineSplit,charsetName);  
            },
            write:function(args){
                var path=args.First();
                args=args.Rest();
                var content=args.First();
                args=args.Rest();
                var charsetName="UTF-8";
                if(args){
                    charsetName=args.First();
                }
                return mb_Util.saveTxt(path,content,charsetName);
            },
            "lib-path":function(args){
                return S_Root+args.First();  
            },
            /**
             * 返回类型
             * String//type类型
             */
            Java_type:function(args){
                return Java.type(args.First());
            },
            /**
             * 创建实例
             * type//Java_type返回的类型，或类型名
             * [params]//构造参数
             * [kvs]//重载
             */
            Java_new:function(args){
                var Class=args.First();
                if(typeof(Class)=='string'){
                    Class=Java.type(Class);
                }
                args=args.Rest();
                if(args){
                    //有参
                    var params=args.First();
                    args=args.Rest();
                    if(args){
                        //重写
                        var kvs=args.First();
                        var os=lib.s.o_from_kvs(kvs);
                        var o=mb.Object.map(os,function(v,k){
                            return function(){
                                var r=null;
                                for(var i=arguments.length-1;i>-1;i--){
                                    r=lib.s.extend(arguments[i],r);
                                }
                                return v.exec(r);
                            }
                        });
                        Class=Java.extend(Class,o);
                    }
                    var ps=[];
                    return eval("new Class"+exec(params,ps,"ps"));
                }else{
                    //无参
                    params=[];
                    return new Class();
                }
            },
            /**
             * 兼容实例方法与静态方法
             * Object:obj
             * String:method
             * []:params
             */
            Java_call:function(args){
                var obj=args.First();
                args=args.Rest();
                var method=args.First();
                args=args.Rest();
                if(args){
                    //有参
                    var params=args.First();
                    var ps=[];
                    return eval("obj."+method+exec(params,ps,"ps"));
                }else{
                    //无参
                    return obj[method]();
                }
            },
            /**
             * 读写属性
             * Object
             * key
             * value
             */
            Java_attr:function(args){
                var obj=args.First();
                args=args.Rest();
                var key=args.First();
                args=args.Rest();
                if(args){
                    //设值
                    obj[key]=args.First();
                    return null;
                }else{
                    //取值
                    return obj[key];
                }
            },
            apply:function(args){
                var func=args.First();
                var args=args.Rest().First();
                return func.exec(args);
            }
        };
        var exec=function(params,ps,ps_name){
            var i=0;
            var str="(";
            for(var t=params;t!=null;t=t.Rest()){
                var v=t.First();
                ps.push(v);
                str=str+ps_name+"["+i+"],";
                i++;
            }
            if(i!=0){
                str=str.substr(0,str.length-1);
            }
            str=str+");";
            return str;
        };
        var buildFunc=function(k,v){
            if(typeof(v)=='function'){
                return mb.Java_new(Fun,[],{
                    exec:v,
                    toString:function(){
                        return k;
                    },
                    ftype:function(){
                        return Fun.Type.buildIn;
                    }
                });
            }else{
                return v;
            }
        };
        var r=lib.System({
            log:log_factory(function(v){
                System.out.print(v);
            }),
            isList:function(n){
                return (n instanceof Node);
            },
            isFun:function(f){
                return (f instanceof Fun);
            },
            toString:toString,
            Fun:Fun
        });
        mb.Object.forEach(library,function(v,k){
            r=lib.s.kvs_extend(k,buildFunc(k,v),r);
        });
        var library=r;
        r=lib.s.kvs_extend("parse",new Parse(r,line_split),r);
        r=lib.s.kvs_extend("base-scope",r,r);
        r=lib.s.kvs_extend(
            "cache",
            buildFunc("cache",function(args){
                var v=args.First();
                /*cache返回这个函数是特殊的*/
                return mb.Java_new(Fun,[],{
                    exec:function(args){
	                    if(args==null){
	                        return v;
	                    }else{
	                        v=args.First();
	                    }
	                },
                    toString:function(){
                        return "[]";
                    },
                    ftype:function(){
                        return Fun.Type.cache;
                    }
                });
            }),
        r);
        
        return {
            library:r,
            buildFunc:buildFunc,
            log_factory:log_factory,
            S_Root:S_Root,
            toString:toString
        };
    }
});
;
   return {lib:lib,body:body};
};
cache["util/back-s-lisp/package.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("util/back-s-lisp/package.js",url);};
   var body=(function(){
	return {
		dependences:{
		}
	};
})()
;
   return {lib:lib,body:body};
};
cache["util/back-s-lisp/s.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("util/back-s-lisp/s.js",url);};
   var body=({
    delay:true,
    success:function(){
        var Node=Java.type("s.Node");
        return {
            reverse:function(v){
                return Node.reverse(v);
	        },
            o_from_kvs:function(kvs){
                var o={};
                for(var t=kvs;t!=null;t=t.Rest()){
                    var key=t.First();
                    t=t.Rest();
                    var value=t.First();
                    o[key]=value;
                }
                return o;
            },
            kvs_from_o:function(o){
                var r=null;
                mb.Object.forEach(o,function(v,k){
                    r=Node.kvs_extend(k,v,r);
                });
                return r;
            },
            kvs_find1st:function(kvs,key){
                return Node.kvs_find1st(kvs,key);
            },
            kvs_extend:function(k,v,scope){
	            return Node.kvs_extend(k,v,scope);
	        },
            extend:function(v,vs){
                return Node.extend(v,vs);
            },
            list:function(){
                var r=null;
                for(var i=arguments.length-1;i>-1;i--){
                    r=Node.extend(arguments[i],r);
                }
                return r;
            }
        }
    }
});
;
   return {lib:lib,body:body};
};
cache["util/back-s-lisp/System.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("util/back-s-lisp/System.js",url);};
   var body=
({
	data:{
		s:"./s.js"
	},
	success:function(p){
		var Fun=p.Fun;
		
        var or=function(a,b){
            return a||b;
        };
        var and=function(a,b){
            return a&&b;
        };
        var reduce=function(args,func,init) {
            for(var t=args;t!=null;t=t.Rest()){
                init=func(init,t.First());
            }
            return init;
        };
        var compare=function(args,check,func){
            var last=args.First();
            var init=true;
            check(last);
            //mb.log(args.toString())
            for(var t=args.Rest();t!=null;t=t.Rest()){
                var now=t.First();
                check(now);
                init=and(init,func(last,now));
                last=now;
            }
            return init;
        };
        var check_is_number=function(s){
            if(s==0){
                return true;
            }else
            if(s && s.constructor==Number){
                return true;
            }else{
                mb.log(s+"不是合法的数字类型"+s.constructor);
                return false;
            }
        };
        var kvs_path=function(kvs,paths){
            var value=null;
            while(paths!=null){
                var path=paths.First();
                value=lib.s.kvs_find1st(kvs,path);
                paths=paths.Rest();
                kvs=value;
            }
            return value;
        };
        var eq=function(args,check){
            //可用于数字，字符串，实体
            return compare(args,check,function(last,now){
                return (last==now);
            });
        };
		
		var FirstFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var v=args.First();
                return v.First();
            
				},
				toString:function(){
					return "first";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var RestFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var v=args.First();
                return v.Rest();
            
				},
				toString:function(){
					return "rest";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var ExtendFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return lib.s.extend(args.First(),args.Rest().First());
            
				},
				toString:function(){
					return "extend";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var LengthFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return args.First().Length();
            
				},
				toString:function(){
					return "length";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var IsemptyFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return (args.First()==null);
            
				},
				toString:function(){
					return "empty?";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var IsexistFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return (args.First()!=null);
            
				},
				toString:function(){
					return "exist?";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var IfFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return IfFun.base_run(args);
            
				},
				toString:function(){
					return "if";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		
            IfFun.base_run=function(args){
                if(args.First()){
                    return args.Rest().First();
                }else{
                    args=args.Rest().Rest();
                    if(args){
                        return args.First();
                    }else{
                        return null;
                    }
                }
            };
            ;
						
		var EqFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return eq(args,function(){return true;});
            
				},
				toString:function(){
					return "eq";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var ApplyFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var run=args.First();
                args=args.Rest();
                return run.exec(args.First());
            
				},
				toString:function(){
					return "apply";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var LogFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                p.log(args);
            
				},
				toString:function(){
					return "log";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var ToStringFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var b=args.First();
                return p.toString(b,false);
            
				},
				toString:function(){
					return "toString";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var StringifyFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var b=args.First();  
                return p.toString(b,true);
            
				},
				toString:function(){
					return "stringify";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var TypeFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var n=args.First();
                return TypeFun.base_run(n);
            
				},
				toString:function(){
					return "type";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		
                TypeFun.base_run=function(n){
                    if(n==null){
                        return "list";
                    }else{
                        if(p.isList(n)){
                            return "list";
                        }else
                        if(p.isFun(n)){
                            return "function";
                        }else{
                            var t=typeof(n);
                            if(t=="string"){
                                return "string";
                            }else
                            if(t=="boolean"){
                                return "bool";
                            }else
                            if(t=="number"){
                                if(n%1===0){
                                    return "int";
                                }else{
                                    return "float";
                                }
                            }else{
                                return t;
                            }
                        }
                    }
                }
            ;
						
		var AddFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return reduce(args,function(last,now){
                    return last+now;
                },0);
            
				},
				toString:function(){
					return "+";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var SubFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var r=args.First();
                return reduce(args.Rest(),function(last,now){
                    return last-now;
                },r);
            
				},
				toString:function(){
					return "-";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var MultiFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return reduce(args,function(last,now){
                    return last*now;
                },1);
            
				},
				toString:function(){
					return "*";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var DivFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var r=args.First();
                return reduce(args.Rest(),function(last,now){
                    return last/now;
                },r);
            
				},
				toString:function(){
					return "/";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var ParseIntFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return parseInt(args.First());
            
				},
				toString:function(){
					return "parseInt";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var MBiggerFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                //数字
                return compare(args,check_is_number,function(last,now){
                    return (last>now);
                });
            
				},
				toString:function(){
					return ">";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var MSmallerFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                //数字
                return compare(args,check_is_number,function(last,now){
                    return (last<now);
                });
            
				},
				toString:function(){
					return "<";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var MEqFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return MEqFun.base_run(args);
            
				},
				toString:function(){
					return "=";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		
                MEqFun.base_run=function(args){
                    return eq(args,check_is_number);
                }
            ;
						
		var AndFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return reduce(args,function(init,v) {
                    return and(init,v);
                },true);
            
				},
				toString:function(){
					return "and";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var OrFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return reduce(args,function(init,v) {
                    return or(init,v);
                },false);
            
				},
				toString:function(){
					return "or";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var NotFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return !args.First();
            
				},
				toString:function(){
					return "not";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_eqFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return eq(args,function(s){
                    if(s && s.constructor==String){
                        return true;
                    }else{
                        return false;
                    }
                });
            
				},
				toString:function(){
					return "str-eq";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_lengthFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var str=args.First();
                return str.length;
            
				},
				toString:function(){
					return "str-length";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_charAtFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var str=args.First();
                args=args.Rest();
                var index=args.First();  
                return str[index];
            
				},
				toString:function(){
					return "str-charAt";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_substrFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var a=args.First();
                args=args.Rest();
                var begin=args.First();
                args=args.Rest();
                if(args==null){
                    return a.substr(begin);
                }else{
                    return a.substr(begin,args.First());
                }
            
				},
				toString:function(){
					return "str-substr";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_joinFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                //字符串
                var array=args.First();
                var split="";
                if(args.Rest()!=null){
                    split=args.Rest().First();
                }
                var r="";
                for(var t=array;t!=null;t=t.Rest()){
                    r=r+t.First()+split;
                }
                return r.substr(0,r.length-split.length);
            
				},
				toString:function(){
					return "str-join";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_splitFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var a=args.First();
                var split="";
                args=args.Rest()
                if(args!=null){
                    split=args.First();
                }
                return a.split(split);
            
				},
				toString:function(){
					return "str-split";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_upperFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return args.First().toUpperCase();
            
				},
				toString:function(){
					return "str-upper";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_lowerFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return args.First().toLowerCase();
            
				},
				toString:function(){
					return "str-lower";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_trimFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var str=args.First();
                return str.trim();
            
				},
				toString:function(){
					return "str-trim";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_indexOfFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.indexOf(v);
            
				},
				toString:function(){
					return "str-indexOf";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_lastIndexOfFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.lastIndexOf(v);
            
				},
				toString:function(){
					return "str-lastIndexOf";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_startsWithFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.startsWith(v);
            
				},
				toString:function(){
					return "str-startsWith";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var Str_endsWithFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.endsWith(v);
            
				},
				toString:function(){
					return "str-endsWith";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var QuoteFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return args.First();
            
				},
				toString:function(){
					return "{(first args ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var ListFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return args;
            
				},
				toString:function(){
					return "{args }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var Kvs_find1stFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var kvs=args.First();
                args=args.Rest();
                var key=args.First();
                return lib.s.kvs_find1st(kvs,key);
            
				},
				toString:function(){
					return "{(let (key kvs ) args find1st this ) (let (k v ...kvs ) args ) (if-run (str-eq k key ) {v } {(find1st key kvs ) } ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var Kvs_extendFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var key=args.First();
                args=args.Rest();
                var value=args.First();
                args=args.Rest();
                var kvs=args.First();
                return lib.s.kvs_extend(key,value,kvs);
            
				},
				toString:function(){
					return "{(let (k v kvs ) args ) (extend k (extend v kvs ) ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var IstypeFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var x=args.First();
                args=args.Rest();
                var n=args.First();
                return (TypeFun.base_run(x)==n);
            
				},
				toString:function(){
					return "{(let (x n ) args ) (str-eq (type x ) n ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var CallFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var run=args.First();
                args=args.Rest();
                return run.exec(args);
            
				},
				toString:function(){
					return "call";
				},
				ftype:function(){
					return Fun.Type.buildIn;
				}
			});
		};
		;
						
		var MNotEqFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return !MEqFun.base_run(args);
            
				},
				toString:function(){
					return "{(not (apply = args ) ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var Empty_funFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return null;
            
				},
				toString:function(){
					return "{}";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var DefaultFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var v=args.First();
                if(v!=null){
                    return v;
                }else{
                    args=args.Rest();
                    return args.First();
                }
            
				},
				toString:function(){
					return "{(let (a d ) args ) (if (exist? a ) a d ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var LenFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var list=args.First();
                if(list){
                    return list.Length();
                }else{
                    return 0;
                }
            
				},
				toString:function(){
					return "{(let (cs ) args ) (if-run (exist? cs ) {(length cs ) } {0 } ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var If_runFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var o=IfFun.base_run(args);
                if(o==null){
                    return null;
                }else{
                    return o.exec(null);
                }
            
				},
				toString:function(){
					return "{(let (a b c ) args ) (let x (default (if a b c ) ) ) (x ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var LoopFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var f=args.First();
                args=args.Rest();
                var will=true;
                while(will){
                    args=f.exec(args);
                    will=args.First();
                    args=args.Rest();
                }
                return args;
            
				},
				toString:function(){
					return "{(let (f ...init ) args loop this ) (let (will ...init ) (apply f init ) ) (if-run will {(apply loop (extend f init ) ) } {init } ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var ReverseFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return ReverseFun.base_run(args.First());
            
				},
				toString:function(){
					return "{(let (xs ) args ) (reduce xs {(let (init x ) args ) (extend x init ) } [] ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		
            ReverseFun.base_run=function(list){
                return lib.s.reverse(list);
            };
            ;
						
		var Kvs_reverseFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                return Kvs_reverseFun.base_run(args.First());
            
				},
				toString:function(){
					return "{(let (kvs ) args ) (kvs-reduce kvs {(let (init v k ) args ) (kvs-extend k v init ) } [] ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		
            Kvs_reverseFun.base_run=function(kvs){
                var r=null;
                var tmp=kvs;
                while(tmp!=null){
                    var key=tmp.First();
                    tmp=tmp.Rest();
                    var value=tmp.First();
                    tmp=tmp.Rest();
                    r=lib.s.kvs_extend(key,value,r);
                }
                return r;
            };
            ;
						
		var ReduceFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var list=args.First();
                args=args.Rest();
                return ReduceFun.base_run(list,args);
            
				},
				toString:function(){
					return "{(let (xs run init ) args reduce this ) (if-run (exist? xs ) {(let (x ...xs ) xs ) (let init (run init x ) ) (reduce xs run init ) } {init } ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		
            ReduceFun.base_run=function(list,args){
                var f=args.First();
                args=args.Rest();
                var init=args.First();
                while(list!=null){
                    var x=list.First();
                    list=list.Rest();
                    var nargs=lib.s.list(init,x);
                    init=f.exec(nargs);
                }
                return init;
            }
            ;
						
		var Reduce_rightFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var list=args.First();
                list=ReverseFun.base_run(list);
                args = args.Rest();
                return ReduceFun.base_run(list,args);
            
				},
				toString:function(){
					return "{(let (xs run init ) args reduce-right this ) (if-run (exist? xs ) {(let (x ...xs ) xs ) (run (reduce-right xs run init ) x ) } {init } ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var Kvs_reduceFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var kvs=args.First();
                args=args.Rest();
                return Kvs_reduceFun.base_run(kvs,args);
            
				},
				toString:function(){
					return "{(let (kvs run init ) args kvs-reduce this ) (if-run (exist? kvs ) {(let (k v ...kvs ) kvs ) (let init (run init v k ) ) (kvs-reduce kvs run init ) } {init } ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		
            Kvs_reduceFun.base_run=function(kvs,args){
                var f=args.First();
                args=args.Rest();
                var init=args.First();
                while(kvs!=null){
                    var key=kvs.First();
                    kvs=kvs.Rest();
                    var value=kvs.First();
                    kvs=kvs.Rest();
                    var nargs=lib.s.list(init,value,key);
                    init=f.exec(nargs);
                }
                return init;
            }
            ;
						
		var Kvs_reduce_rightFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var kvs=args.First();
                kvs=Kvs_reverseFun.base_run(kvs);
                args=args.Rest();
                return Kvs_reduceFun.base_run(kvs,args);
            
				},
				toString:function(){
					return "{(let (kvs run init ) args kvs-reduce-right this ) (if-run (exist? kvs ) {(let (k v ...kvs ) kvs ) (run (kvs-reduce-right kvs run init ) v k ) } {init } ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var Kvs_pathFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var kvs=args.First();
                args=args.Rest();
                var paths=args.First();
                return kvs_path(kvs,paths);
            
				},
				toString:function(){
					return "{(let (e paths ) args kvs-path this ) (if-run (exist? paths ) {(let (path ...paths ) paths ) (kvs-path (kvs-find1st e path ) paths ) } {e } ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var Kvs_path_runFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var kvs=args.First();
                args=args.Rest();
                var paths=args.First();
                args=args.Rest();
                return kvs_path(kvs,paths).exec(args);
            
				},
				toString:function(){
					return "{(let (e paths ...ps ) args ) (apply (kvs-path e paths ) ps ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var PipFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
				var o=args.First();
				args=args.Rest();
				while(args!=null){
					var f=args.First();
					args=args.Rest();
					o=f.exec(lib.s.list(o));
				}
				return o;
			
				},
				toString:function(){
					return "{(first (apply loop (extend {(let (x f ...xs ) args ) (if-run (empty? xs ) {(list false (f x ) ) } {(extend true (extend (f x ) xs ) ) } ) } args ) ) ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var Slice_fromFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
            var list=args.First();
            args=args.Rest();
            var i=args.First();
            return Slice_fromFun.base_run(list,i);
            
				},
				toString:function(){
					return "{(let (list i ) args offset this ) (if-run (= i 0 ) {list } {(offset (rest list ) (- i 1 ) ) } ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		
            Slice_fromFun.base_run=function(list,i){
                while(i!=0){
                    list=list.Rest();
                    i--;
                }
                return list;
            }
            ;
						
		var Slice_toFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var list=args.First();
                args=args.Rest();
                var i=args.First();
                var r=null;
                while(i!=0){
                    r=lib.s.extend(list.First(),r);
                    list=list.Rest();
                    i--;
                }
                return ReverseFun.base_run(r);
            
				},
				toString:function(){
					return "{(let (xs to ) args slice-to this ) (if-run (= to 0 ) {[] } {(let (x ...xs ) xs ) (extend x (slice-to xs (- to 1 ) ) ) } ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var OffsetFun=function(){
			return mb.Java_new(Fun,[],{
				exec:function(args){
	                
                var list=args.First();
                args=args.Rest();
                var i=args.First();
                return Slice_fromFun.base_run(list,i).First()
            
				},
				toString:function(){
					return "{(first (apply slice-from args ) ) }";
				},
				ftype:function(){
					return Fun.Type.user;
				}
			});
		};
		;
						
		var m=null;
		m=lib.s.kvs_extend("true",true,m);
		m=lib.s.kvs_extend("false",false,m);
		
		m=lib.s.kvs_extend("first",FirstFun(),m);
						
		m=lib.s.kvs_extend("rest",RestFun(),m);
						
		m=lib.s.kvs_extend("extend",ExtendFun(),m);
						
		m=lib.s.kvs_extend("length",LengthFun(),m);
						
		m=lib.s.kvs_extend("empty?",IsemptyFun(),m);
						
		m=lib.s.kvs_extend("exist?",IsexistFun(),m);
						
		m=lib.s.kvs_extend("if",IfFun(),m);
						
		m=lib.s.kvs_extend("eq",EqFun(),m);
						
		m=lib.s.kvs_extend("apply",ApplyFun(),m);
						
		m=lib.s.kvs_extend("log",LogFun(),m);
						
		m=lib.s.kvs_extend("toString",ToStringFun(),m);
						
		m=lib.s.kvs_extend("stringify",StringifyFun(),m);
						
		m=lib.s.kvs_extend("type",TypeFun(),m);
						
		m=lib.s.kvs_extend("+",AddFun(),m);
						
		m=lib.s.kvs_extend("-",SubFun(),m);
						
		m=lib.s.kvs_extend("*",MultiFun(),m);
						
		m=lib.s.kvs_extend("/",DivFun(),m);
						
		m=lib.s.kvs_extend("parseInt",ParseIntFun(),m);
						
		m=lib.s.kvs_extend(">",MBiggerFun(),m);
						
		m=lib.s.kvs_extend("<",MSmallerFun(),m);
						
		m=lib.s.kvs_extend("=",MEqFun(),m);
						
		m=lib.s.kvs_extend("and",AndFun(),m);
						
		m=lib.s.kvs_extend("or",OrFun(),m);
						
		m=lib.s.kvs_extend("not",NotFun(),m);
						
		m=lib.s.kvs_extend("str-eq",Str_eqFun(),m);
						
		m=lib.s.kvs_extend("str-length",Str_lengthFun(),m);
						
		m=lib.s.kvs_extend("str-charAt",Str_charAtFun(),m);
						
		m=lib.s.kvs_extend("str-substr",Str_substrFun(),m);
						
		m=lib.s.kvs_extend("str-join",Str_joinFun(),m);
						
		m=lib.s.kvs_extend("str-split",Str_splitFun(),m);
						
		m=lib.s.kvs_extend("str-upper",Str_upperFun(),m);
						
		m=lib.s.kvs_extend("str-lower",Str_lowerFun(),m);
						
		m=lib.s.kvs_extend("str-trim",Str_trimFun(),m);
						
		m=lib.s.kvs_extend("str-indexOf",Str_indexOfFun(),m);
						
		m=lib.s.kvs_extend("str-lastIndexOf",Str_lastIndexOfFun(),m);
						
		m=lib.s.kvs_extend("str-startsWith",Str_startsWithFun(),m);
						
		m=lib.s.kvs_extend("str-endsWith",Str_endsWithFun(),m);
						
		m=lib.s.kvs_extend("quote",QuoteFun(),m);
						
		m=lib.s.kvs_extend("list",ListFun(),m);
						
		m=lib.s.kvs_extend("kvs-find1st",Kvs_find1stFun(),m);
						
		m=lib.s.kvs_extend("kvs-extend",Kvs_extendFun(),m);
						
		m=lib.s.kvs_extend("type?",IstypeFun(),m);
						
		m=lib.s.kvs_extend("call",CallFun(),m);
						
		m=lib.s.kvs_extend("!=",MNotEqFun(),m);
						
		m=lib.s.kvs_extend("empty-fun",Empty_funFun(),m);
						
		m=lib.s.kvs_extend("default",DefaultFun(),m);
						
		m=lib.s.kvs_extend("len",LenFun(),m);
						
		m=lib.s.kvs_extend("if-run",If_runFun(),m);
						
		m=lib.s.kvs_extend("loop",LoopFun(),m);
						
		m=lib.s.kvs_extend("reverse",ReverseFun(),m);
						
		m=lib.s.kvs_extend("kvs-reverse",Kvs_reverseFun(),m);
						
		m=lib.s.kvs_extend("reduce",ReduceFun(),m);
						
		m=lib.s.kvs_extend("reduce-right",Reduce_rightFun(),m);
						
		m=lib.s.kvs_extend("kvs-reduce",Kvs_reduceFun(),m);
						
		m=lib.s.kvs_extend("kvs-reduce-right",Kvs_reduce_rightFun(),m);
						
		m=lib.s.kvs_extend("kvs-path",Kvs_pathFun(),m);
						
		m=lib.s.kvs_extend("kvs-path-run",Kvs_path_runFun(),m);
						
		m=lib.s.kvs_extend("pip",PipFun(),m);
						
		m=lib.s.kvs_extend("slice-from",Slice_fromFun(),m);
						
		m=lib.s.kvs_extend("slice-to",Slice_toFun(),m);
						
		m=lib.s.kvs_extend("offset",OffsetFun(),m);
						
		return m;
	}
});
								
;
   return {lib:lib,body:body};
};
cache["util/package.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("util/package.js",url);};
   var body=(function(){
	return {
		dependences:{
            "back-lib-map":1,
            "back-s-lisp":1
		}
	};
})()
;
   return {lib:lib,body:body};
};
cache["util/shell.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("util/shell.js",url);};
   var body=({
    success:function(p){
        var Scanner=Java.type("java.util.Scanner");
        var System=Java.type("java.lang.System");
        var console=new Scanner(System["in"]);
        var shell=p.shell(function(v){
            System.out.print(v);
        });
        var in_sym="<=";
        var out_sym="=>";
        var circle=function(){
            var str_in="";
            System.out.print(in_sym);
            var tmp=console.nextLine();
            if(tmp==p.end){
                //多行
                tmp="";
	            while(tmp!=p.end){
	                tmp=console.nextLine();
	                str_in+=tmp+"\n";
	            }
            }else{
                //单行
                str_in=tmp;
            }
            if(str_in!="exit"){
	            var str_out="";
	            try{
	                var obj=shell(str_in,"\n");
	                str_out=p.toString(obj);
	            }catch(e){
	                if(e.getMessage){
	                    str_out=e.getMessage();
	                }else{
	                    str_out=e.toString();
	                }
	            }
	            System.out.println(out_sym+str_out);
	            System.out.println("");
	            circle();
            }
        };
        return circle;
    }
})
;
   return {lib:lib,body:body};
};
cache["ext/index.js"]=function(){
   var lib={};
   var pathOf=function(url){return mb.load.pathOf("ext/index.js",url);};
   var body=({
    success:function(){
    
    }
});
;
   return {lib:lib,body:body};
};
mb.getLib=function(path,deal){
                return mb.load(path,deal,function(url){
                    var v=cache[url];
                    if(v){
                        return v();
                    }else{
                        return null;
                    }
                });
            };mb.getLib("ext/index.js")(mb);
(function(){
            return mb.Java_new("mb.JSBridge.JSMethod",[],{
                run:function(map){
                    var url="act/"+map.get("type")+"/index.js";
                    var act=mb.getLib(url);
                    if(act!=null){
                        act(map);
                    }else{
                        mb.log("未找到处理模块！"+url);
                    }
                }
            });
        }());