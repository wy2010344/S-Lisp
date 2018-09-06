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
            o={
                success:pkg.body.delay?pkg.body.success():pkg.body.success,
                out:pkg.body.out
            };
            cache[path]=o;
            mb.Object.forEach(pkg.body.data,function(v,k){
	           var r;
	           if(typeof(v)=='string'){
	                var url;
	                if(v[0]=='.'){
	                    url=mb.load.calAbsolutePath(path,v);
	                }else{
	                    url=v;
	                }
                    r=load(url,false,getFun);
	           }else
	           if(typeof(v)=='object'){
	                var url=mb.load.calAbsolutePath(path,v.url);
	                if(typeof(v.type)=='string'){
	                    if(v.type=="path"){
	                        r=mb.load.path(url);
	                    }else
	                    if(v.type=="file"){
	                        r=mb.load.file(url);
	                    }else
	                    if(v.type=="text"){
	                        r=mb.load.text(url);
	                    }else{
	                        throw "未支持类型"+v;
	                        r=url;
	                    }
	                }else{
	                    /*本来是同步的，可以加载*/
	                    v.type({
	                        url:url,
	                        notice:function(success){
	                            r=success;
	                        }
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
        if(servlet){
            if(o.out){
                return o.success;
            }else{
                return null;
            }
        }else{
            return o.success;
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
                    return cache[url]();
                });
            };
       }else{
           //不打包文件
           getLib=function(path,servlet){
	           return mb.load(path,servlet,function(url){
                    var lib={};
                    var body=eval(mb.load.text(url));
                    return{
                        lib:lib,
                        body:body
                    };
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