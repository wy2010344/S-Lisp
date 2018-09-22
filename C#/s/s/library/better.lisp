[
	quote [
	    C# [
	        run "
	            return args.First();
	        "
	    ]
	    lisp {(first args)}
	]
	
	list [
	    C# [
	        run "
	            return args;
	        "
	    ]
	    lisp {args}
	]
	
	`是可以用lisp实现的`
    kvs-find1st [
        C# [
            run "
                Node<Object> kvs=args.First() as Node<Object>;
                args=args.Rest();
                String key=args.First() as String;
                return Node<Object>.kvs_find1st(kvs,key);
            "
        ]
    ]
    
    kvs-extend [
        C# [
            run "
                String key=args.First() as String;
                args=args.Rest();
                Object value=args.First();
                args=args.Rest();
                Node<Object> kvs=args.First() as Node<Object>;
                return Node<Object>.kvs_extend(key,value,kvs);
            "
        ]
    ]
    
    reverse [
        C# [
            run "
                Node<Object> r=null;
                for(Node<Object> tmp=(args.First() as Node<Object>);tmp!=null;tmp=tmp.Rest())
                {
                    r=Node<Object>.extend(tmp.First(),r);
                }
                return r;
            "
        ]
    ]
]