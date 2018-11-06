[
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
        js [
            run "
                var o=IfFun.base_run(args);
                if(o==null){
                    return null;
                }else{
                    return o.exec(null);
                }
            "
        ]
        python [
            run "
        o=IfFun.base_run(args)
        if o==None:
            return None
        else:
            return o.exe(None)
            "
        ]
        lisp {
            (let (a b c) args)
            (let x (default (if a b c)))
            (x)
        }
    ]
]