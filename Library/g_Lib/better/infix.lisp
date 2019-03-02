[

	`中缀表达式
	即1+1-3+9，无优先级
	和chain-plus相似，不过中间不是字符串，而是函数，接受两个参数来处理`
	infix [


		C# [
			run "
				Object o=args.First();
				args=args.Rest();
				while(args!=null){
					Function fun=args.First() as Function;
					args=args.Rest();
					Object right=args.First();
					args=args.Rest();
					o=fun.exec(Node<Object>.list(o,right));
				}
				return o;
			"
		]
		lisp {

		}
	]

]