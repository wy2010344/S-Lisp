[
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
        js [
            run "
                var kvs=args.First();
                args=args.Rest();
                var paths=args.First();
                return kvs_path(kvs,paths);
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
        js [
            run "
                var kvs=args.First();
                args=args.Rest();
                var paths=args.First();
                args=args.Rest();
                return kvs_path(kvs,paths).exec(args);
            "
        ]
        lisp {
            (let (e paths ...ps) args)
            (apply (kvs-path e paths) ps)
        }
    ]
]