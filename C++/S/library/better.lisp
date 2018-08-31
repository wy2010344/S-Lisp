[
	list [
		cpp [
			run "
                return args;
			"
		]
		lisp {
			args
		}
	]
	quote [
		cpp [
			run "
            	return args->First();
			"
		]
		lisp {
			(first args)
		}
	]

    != [

    ]

    empty-fun [
        cpp [
            run "
                return NULL;
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
        lisp {
            (let (a d) args)
            (if (exist? a) a d)
        }
    ]   
    if-run [
        cpp [
            run "
                Function * If=IfFunc::instance();
                Base*  run=If->exec(args);
                if(run!=NULL){
                    Base * b=(static_cast<Function*>(run))->exec(NULL);
                    run->release();/*从函数出来都加了1，release*/
                    b->eval_release();/*从函数出来都加了1*/
                    return b;
                }else{
                    return NULL;
                }
            "
        ]
        lisp {
            (let (a b c) args)
            (let x (default (if a b c)))
            (x)
        }
    ]

    reduce-left [

    ]

    reduce-right [

    ]

    kvs-reduce-left [

    ]

    kvs-reduce-right [

    ]
	kvs-find1st [
		cpp [
			run "
                Node* kvs_map=static_cast<Node*>(args->First());
                args=args->Rest();
                String* key=static_cast<String*>(args->First());
                return kvs::find1st(kvs_map,key->StdStr());
			"
		]
        lisp {
            (let (key kvs) args find1st this)
            (let (k v ...kvs) args)
            (if-run (str-eq k key)
                {v}
                {find1st key kvs}
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
        lisp {
            (let (k v kvs) args)
            (extend k (extend v kvs))
        }
	]
]