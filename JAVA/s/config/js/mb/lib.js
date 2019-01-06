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