[
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

        js [
            other "
            ReduceFun.base_run=function(list,args){
                var f=args.First();
                args=args.Rest();
                var init=args.First();
                while(list!=null){
                    var x=list.First();
                    list=list.Rest();
                    var nargs=lib.s.list(init,x);
                    init=f.exec(nargs);
                }
                return init;
            }
            "
            run "
                var list=args.First();
                args=args.Rest();
                return ReduceFun.base_run(list,args);
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
        js [
            run "
                var list=args.First();
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
        js [
            other "
            Kvs_reduceFun.base_run=function(kvs,args){
                var f=args.First();
                args=args.Rest();
                var init=args.First();
                while(kvs!=null){
                    var key=kvs.First();
                    kvs=kvs.Rest();
                    var value=kvs.First();
                    kvs=kvs.Rest();
                    var nargs=lib.s.list(init,value,key);
                    init=f.exec(nargs);
                }
                return init;
            }
            "
            run "
                var kvs=args.First();
                args=args.Rest();
                return Kvs_reduceFun.base_run(kvs,args);
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
        js [
            run "
                var kvs=args.First();
                kvs=Kvs_reverseFun.base_run(kvs);
                args=args.Rest();
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
]