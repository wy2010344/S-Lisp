({
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
        mb.log(typeof(S_Root),S_Root);
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
            return v;
        };
        var log_factory=function(append){
            return function(args){
                for(var t=args;t!=null;t=t.Rest()){
                    append(toString(t.First()));
                    append("\t");
                }
                append("\n");
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