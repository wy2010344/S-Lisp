[
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
        js [
            other "
            ReverseFun.base_run=function(list){
                return lib.s.reverse(list);
            };
            "
            run "
                return ReverseFun.base_run(args.First());
            "
        ]
        python [
            other "
    @staticmethod
    def base_run(list):
        return Node.reverse(list)
            "
            run "
        return ReverseFun.base_run(args.First())
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
        js [
            other "
            Kvs_reverseFun.base_run=function(kvs){
                var r=null;
                var tmp=kvs;
                while(tmp!=null){
                    var key=tmp.First();
                    tmp=tmp.Rest();
                    var value=tmp.First();
                    tmp=tmp.Rest();
                    r=lib.s.kvs_extend(key,value,r);
                }
                return r;
            };
            "
            run "
                return Kvs_reverseFun.base_run(args.First());
            "
        ]
        python [
            other "
    @staticmethod
    def base_run(kvs):
        r=None
        tmp=kvs
        while tmp!=None:
            key=tmp.First()
            tmp=tmp.Rest()
            value=tmp.First()
            tmp=tmp.Rest()
            r=Node.kvs_extend(key,value,r)
        return r
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
]