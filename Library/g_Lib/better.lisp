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
		lisp {
			args
		}
	]

    type? [
        `可以用type实现，就不一一枚举了`
        cpp [
            run "
                Base *b=args->First();
                args=args->Rest();
                bool ret=false;
                string & type=static_cast<String*>(args->First())->StdStr();
                if(b==NULL){
                    ret=(type==\"list\");
                }else{
                    Base::S_Type t=b->stype();
                    if(t==Base::sList){
                        ret=(type==\"list\");
                    }else
                    if(t==Base::sFunction){
                        ret=(type==\"function\");
                    }else
                    if(t==Base::sInt){
                        ret=(type==\"int\");
                    }else
                    if(t==Base::sString){
                        ret=(type==\"string\");
                    }else
                    if(t==Base::sBool){
                        ret=(type==\"bool\");
                    }else
                    if(t==Base::sUser){
                        ret=(type==\"user\");
                    }else{
                        if(t==Base::sToken){
                            ret=(type==\"token\");
                        }else
                        if(t==Base::sExp){
                            ret=(type==\"exp\");
                        }else
                        if(t==Base::sLocation){
                            ret=(type==\"location\");
                        }
                    }
                }
                return Bool::trans(ret);
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
        lisp {
            (let (x n) args)
            (str-eq (type x) n)
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
        lisp {
            (not (apply = args))
        }
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
        C# [
            other "
            public static Node<Object> base_run(Node<Object> list){
                Node<Object> r=null;
                Node<Object> tmp=list;
                while(tmp!=null){
                    Object v=tmp.First();
                    r=Node<Object>.extend(v,r);
                    tmp=tmp.Rest();
                }
                return r;
            }
            "
            run "
                return base_run(args.First() as Node<Object>);
            "
        ]

        `可以用lisp实现，有点麻烦的样子`
        lisp {
            (let (xs) args)
            (reduce 
                xs
                {
                    (let (init x) args)
                    (extend x init)
                }
                []
            )
        }
    ]

    kvs-reverse [
        cpp [

        ]
        C# [
            other "
            public static Node<Object> base_run(Node<Object> kvs){
                Node<Object> r=null;
                Node<Object> tmp=kvs;
                while(tmp!=null){
                    String key=tmp.First() as String;
                    tmp=tmp.Rest();
                    Object value=tmp.First();
                    tmp=tmp.Rest();
                    r=Node<Object>.kvs_extend(key,value,r);
                }
                return r;
            }
            "
            run "
                return base_run(args.First() as Node<Object>);
            "
        ]
        lisp {
            (let (kvs) args)
            (kvs-reduce 
                kvs
                {
                    (let (init v k) args)
                    (kvs-extend k v init)
                }
                []
            )
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
        lisp {
            (let (a d) args)
            (if (exist? a) a d)
        }
    ]   
    if-run [
        cpp [
            `可以调用IfFunc，但中间retain\release次数比较多`
            run "
                Base * fun=IfFun::base_run(args);
                Base * b=NULL;
                if(fun!=NULL){
                    b=static_cast<Function*>(fun)->exec(NULL);
                    if(b!=NULL){
                        b->eval_release();
                    }
                }
                return b;
            "
        ]
        C# [
            run "
                Object o=IfFun.base_run(args);
                if(o==null){
                    return null;
                }else{
                    return (o as Function).exec(null);
                }
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
                while(list!=NULL){
                    Base * x=list->First();
                    Node *nargs=new Node(init,new Node(x,NULL));
                    nargs->retain();
                    Base* n_init=f->exec(nargs);
                    nargs->release();
                    if(n_init!=NULL){
                        n_init->eval_release();
                    }
                    init=n_init;
                    list=list->Rest();
                }
                return init;
            "
        ]
        C# [
            other "
            public static Object base_run(Node<Object> list,Node<Object> args){

                Function f = args.First() as Function;
                args = args.Rest();
                Object init = args.First();
                while (list != null)
                {
                    Object x = list.First();
                    list = list.Rest();
                    Node<Object> nargs = Node<Object>.list(init, x);
                    init = f.exec(nargs);
                }
                return init;
            }
            "
            run "
                Node<Object> list = args.First() as Node<Object>;
                args = args.Rest();
                return base_run(list,args);
            "
        ]
        lisp {
            (let (xs run init) args reduce this)
            (if-run (exist? xs)
                {
                    (let (x ...xs) xs)
                    (let init (run init x))
                    (reduce xs run init)
                }
                {init}
            )
        }
    ]

    reduce-right [
        C# [
            run "
                Node<Object> list = args.First() as Node<Object>;
                list=ReverseFun.base_run(list);
                args = args.Rest();
                return ReduceFun.base_run(list,args);
            "
        ]
        lisp {
            (let (xs run init) args reduce-right this)
            (if-run (exist? xs)
                {
                    (let (x ...xs) xs)
                    (run 
                        (reduce-right xs run  init)
                        x
                    )
                }
                { init }
            )
        }
    ]

    kvs-reduce [
        C# [
            other "
            public static Object base_run(Node<Object> kvs,Node<Object> args){
                Function f = args.First() as Function;
                args = args.Rest();
                Object init = args.First();
                while (kvs != null)
                {
                    Object key = kvs.First();
                    kvs = kvs.Rest();
                    Object value = kvs.First();
                    kvs = kvs.Rest();
                    Node<Object> nargs = Node<Object>.list(init,value,key);
                    init = f.exec(nargs);
                }
                return init;
            }
            "
            run "
                Node<Object> kvs = args.First() as Node<Object>;
                args = args.Rest();
                return base_run(kvs,args);
            "
        ]
        lisp {
            (let (kvs run init) args kvs-reduce this)
            (if-run (exist? kvs)
                {
                    (let (k v ...kvs) kvs)
                    (let init (run init v k))
                    (kvs-reduce kvs run init)
                }
                {init}
            )
        }
    ]

    kvs-reduce-right [
        C# [
            run "
                Node<Object> kvs = args.First() as Node<Object>;
                kvs=Kvs_reverseFun.base_run(kvs);
                args = args.Rest();
                return Kvs_reduceFun.base_run(kvs,args);
            "
        ]
        lisp {
            (let (kvs run init) args kvs-reduce-right this)
            (if-run (exist? kvs)
                {
                    (let (k v ...kvs) kvs)
                    (run
                        (kvs-reduce-right kvs run init)
                        v
                        k
                    )
                }
                { init }
            )
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
        C# [
            run "
                Node<Object> kvs=args.First() as Node<Object>;
                args=args.Rest();
                String key=args.First() as String;
                return Node<Object>.kvs_find1st(kvs,key);
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
        lisp {
            (let (k v kvs) args)
            (extend k (extend v kvs))
        }
	]
    
    kvs-path [
        C# [
            other "
                public static Object kvs_path(Node<Object> o,Node<Object> paths)
                {
                    Object value=null;
                    while(paths!=null)
                    {
                        String path=paths.First() as String;
                        value=Node<Object>.kvs_find1st(o,path);
                        paths=paths.Rest();
                        if(paths!=null)
                        {
                            o=value as Node<Object>;
                        }
                    }
                    return value;
                }
            "
            run "
                Node<Object> o=args.First() as Node<Object>;
                args=args.Rest();
                Node<Object> paths=args.First() as Node<Object>;
                return kvs_path(o,paths);
            "
        ]
        lisp {
            (let (e paths) args kvs-path this)
            (if-run (exist? paths)
                {
                    (let (path ...paths) paths)
                    (kvs-path 
                        (kvs-find1st e path)
                        paths
                    )
                }
                {e}
            )
        }
    ]
    
    kvs-path-run [
        C# [
            run "
                Node<Object> o=args.First() as Node<Object>;
                args=args.Rest();
                Node<Object> paths=args.First() as Node<Object>;
                args=args.Rest();
                Function f=Kvs_pathFun.kvs_path(o,paths) as Function;
                return f.exec(args);
            "
        ]
        lisp {
            (let (e paths ...ps) args)
            (apply (kvs-path e paths) ps)
        }
    ]

    offset [
        C# [
            other "
            public static Node<Object> base_run(Node<Object> list,int i){
                while(i!=0){
                    list=list.Rest();
                    i--;
                }
                return list;
            }
            "
            run "
                Node<Object> list=args.First() as Node<Object>;
                args=args.Rest();
                int i=(int)args.First();
                return base_run(list,i);
            "
        ]
        lisp {
            (let (list i) args offset this)
            (if-run (= i 0) 
                { list }
                {
                    (offset (rest list) (- i 1)) 
                } 
            )
        }
    ]

    slice-to [
        C# [
            run "
                Node<Object> list=args.First() as Node<Object>;
                args=args.Rest();
                int i=(int)args.First();

                Node<Object> r=null;
                while(i!=0){
                    r=Node<Object>.extend(list.First(),r);
                    list=list.Rest();
                    i--;
                }
                return ReverseFun.base_run(r);
            "
        ]
        lisp {
            (let (xs to) args slice-to this)
            (if-run (= to 0)
                {[]}
                {
                    (let (x ...xs) xs)
                    (extend x (slice-to xs (- to 1)))
                }
            )
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