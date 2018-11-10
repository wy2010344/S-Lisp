[
    slice-from [
        cpp [
            other "
            static Node* base_run(Node* list,int i){
                while(i!=0){
                    list=list->Rest();
                    i--;
                }
                return list;
            }
            "
            run "
            Node* list=static_cast<Node*>(args->First());
            args=args->Rest();
            int i=static_cast<Int*>(args->First())->Value();
            return base_run(list,i);
            "
        ]
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
            Slice_fromFun.base_run=function(list,i){
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
            return Slice_fromFun.base_run(list,i);
            "
        ]

        python [
            other "
    @staticmethod
    def base_run(list,i):
        while i!=0:
            list=list.Rest()
            i=i-1
        return list
            "
            run "
        list=args.First()
        args=args.Rest()
        i=args.First()
        return Slice_fromFun.base_run(list,i)
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
        python [
            run "
        list=args.First()
        args=args.Rest()
        i=args.First()
        r=None
        while i!=0:
            r=Node.extend(list.First(),r)
            list=list.Rest()
            i=i-1
        return ReverseFun.base_run(r)
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

    offset [
        `
        offset与slice-from不同，offset是取slice-from的第一个元素
        `
        cpp [
            run "
            Node* list=static_cast<Node*>(args->First());
            args=args->Rest();
            int i=static_cast<Int*>(args->First())->Value();
            return Slice_fromFun::base_run(list,i)->First();
            "
        ]
        C# [
            run "
                Node<Object> list=args.First() as Node<Object>;
                args=args.Rest();
                int i=(int)args.First();
                return Slice_fromFun.base_run(args,i).First();
            "
        ]
        js [
            run "
                var list=args.First();
                args=args.Rest();
                var i=args.First();
                return Slice_fromFun.base_run(list,i).First()
            "
        ]
        python [
            run "
        list=args.First()
        args=args.Rest()
        i=args.First()
        return Slice_fromFun.base_run(list,i).First()
            "
        ]
        lisp {
            (first (apply slice-from args))
        }
    ]
]