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

    reverse [
        cpp [
            run "
                Node * list=static_cast<Node*>(args->First());
                Node *r=NULL;
                while(list!=NULL){
                    r=new Node(list->First(),r);
                    list=list->Rest();
                }
                return r;
            "
        ]
        `可以用lisp实现，有点麻烦的样子`
        lisp_tmp {
            (let (xs) args)
            (let (x ...xs) xs)
        }
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
            `可以调用IfFunc，但中间retain\release次数比较多`
            run "
                Bool * cond=static_cast<Bool*>(args->First());
                args=args->Rest();
                Function * trueR=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * b=NULL;
                if(cond->Value()){
                    b=trueR->exec(NULL);
                }else{
                    if(args!=NULL){
                        Function *theF=static_cast<Function*>(args->First());
                        b=theF->exec(NULL);
                    }
                }
                /*从函数出来都加了1，release*/
                if(b!=NULL){
                    b->eval_release();
                }
                return b;
            "
        ]
        lisp {
            (let (a b c) args)
            (let x (default (if a b c)))
            (x)
        }
    ]

    `就是reduce-left`
    reduce [
        cpp [
            run "
                Node *list=static_cast<Node*>(args->First());
                args=args->Rest();
                Function *f=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * init=args->First();
                int i=0;
                while(list!=NULL){
                    Base * x=list->First();
                    Int *is=new Int(i);
                    Node *nargs=new Node(init,new Node(x,new Node(is,NULL)));
                    nargs->retain();
                    Base* n_init=f->exec(nargs);
                    nargs->release();
                    if(n_init!=NULL){
                        n_init->eval_release();
                    }
                    init=n_init;
                    i++;
                    list=list->Rest();
                }
                return init;
            "
        ]
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