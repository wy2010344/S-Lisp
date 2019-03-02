[
	`
	类似
	(let a [a b c d e { [x y]}])
	(chain a [-> e [] . x])
	`
	chain-plus [

		cpp [


		]

		C# [
			run "
                Object o = args.First();
				args=args.Rest();
				Node<Object> vs=args.First() as Node<Object>;
				while(vs!=null){
                    Object v=vs.First();
					vs=vs.Rest();
					if(v is Node<Object> || v==null){
						//求函数 
						Function fun=o as Function;
						o=fun.exec(o as Node<Object>);
					}else
                    if(v is String){
                    	Node<Object> kvs = o as Node<Object>;
                    	String op=v as String;
						String key=vs.First() as String;
	                    vs = vs.Rest();
						if(op==\".\"){
							//kvs访问
	                        o = Node<Object>.kvs_find1st(kvs, key);
						}else if(op==\"->\"){
							//原型链式访问
						    Node<Object> param=vs.First() as Node<Object>;
	                        vs=vs.Rest();
	                        Function fun = Node<Object>.kvs_find1st(kvs, key) as Function;
	                        o = fun.exec(Node<Object>.extend(kvs, param));
						}else if(op==\"extend\"){
							Node<Object> param=vs.First() as Node<Object>;
							vs=vs.Rest();
							o=Node<Object>.extend(o,param);
						}else if(op==\"to\"){
							Node<Object> param=o as Node<Object>;
							Function fun=vs.First() as Function;
							vs=vs.Rest();
							o=fun.exec(param);
						}else{
							throw new Exception(\"未找到合法的operator\");
						}
					}
				}
                return o;
			"
		]

		js [

		]

		python [

		]

		lisp {
			(let (o vs) args)
			(loop {
				(let (v ...vs) args)
				(if-run (type? v 'list)
					{}
					{
						(if-run (type? v 'string)
							{
								(if-run (str-eq v '.)
									{}
									{}
								)
							}
							{}
						)
					}
				)
			}
			vs)
		}
	]
]