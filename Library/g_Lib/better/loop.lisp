[
    loop [
        `
        列表的遍历有reduce，但非列表的递归调用会报错，用宿主语言的while语句优化一下。
        reduce也可以用loop来实现。
        loop可以说是所有尾递归优化的根

        进一步改造，init不是一个值，是剩下的所有值
        `
        cpp [
            run "
                Function * f=static_cast<Function*>(args->First());
                args=args->Rest();
                bool will=true;
                while(will){
                    Node* o=static_cast<Node*>(f->exec(args));
                    will=static_cast<Bool*>(o->First())->Value();
                    args=o->Rest();
                    if(args!=NULL){
                        args->retain();
                        o->release();
                        args->eval_release();
                    }else{
                        o->release();
                    }
                }
                return args;
            "
        ]
        C# [
            run "
                Function f=args.First() as Function;
                args=args.Rest();
                bool will=true;
                while(will){
                    args=f.exec(args) as Node<Object>;
                    will=(bool)(args.First());
                    args=args.Rest();
                }
                return args;
            "
        ]

        js [
            run "
                var f=args.First();
                args=args.Rest();
                var will=true;
                while(will){
                    args=f.exec(args);
                    will=args.First();
                    args=args.Rest();
                }
                return args;
            "
        ]

        python [
            run "
        f=args.First()
        args=args.Rest()
        will=True 
        while will:
            args=f.exe(args)
            will=args.First()
            args=args.Rest()
        return args
            "
        ]
        lisp {
            (let (f ...init) args loop this)
            (let (will ...init) (apply f init))
            (if-run will
                {
                    (apply loop (extend f init))
                }
                {init}
            )
        }
    ]
]