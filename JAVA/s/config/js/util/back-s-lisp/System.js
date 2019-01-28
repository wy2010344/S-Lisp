
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
								