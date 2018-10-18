[
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
        js [
            other "
            OffsetFun.base_run=function(list,i){
                while(i!=0){
                    list=list.Rest();
                    i--;
                }
                return list;
            }
            "
            run "
            var list=args.First();
            args=args.Rest();
            var i=args.First();
            return OffsetFun.base_run(list,i);
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
        js [
            run "
                var list=args.First();
                args=args.Rest();
                var i=args.First();
                var r=null;
                while(i!=0){
                    r=lib.s.extend(list.First(),r);
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
]