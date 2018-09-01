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
    

    /*计算相对路径*/
    var calAbsolutePath=function(base_url,url){
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
    
    var pkgLoader=function(path){
        //需要与单个加载的保持一致
        var me={
            relative_to_absolute:function(relative){
                return mb.load.path(mb.load.calAbsolutePath(path,relative));
            },
            current_act:function(){
                return path;  
            },
            load_file:function(relative){
                return mb.load.file(mb.load.calAbsolutePath(path,relative));
            },
            log:mb.log.loadder(path)
        };
        return me;
    };
    return {
	    text:function(path){
	        return me.readTxt(base_path+path.replace(/>/g,sp));
	    },
        /**
         * 返回JAVA的File实例，传入js为根的任何路径。
         */
	    file:function(path){
	        return me.fileFromPath(base_path+sp+path+sp)
	    },
        path:function(path){
            return base_path+sp+path;
        },
        calAbsolutePath:calAbsolutePath,
        pkgLoader:pkgLoader,
	    require:(function(){
        var fakemap=function(){
            var _c={};
            return {
                get:function(key){
	                 return _c[key];
	            },
	            put:function(key,value){
	                 _c[key]=value;
	            },
                clear:function(){
                    _c={};
                }
	        };
	    };
	    var core=fakemap();
		//var core=ini.get("js_cache");
		/**
		 * data
		 * success
		 */
		var ret=function(p){
			var me={
				retData:null,//注入
				url:null,//注入
				init:function(){
					if(p.data){
						mb.Object.forEach(p.data, function(value,key){
							me.retData[key]=ret.sync(value,me.url)();
						});
					}
				},
                delay:p.delay
			};
            if(typeof(p.success)=='function'){
                me.success=function(){
                    try{
                        return p.success.apply(null,arguments);
                    }catch(e){
                        mb.log("出错:",me.url);
                        throw e;
                    }
                };
            }else{
                me.success=p.success;
            }
			return me;
		};
        ret.core=core;
		ret.sync=function(value,base_url){
            if(value[0]=='.'){
                value=calAbsolutePath(base_url,value);
            }
			var vk=core.get(value);
			if(!vk){
				//mb.log("new:"+value);
				var txt=mb.load.text(value);
                if(txt==""){
                    return null;
                }else{
					var lib={};
                    //pkg与打包的要对称
                    var pkg=pkgLoader(value);
					var cvk=eval("mb.load.require"+txt);
					cvk.retData=lib;
					cvk.url=value;
					cvk.init();
                    var s;
                    if(cvk.delay){
                        s=cvk.success();
                    }else{
                        s=cvk.success;
                    }
                    /**与打包成一个文件的情况兼容*/
                    vk=function(){
                        return s;
                    };
					core.put(value,vk);
                }
			};
			return vk;
	    };
	    return ret;
	    })()
	};
})();
mb.compile=(function(){
    //importPackage(java.io);
    var sp=""+ini.get("file_sp");
    var base_path=""+ini.get("server_path");
    
    var escapeStr=function(str){
        return "\""+str.replace(/"/g,"\\\"")+"\"";
    };
    var stringify=function(p){
        var tp=typeof(p);
        if(tp=='function' || tp=="boolean" || tp=="number"){
            return p.toString().trim();
        }else
        if(p==null)
        {
            return "null";
        }else
        if(tp=="string"){
            return escapeStr(p);
        }else
        if(p instanceof Array){
            var ret=[];
            for(var i=0;i<p.length;i++){
                ret.push(stringify(p[i]));
            }
            return '['+ret.join(',')+']';
        }else
        {
            //object
            var ret=[];
            for(var k in p){
                ret.push(escapeStr(k)+":"+stringify(p[k]));
            }
            return '{'+ret.join(',')+'}';
        }
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
        var require=function(p){
            return p;
        };
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
                        root[childName.replace(regx,'/')]="require"+loadTxt(child);
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
            return  ["(function(){",
                    "var calAbsolutePath=mb.load.calAbsolutePath;",
                    "var pkgLoader=mb.load.pkgLoader;",
                    "var xload="+(function(obj){
                        var ret={};
                        for(var key in obj){
                            ret[key]=core[obj[key]]();
                        }
                        return ret;
                    }).toString(),
                    "  var cache={};",
                    "  var core={};",
                    "  var servlets={};",
                    (function(){
                            var core=[];
                            for(var key in root){
                            	try{
                            		var obj=eval(root[key]);
                            	}catch(ex){
                            		mb.log("出错："+key);
                            		mb.log(mb.log.stringifyError(ex));
                            	}
                                var escapeKey=escapeStr(key);
                                var newFunc=["function(){ ",
                                			"    var  ret=cache["+escapeKey+"];", //缓存一下，只执行一次
                                			"    if(!ret){",
                                            "    var pkg=pkgLoader("+escapeKey+");",
                                            "    var lib=xload("+JSON.stringify(
                                            		mb.Object.map(obj.data||{},function(val){
                                            			val=val.trim();
                                            			if(val[0]=='.'){
                                            				val=mb.load.calAbsolutePath(key,val);
                                            			}
                                                        if(!root[val]){
                                                            mb.log("未找到\""+val+"\"在"+escapeKey);
                                                        }
                                                        return val;
                                            		})
                                            ,"",2)+");",
                                            "    ret="+stringify(obj.success).trim()+(obj.delay?"()":"")+";",
                                            "		 cache["+escapeKey+"]=ret;",
                                            "    }",
                                            "	 return ret;",
                                            "};"].join('\r\n');
                                core.push("core["+escapeKey+"]="+newFunc);
                                if(obj.out){
                                    //如果有out标识，可供servlet访问
                                    core.push("servlets["+escapeKey+"]=core["+escapeKey+"];");
                                }
                            }
                            return core.join(";\r\n");
                    })(),
                    "  return {coreData:core,servlets:servlets};",
                    "})()"].join('\r\n');
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
        if(ini.get("package")){
            //打包成一个文件
            var rqs=loadRequire(
                "act"+sp,
                "util"+sp,
                "ext"+sp);
            ret.push("(");
            ret.push(
                (function(xp){/*{coreData,servlets}*/
                    var getLib=function(libs,path){
                        var lib=libs[path];
                        if(lib){
                            return lib();
                        }else{
                            return null;
                        }
                  };
                  //差异库
                  getLib(xp.coreData,"ext/index.js");
                  if(ini.get("servlet")==true){
                       mb.init(function(path){return getLib(xp.servlets,path);});
                  }else{
                       mb.init(function(path){return getLib(xp.coreData,path);});
                  }
                }).toString()
           );
           ret.push(")");
           ret.push("("+rqs+")");
       }else{
           //不打包文件
           ret.push("(");
           ret.push(
               (function(){
                    var getLib=function(c_path){
			            var lib=mb.load.require.sync(c_path);
			            if(lib){
			                return lib();
			            }else{
			                return null;
			            }
			        };
                    
                    //差异库
                    getLib("ext/index.js");
                    mb.init(getLib);
               }).toString()
           );
           ret.push(")");
           ret.push("()");
        }
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