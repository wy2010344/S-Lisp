[
    kvs-path [
        C# [
            other "
                public static Object base_run(Node<Object> o,Node<Object> paths)
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
                return base_run(o,paths);
            "
        ]
        js [
            run "
                var kvs=args.First();
                args=args.Rest();
                var paths=args.First();
                return kvs_path(kvs,paths);
            "
        ]

        python [
            other "
    @staticmethod
    def base_run(kvs,paths):
        while paths!=None:
            path=paths.First()
            value=Node.kvs_find1st(kvs,path)
            paths=paths.Rest()
            if paths!=None:
                kvs=value
        return value
            "
            run "
        kvs=args.First()
        args=args.Rest()
        paths=args.First()
        return Kvs_pathFun.base_run(kvs,args)
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
                Function f=Kvs_pathFun.base_run(o,paths) as Function;
                return f.exec(args);
            "
        ]
        js [
            run "
                var kvs=args.First();
                args=args.Rest();
                var paths=args.First();
                args=args.Rest();
                return kvs_path(kvs,paths).exec(args);
            "
        ]
        python [
            run "
        kvs=args.First()
        kvs=kvs.Rest()
        paths=args.First()
        args=args.Rest()
        return Kvs_pathFun.base_run(kvs,paths).exe(args)
            "
        ]
        lisp {
            (let (e paths ...ps) args)
            (apply (kvs-path e paths) ps)
        }
    ]
]