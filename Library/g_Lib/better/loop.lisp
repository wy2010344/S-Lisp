[
    loop [
        `
        列表的遍历有reduce，但非列表的递归调用会报错，用宿主语言的while语句优化一下。
        reduce也可以用loop来实现。
        loop可以说是所有尾递归优化的根
        `
        cpp [
            run "
                Function * f=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * init=NULL;
                if(args!=NULL){
                    init=args->First();
                }
                bool will=true;
                while(will){
                    Node * o=static_cast<Node*>(f->exec(list::extend(init,NULL)));
                    will=static_cast<Bool*>(o->First())->Value();
                    init=o->Rest()->First();
                    if(init!=NULL){
                        init->retain();
                        o->release();
                        init->eval_release();
                    }else{
                        o->release();
                    }
                }
                return init;
            "
        ]
        C# [
            run "
                Function f=args.First() as Function;
                args=args.Rest();
                Object init=null;
                if(args!=null){
                    init=args.First();
                }
                bool will=true;
                while(will){
                    Node<Object> o=f.exec(Node<Object>.extend(init,null)) as Node<Object>;
                    will=(bool)(o.First());
                    o=o.Rest();
                    init=o.First();
                }
                return init;
            "
        ]

        js [
            run "
                var f=args.First();
                args=args.Rest();
                var init=null;
                if(args!=null){
                    init=args.First();
                }
                var will=true;
                while(will){
                    var o=f.exec(lib.s.extend(init,null));
                    will=o.First();
                    o=o.Rest();
                    init=o.First();
                }
                return init;
            "
        ]

        lisp {
            (let (f init) args loop this)
            (let (will init) (f init))
            (if-run will
                {
                    (loop f init)
                }
                {init}
            )
        }
    ]
]