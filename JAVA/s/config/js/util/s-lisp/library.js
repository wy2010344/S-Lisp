({
    data:{
        util:"./util.js"
    },
    success:function(){
        var Node=Java.type("s.Node");
        var Fun=Java.type("s.Function");
        var Eval=Java.type("s.Eval");
        var System=Java.type("java.lang.System");
        var mb_Util=Java.type("mb.Util");
        var reduce=function(node,init,func){
            for(var t=node;t!=null;t=t.Rest()){
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
        var reduce=function(node,func,init) {
            for(var t=node;t!=null;t=t.Rest()){
                init=func(init,t.First());
            }
            return init;
        };
        var compare=function(node,func){
            var last=node.First();
            var init=true;
            for(var t=node.Rest();t!=null;t=t.Rest()){
                var now=t.First();
                init=and(init,func(last,now));
                last=now;
            }
            return init;
        };
        var log_factory=function(append){
            return function(node){
	            for(var t=node;t!=null;t=t.Rest()){
	                append(t.First());
	                append("\t");
	            }
	            append("\n");
                return null;
            };
        };
		var library={
            "false":false,
            "true":true,
            log:log_factory(function(v){
                if(v==null){
                    v="[]";
                }
                System.out.print(v);
            }),
            reverse:function(node){
                var v=node.First();
                return lib.util.reverse(node.First());
            },
            rest:function(node){
                var v=node.First();
                return v.Rest();
            },
            first:function(node){
                var v=node.First();
                return v.First();
            },
            /*主要用于用闭包构建参数*/
            list:function(node){
                return node;
            },
            "empty?":function(node){
                return (node.First()==null);
            },
            "exist?":function(node) {
                return (node.First()!=null);
            },
            "list?":function(node){
                var n=node.First();
                return (n && (n instanceof Node));
            },
            "function?":function(node){
                var n=node.First();
                return (n && (n instanceof Fun));
            },
            length:function(node){
                return node.First().Length();
            },
            extend:function(node){
            	return lib.util.extend(node.First(),node.Rest().First());
            },
			quote:function(node){
				return node.First();
			},
            "parseInt":function(node){
                return parseInt(node.First());
            },
            "kvs-find1st":function(node){
                var kvs=node.First();
                node=node.Rest();
                var key=node.First();
                return lib.util.kvs_find1st(kvs,key);
            },
            "kvs-extend":function(node) {
                var key=node.First();
                node=node.Rest();
                var value=node.First();
                node=node.Rest();
                var kvs=node.First();
                return lib.util.kvs_extend(key,value,kvs);
            },
            //a?b:default(null)
            "if":function(node){
                if(node.First()){
                    return node.Rest().First();
                }else{
                    node=node.Rest().Rest();
                    if(node){
                        return node.First();
                    }else{
                        return null;
                    }
                }
            },
            "str-join":function(node){
                //字符串
                var array=node.First();
                var split="";
                if(node.Rest()!=null){
                    split=node.Rest().First();
                }
                var r="";
                for(var t=array;t!=null;t=t.Rest()){
                    r=r+t.First()+split;
                }
                return r.substr(0,r.length-split.length);
            },
            "char-at":function(node){
                var str=node.First();
                node=node.Rest();
                var index=node.First();
                return str.charAt(index);
            },
            "str-split":function(node){
                var str=node.First();
                node=node.Rest();
                var split=node.First();
                var array=str.split(split);
                var r=null;
                for(var i=array.length-1;i>-1;i--){
                    r=lib.util.extend(array[i],r);
                }
                return r;
            },
            "str-upper":function(node){
                return node.First().toUpperCase();
            },
            "str-lower":function(node){
                return node.First().toLowerCase();
            },
            toString:function(node){
                return node.First().toString();  
            },
            stringify:function(node){
                //类似于JSON.stringify，没想好用toString还是stringify;
                return node.First().toString();  
            },
            "str-trim":function(node) {
                var str=node.First();
                return str.trim();
            },
            "str-length":function(node) {
                var str=node.First();
                return str.length;
            },
			"+":function(node){
                return reduce(node,function(last,now){
                    return last+now;
                },0);
			},
			"-":function(node){
                var r=node.First();
                return reduce(node.Rest(),function(last,now){
                    return last-now;
                },r);
			},
            "*":function(node){
                return reduce(node,function(last,now){
                    return last*now;
                },1);
            },
            "/":function(node){
                var r=node.First();
                return reduce(node.Rest(),function(last,now){
                    return last/now;
                },r);
            },
            ">":function(node){
                //数字
                return compare(node,function(last,now){
                    return (last>now);
                });
            },
            "<":function(node){
                //数字
                return compare(node,function(last,now){
                    return (last<now);
                });
            },
            "=":function(node){
                //可用于数字，字符串
                return compare(node,function(last,now){
                    return (last==now);
                });
            },
            and:function(node){
                return reduce(node,function(init,v) {
                    return and(init,v);
                },true);
            },
            or:function(node){
                return reduce(node,function(init,v) {
                    return or(init,v);
                },false);
            },
            not:function(node){
                return !node.First();
            },
            read:function(node){
                var path=node.First();
                node=node.Rest();
                var lineSplit="\n";
                var charsetName="UTF-8";
                if(node){
                    lineSplit=node.First()||lineSplit;
                    node=node.Rest();
                    if(node){
                        charsetName=node.First()||charsetName;
                    }
                }
                return mb_Util.readTxt(path,lineSplit,charsetName);  
            },
            write:function(node){
                var path=node.First();
                node=node.Rest();
                var content=node.First();
                node=node.Rest();
                var charsetName="UTF-8";
                if(node){
                    charsetName=node.First();
                }
                return mb_Util.saveTxt(path,content,charsetName);
            },
            /**
             * 返回类型
             * String//type类型
             */
            Java_type:function(node){
                return Java.type(node.First());
            },
            /**
             * 创建实例
             * type//Java_type返回的类型，或类型名
             * [params]//构造参数
             * [kvs]//重载
             */
            Java_new:function(node){
                var Class=node.First();
                if(typeof(Class)=='string'){
                    Class=Java.type(Class);
                }
                node=node.Rest();
                if(node){
                    //有参
                    var params=node.First();
                    node=node.Rest();
                    if(node){
                        //重写
                        var kvs=node.First();
                        var os=lib.util.o_from_kvs(kvs);
                        var o=mb.Object.map(os,function(v,k){
                            return function(){
                                var r=null;
                                for(var i=arguments.length-1;i>-1;i--){
                                    r=new Node(arguments[i],r);
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
            Java_call:function(node){
                var obj=node.First();
                node=node.Rest();
                var method=node.First();
                node=node.Rest();
                if(node){
                    //有参
                    var params=node.First();
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
            Java_attr:function(node){
                var obj=node.First();
                node=node.Rest();
                var key=node.First();
                node=node.Rest();
                if(node){
                    //设值
                    obj[key]=node.First();
                    return null;
                }else{
                    //取值
                    return obj[key];
                }
            },
            apply:function(node){
                var func=node.First();
                var args=node.Rest().First();
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
                    }
                });
            }else{
                return v;
            }
        };
        var r=null;
        mb.Object.forEach(library,function(v,k){
            r=lib.util.kvs_extend(k,buildFunc(k,v),r);
        });
        var library=r;
        r=lib.util.kvs_extend(
            "parse",
            buildFunc("parse",function(node){
	            var str=node.First();
	            node=node.Rest();
	            if(node){
	                var scope=node.First();
	            }else{
	                scope=library;
	            }
	            return Eval.run(str,scope,'\n');
	        }),
        r);
        r=lib.util.kvs_extend("library",r,r);
        r=lib.util.kvs_extend(
            "cache",
            buildFunc("cache",function(node){
                var v=node.First();
                return buildFunc(null,function(node){
                    if(node==null){
                        return v;
                    }else{
                        v=node.First();
                    }
                });
            }),
        r);
        
        return {
            library:r,
            buildFunc:buildFunc,
            log_factory:log_factory
        };
    }
});