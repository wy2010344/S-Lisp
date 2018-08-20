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
    empty-fun [
        lisp {}
    ]
    default [
        lisp {
            (let (a d) args)
            (if (exist? a) a d)
        }
    ]   
    if-run [
        lisp {
            (let (a b c) args)
            (let x (default (if a b c)))
            (x)
        }
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