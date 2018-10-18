[
    + [
        alias AddFun
        cpp [
            run "
                int all=0;
                for(Node * t=args;t!=NULL;t=t->Rest()){
                    Int * it=static_cast<Int*>(t->First());
                    all=all+it->Value();
                }
                return new Int(all);
            "
        ]
        C# [
            run "
                int all=0;
                for(Node<Object> t=args;t!=null;t=t.Rest())
                {
                    int it=(int)t.First();
                    all=all+it;
                }
                return all;
            "
        ]
        js [
            run "
                return reduce(args,function(last,now){
                    return last+now;
                },0);
            "
        ]
    ]
    - [
        alias SubFun
        cpp [
            run "
                int all=static_cast<Int*>(args->First())->Value();
                args=args->Rest();
                for(Node * t=args;t!=NULL;t=t->Rest()){
                    Int * it=static_cast<Int*>(t->First());
                    all=all-it->Value();
                }
                return new Int(all);
            "
        ]
        C# [
            run "
                int all=(int)args.First();
                args=args.Rest();
                for(Node<Object> t=args;t!=null;t=t.Rest())
                {
                    int it=(int)args.First();
                    all=all-it;
                }
                return all;
            "
        ]
        js [
            run "
                var r=args.First();
                return reduce(args.Rest(),function(last,now){
                    return last-now;
                },r);
            "
        ]
    ]

    * [
        alias MultiFun
        js [
            run "
                return reduce(args,function(last,now){
                    return last*now;
                },1);
            "
        ]
    ]

    / [
        alias DivFun
        js [
            run "
                var r=args.First();
                return reduce(args.Rest(),function(last,now){
                    return last/now;
                },r);
            "
        ]
    ]

    parseInt [
        js [
            run "
                return parseInt(args.First());
            "
        ]
    ]

    >  [
        alias MBiggerFun
        cpp [
            run "
                bool ret=true;
                Int* last=static_cast<Int*>(args->First());
                args=args->Rest();
                while(args!=NULL && ret){
                    Int* current=static_cast<Int*>(args->First());
                    ret=(last->Value()>current->Value());
                    last=current;
                    args=args->Rest();
                }
                return Bool::trans(ret);
            "
        ]
        C# [
            run "
                bool ret=true;
                int last=(int)args.First();
                args=args.Rest();
                while(args!=null && ret)
                {
                    int current=(int)args.First();
                    ret=(last>current);
                    last=current;
                    args=args.Rest();
                }
                return ret;
            "
        ]
        js [
            run "
                //数字
                return compare(args,check_is_number,function(last,now){
                    return (last>now);
                });
            "
        ]
    ]

    < [
        alias MSmallerFun
        cpp [
            run "
                bool ret=true;
                Int* last=static_cast<Int*>(args->First());
                args=args->Rest();
                while(args!=NULL && ret){
                    Int* current=static_cast<Int*>(args->First());
                    ret=(last->Value()<current->Value());
                    last=current;
                    args=args->Rest();
                }
                return Bool::trans(ret);
            "
        ]
        C# [
            run "
                bool ret=true;
                int last=(int)args.First();
                args=args.Rest();
                while(args!=null && ret)
                {
                    int current=(int)args.First();
                    ret=(last<current);
                    last=current;
                    args=args.Rest();
                }
                return ret;
            "
        ]
        js [
            run "
                //数字
                return compare(args,check_is_number,function(last,now){
                    return (last<now);
                });
            "
        ]
    ]

    = [
        alias MEqFun
        cpp [
            other "
            static bool base_run(Node* args){
                bool ret=true;
                Int* last=static_cast<Int*>(args->First());
                args=args->Rest();
                while(args!=NULL && ret){
                    Int* current=static_cast<Int*>(args->First());
                    ret=(last->Value()==current->Value());
                    last=current;
                    args=args->Rest();
                }
                return ret;
            }
            "
            run "
                return Bool::trans(base_run(args));
            "
        ]
        C# [
            other "
            public static bool base_run(Node<Object> args){
                bool ret=true;
                int last=(int)args.First();
                args=args.Rest();
                while(args!=null && ret)
                {
                    int current=(int)args.First();
                    ret=(last==current);
                    last=current;
                    args=args.Rest();
                }
                return ret;
            }
            "
            run "
                return base_run(args);
            "
        ]
        js [
            other "
                MEqFun.base_run=function(args){
                    return eq(args,check_is_number);
                }
            "
            run "
                return MEqFun.base_run(args);
            "
        ]
    ]
]