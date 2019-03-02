[
	`
	将函数第一个参数在左边的链式语法，类似于静态语言中的扩展方法。
	`

	extension [
		C# [
			run "
				Object o=args.First();
				args=args.Rest();
				while(args!=null){
					Function fun=args.First() as Function;
					args=args.Rest();
					Node<Object> list=args.First() as Node<Object>;
					args=args.Rest();
					o=fun.exec(Node<Object>.extend(o,list));
				}
				return o;
			"
		]

		lisp {
			
		}
	]
]