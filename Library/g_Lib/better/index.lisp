[
    quote [
        cpp [
            run "
                return args->First();
            "
        ]
        C# [
            run "
                return args.First();
            "
        ]
        js [
            run "
                return args.First();
            "
        ]

        OC [
            run "
                return [args First];
            "
        ]

        python [
            run "
        return args.First()
            "
        ]
        lisp {
            (first args)
        }
    ]

	list [
		cpp [
			run "
                return args;
			"
		]
        C# [
            run "
                return args;
            "
        ]
        js [
            run "
                return args;
            "
        ]
        OC [
            run "
                return args;
            "
        ]
        python [
            run "
        return args
            "
        ]
		lisp {
			args
		}
	]

    kvs-find1st [
        cpp [
            run "
                Node* kvs_map=static_cast<Node*>(args->First());
                args=args->Rest();
                String* key=static_cast<String*>(args->First());
                return kvs::find1st(kvs_map,key);
            "
        ]
        C# [
            run "
                Node<Object> kvs=args.First() as Node<Object>;
                args=args.Rest();
                String key=args.First() as String;
                return Node<Object>.kvs_find1st(kvs,key);
            "
        ]
        js [
            run "
                var kvs=args.First();
                args=args.Rest();
                var key=args.First();
                return lib.s.kvs_find1st(kvs,key);
            "
        ]

        OC [
            run "
                SNode* kvs=(SNode*)[args First];
                args=[args Rest];
                NSString* key=(NSString*)[args First];
                return [SNode kvs_find1stFrom:kvs of:key];
            "
        ]

        python [
            run "
        kvs=args.First()
        args=args.Rest()
        key=args.First()
        return Node.kvs_find1st(kvs,key)
            "
        ]
        lisp {
            (let (key kvs) args find1st this)
            (let (k v ...kvs) args)
            (if-run (str-eq k key)
                {v}
                {(find1st key kvs)}
            )
        }
    ]
    
    kvs-extend [
        cpp [
            run "
                String* key=static_cast<String*>(args->First());
                args=args->Rest();
                Base* val=args->First();
                args=args->Rest();
                Node* kvs_map=static_cast<Node*>(args->First());
                return kvs::extend(key,val,kvs_map);
            "
        ]
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
        js [
            run "
                var key=args.First();
                args=args.Rest();
                var value=args.First();
                args=args.Rest();
                var kvs=args.First();
                return lib.s.kvs_extend(key,value,kvs);
            "
        ]
        OC [
            run "
                NSString* key=(NSString*)[args First];
                args=[args Rest];
                NSObject* value=[args First];
                args=[args Rest];
                SNode* kvs=(SNode*)[args First];
                return [SNode kvs_extendKey:key value:value kvs:kvs];
            "
        ]
        python [
            run "
        key=args.First()
        args=args.Rest()
        value=args.First()
        args=args.Rest()
        kvs=args.First()
        return Node.kvs_extend(key,value,kvs)
            "
        ]
        lisp {
            (let (k v kvs) args)
            (extend k (extend v kvs))
        }
    ]
    
    type? [
        `可以用type实现，就不一一枚举了`
        cpp [
            run "
                Base *b=args->First();
                args=args->Rest();
                String* real_type=TypeFun::instance()->base_run(b);
                return Bool::trans(real_type->StdStr()==static_cast<String*>(args->First())->StdStr());
            "
        ]

        C# [
            run "
                Object x=args.First();
                args=args.Rest();
                String n=args.First() as String;
                return (TypeFun.base_run(x)==n);
            "
        ]

        js [
            run "
                var x=args.First();
                args=args.Rest();
                var n=args.First();
                return (TypeFun.base_run(x)==n);
            "
        ]

        python [
            run "
        x=args.First()
        args=args.Rest()
        n=args.First()
        return (TypeFun.base_run(x)==n)
            "
        ]
        lisp {
            (let (x n) args)
            (str-eq (type x) n)
        }
    ]

    call [
        cpp [
            run "
                Function* f=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * b=f->exec(args);
                if(b!=NULL){
                    b->eval_release();
                }
                return b;
            "
        ]
        C# [
            run "
                Function f=args.First() as Function;
                args=args.Rest();
                return f.exec(args);
            "
        ]
        js [
            run "
                var run=args.First();
                args=args.Rest();
                return run.exec(args);
            "
        ]
        python [
            run "
        run=args.First()
        args=args.Rest()
        return run.exe(args)
            "
        ]
        list {
            (let (f ...args) args)
            (apply f args)
        }
    ]
    != [
        alias MNotEqFun
        cpp [
            run "
                return Bool::trans(!MEqFun::base_run(args));
            "
        ]
        C# [
            run "
                return !MEqFun.base_run(args);
            "
        ]
        js [
            run "
                return !MEqFun.base_run(args);
            "
        ]
        python[
            run "
        return (not MEqFun.base_run(args))
            "
        ]
        lisp {
            (not (apply = args))
        }
    ]

    empty-fun [
        cpp [
            run "
                return NULL;
            "
        ]
        C# [
            run "
                return null;
            "
        ]
        js [
            run "
                return null;
            "
        ]
        python [
            run "
        return None
            "
        ]
        lisp {}
    ]
    default [
        cpp [
            run "
                Base * v=args->First();
                if(v!=NULL){
                    return v;
                }else{
                    args=args->Rest();
                    Base * d=args->First();
                    return d;
                }
            "
        ]
        C# [
            run "
                Object v=args.First();
                if(v!=null){
                    return v;
                }else{
                    args=args.Rest();
                    return args.First();
                }
            "
        ]
        js [
            run "
                var v=args.First();
                if(v!=null){
                    return v;
                }else{
                    args=args.Rest();
                    return args.First();
                }
            "
        ]
        python [
            run "
        v=args.First()
        if v!=None:
            return v
        else:
            args=args.Rest()
            return args.First()
            "
        ]
        lisp {
            (let (a d) args)
            (if (exist? a) a d)
        }
    ]   

    len [
        C# [
            run "
                Node<Object> list=args.First() as Node<Object>;
                if(list!=null){
                    return list.Length();
                }else{
                    return 0;
                }
            "
        ]
        js [
            run "
                var list=args.First();
                if(list){
                    return list.Length();
                }else{
                    return 0;
                }
            "
        ]
        python [
            run "
        list=args.First()
        if list!=None:
            return list.Length()
        else:
            return 0
            "
        ]
        lisp {
            (let (cs) args)
            (if-run (exist? cs)
                {
                    (length cs)
                }
                {0}
            )
        }
    ]

    indexOf [
        `默认只找第一个，假设无重复，如果想找所有的，另定义方法`
        cpp [
            run "
                Node* vs=static_cast<Node*>(args->First());
                args=args->Rest();
                Base* k=args->First();
                args=args->Rest();
                Function* eq=EqFun::instance();
                if(args!=NULL){
                    eq=static_cast<Function*>(args->First());
                }

                int index=-1;
                int flag=0;
                while(vs!=NULL && index==-1){
                    Node* nargs=new Node(vs->First(),new Node(k,NULL));
                    nargs->retain();
                    Bool *b=static_cast<Bool*>(eq->exec(nargs));
                    nargs->release();
                    if(b->Value()){
                        index=flag;
                    }else{
                        vs=vs->Rest();
                        flag++;
                    }
                    b->release();
                }
                if(index==-1){
                    return NULL;
                }else{
                    return new Int(index);
                }
            "
        ]
        C# [
            run "
                Node<Object> vs=args.First() as Node<Object>;
                args=args.Rest();
                Object k=args.First();
                args=args.Rest();
                Function eq=EqFun.instance();
                if(args!=null){
                    eq=args.First() as Function;
                }

                int index=-1;
                int flag=0;
                while(vs!=null && index==-1){
                    if((bool)eq.exec(Node<Object>.list(vs.First(),k))){
                        index=flag;
                    }else{
                        vs=vs.Rest();
                        flag++;
                    }
                }
                if(index==-1){
                    return null;
                }else{
                    return index;
                }
            "
        ]

        python [
            run "
        vs=args.First()
        args=args.Rest()
        k=args.First()
        args=args.Rest()
        eq=None
        if args!=None:
            eq=args.First()
        else:
            eq=EqFun()
        index=-1
        flag=0
        while (vs!=None and index==-1):
            if eq.exe(Node.list(vs.First(),k)):
                index=flag
            else:
                vs=vs.Rest()
                flag=flag+1
        if index==-1:
            return None
        else:
            return index

            "
        ]
        lisp {
            (let 
                (vs k is_eq) args
                is_eq (default eq)
            )
            (loop 
                {
                    (let ((v ...vs) index) args)
                    (if-run (is_eq v k)
                        {
                            (list
                                false
                                index
                            )
                        }
                        {
                            (if-run (exist? vs)
                                {
                                    (list
                                        true
                                        (list
                                            vs
                                            (+ index 1)
                                        )
                                    ) 
                                }
                            )
                        }
                    )
                }
                (list vs 0)
            )
        }
    ]
]