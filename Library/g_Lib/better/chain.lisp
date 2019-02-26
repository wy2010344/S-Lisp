[

	`
	只区分列表与字符串，字符串是访问kvs属性，列表是作函数调用
	其实比kvs-path更复杂一点kvs-path-run->chain->chain-plus
	`
	chain [

		cpp [

		]

		C# [
			run "
				Object o=args.First();
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
						Node<Object> kvs=o as Node<Object>;
						String key=v as String;
						o=Node<Object>.kvs_find1st(kvs,key);
					}else{
						throw new Exception(\"不是合法的类型 \");
					}
				}
				return o;
			"
		]

		js [

		]

		lisp {
			(let (o vs) args)
		}
	]
]