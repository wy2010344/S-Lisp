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
]