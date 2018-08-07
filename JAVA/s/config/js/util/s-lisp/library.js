({
    data:{
        util:"./util.js"
    },
    success:function(){
        var Node=Java.type("s.Node");
        var Fun=Java.type("s.Function");
        var Eval=Java.type("s.Eval");
        var System=Java.type("java.lang.System");
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
        var compare=function(node,func,is_or){
            var last=node.First();
            var init=true;
            var c=is_or?or:and;
            for(var t=node.Rest();t!=null;t=t.Rest()){
                var now=t.First();
                init=c(init,func(last,now));
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
            //"null":null,//舍弃null，内部用[]表示null，使用范围更广
            "false":false,
            "true":true,
            log:log_factory(function(v){
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
                return new Node(node.First(),node.Rest().First());
            },
			quote:function(node){
				return node.First();
			},
            "parseInt":function(node){
                return parseInt(node.First());
            },
            join:function(node){
                var array=node.First();
                var split=null;
                var has_split=false;
                if(node.Rest()!=null){
                    has_split=true;
                    split=node.Rest().First();
                }
                var r=null;
                for(var t=array;t!=null;t=t.Rest()){
                    var cs=t.First();
                    for(var x=cs;x!=null;x=x.Rest()){
                        r=new Node(x.First(),r);
                    }
                    if(has_split){
                        r=new Node(split,r);
                    }
                }
                if(has_split && ret!=null){
                    ret=ret.Rest();
                }
                return reverse(ret);
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
                return kvs_extend(key,value,kvs);
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
            toString:function(node){
                return node.First().toString();  
            },
            stringify:function(node){
                //类似于JSON.stringify，没想好用toString还是stringify;
                return node.First().toString();  
            },
			"+":function(node){
                return reduce(node,0,function(last,now){
                    return last+now;
                });
			},
			"-":function(node){
                var r=node.First();
                return reduce(node.Rest(),r,function(last,now){
                    return last-now;
                });
			},
            "*":function(node){
                return reduce(node,1,function(last,now){
                    return last*now;
                });
            },
            "/":function(node){
                var r=node.First();
                return reduce(node.Rest(),r,function(last,now){
                    return last/now;
                });
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
                return compare(node,function(last,now){
                    return (last && now);
                });
            },
            or:function(node){
                return compare(node,function(last,now){
                    return (last || now);
                },true);
            },
            not:function(node){
                return !node.First();
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
            },
            cache:function(node){
                var init=node.First();  
                return function(node){
                    if(node==null){
                        return init;
                    }else{
                        init=node.First();
                        return null;
                    }
                };
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
        var buildFunc=function(v){
            if(typeof(v)=='function'){
                return mb.Java_new(Fun,[],{exec:v});
            }else{
                return v;
            }
        };
        var kvs_extend=function(k,v,scope){
            return new Node(k,new Node(v,scope));
        };
        var r=null;
        mb.Object.forEach(library,function(v,k){
            r=kvs_extend(k,buildFunc(v),r);
        });
        var library=r;
        r=kvs_extend(
            "parse",
            buildFunc(function(node){
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
        r=kvs_extend("library",r,r);
        return {
            library:r,
            buildFunc:buildFunc,
            kvs_extend:kvs_extend,
            log_factory:log_factory
        };
    }
});