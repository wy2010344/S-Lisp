({
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
        
        
        var reduce=function(args,init,func){
            for(var t=args;t!=null;t=t.Rest()){
                init=func(init,t.First());
            }
            return init;
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
        var compare=function(args,func){
            var last=args.First();
            var init=true;
            for(var t=args.Rest();t!=null;t=t.Rest()){
                var now=t.First();
                init=and(init,func(last,now));
                last=now;
            }
            return init;
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
		var library={
            "false":false,
            "true":true,
            log:log_factory(function(v){
                System.out.print(v);
            }),
            reverse:function(args){
                var v=args.First();
                return lib.s.reverse(args.First());
            },
            rest:function(args){
                var v=args.First();
                return v.Rest();
            },
            first:function(args){
                var v=args.First();
                return v.First();
            },
            /*主要用于用闭包构建参数*/
            list:function(args){
                return args;
            },
            "empty?":function(args){
                return (args.First()==null);
            },
            "exist?":function(args) {
                return (args.First()!=null);
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
                return compare(args,function(last,now){
                    return (last==now);
                });
            },
            length:function(args){
                return args.First().Length();
            },
            extend:function(args){
            	return lib.s.extend(args.First(),args.Rest().First());
            },
			quote:function(args){
				return args.First();
			},
            "parseInt":function(args){
                return parseInt(args.First());
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
            "str-charAt":function(args){
                var str=args.First();
                args=args.Rest();
                var index=args.First();
                return str.charAt(index);
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
            toString:function(args){
                return args.First().toString();  
            },
            stringify:function(args){
                //类似于JSON.stringify，没想好用toString还是stringify;
                return args.First().toString();  
            },
            "str-trim":function(args) {
                var str=args.First();
                return str.trim();
            },
            "str-length":function(args) {
                var str=args.First();
                return str.length;
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
            ">":function(args){
                //数字
                return compare(args,function(last,now){
                    return (last>now);
                });
            },
            "<":function(args){
                //数字
                return compare(args,function(last,now){
                    return (last<now);
                });
            },
            "=":function(args){
                //可用于数字，字符串
                return compare(args,function(last,now){
                    return (last==now);
                });
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