[
	`管道，类似于减法`
	pip [
		cpp [
			run "
				Base* o=args->First();
				args=args->Rest();
				while(args!=NULL){
					Function* f=static_cast<Function*>(args->First());
					args=args->Rest();
					Node* n_args=new Node(o,NULL);
					n_args->retain();
					o=f->exec(n_args);
					n_args->release();
					if(o!=NULL){
						o->eval_release();
					}
				}
				return o;
			"
		]
		C# [
			run "
				Object o=args.First();
				args=args.Rest();
				while(args!=null){
					Function f=args.First() as Function;
					args=args.Rest();
					o=f.exec(Node<Object>.extend(o,null));
				}
				return o;
			"
		]
		js [
			run "
				var o=args.First();
				args=args.Rest();
				while(args!=null){
					var f=args.First();
					args=args.Rest();
					o=f.exec(lib.s.list(o));
				}
				return o;
			"
		]
		python [
			run "
		o=args.First()
		args=args.Rest()
		while args!=None:
			f=args.First()
			args=args.Rest()
			o=f.exe(Node.list(o,None))
		return o
			"
		]
		lisp {
			(first
				(apply loop 
					(extend
						{
							(let (x f ...xs) args)
							(if-run (empty? xs)
								{
									(list
										false
										(f x)
									)
								}
								{
									(extend 
										true 
										(extend (f x) xs)
									)
								}
							)
						}
						args	
					)
				)
			)
		}
	]
]
