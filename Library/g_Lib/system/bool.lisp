[
    and [
        cpp [
            run "
                bool init=true;
                Node *t=args;
                while(t!=NULL && init){
                    Bool *b=static_cast<Bool*>(t->First());
                    init=b->Value();
                    t=t->Rest();
                }
                return Bool::trans(init);
            "
        ]
        C# [
            run "
                bool ret=true;
                while(args!=null && ret)
                {
                    ret=(bool)args.First();
                    args=args.Rest();
                }
                return ret;
            "
        ]
        js [
            run "
                return reduce(args,function(init,v) {
                    return and(init,v);
                },true);
            "
        ]
        OC [
            run "
                BOOL init=YES;
                SNode* t=args;
                while(t!=nil && init){
                    init=[(SBool*)[t First] Value];
                    t=[t Rest];
                }
                return [SBool trans:init];
            "
        ]
    ]

    or [
        cpp [
            run "
                bool init=false;
                Node *t=args;
                while(t!=NULL && (!init)){
                    Bool *b=static_cast<Bool*>(t->First());
                    init=b->Value();
                    t=t->Rest();
                }
                return Bool::trans(init);
            "
        ]
        C# [
            run "
                bool ret=false;
                while(args!=null && (!ret))
                {
                    ret=(bool)args.First();
                    args=args.Rest();
                }
                return ret;
            "
        ]
        js [
            run "
                return reduce(args,function(init,v) {
                    return or(init,v);
                },false);
            "
        ]
        OC [
            run "
                BOOL init=NO;
                SNode* t=args;
                while(t!=nil && (!init)){
                    init=[(SBool*)[t First] Value];
                    t=[t Rest];
                }
                return [SBool trans:init];
            "
        ]
    ]

    not [
        cpp [
            run "
                Bool *b=static_cast<Bool*>(args->First());
                return Bool::trans(!b->Value());
            "
        ]
        C# [
            run "
                return !(bool)args.First();
            "
        ]
        js [
            run "
                return !args.First();
            "
        ]
        OC [
            run "
                return [SBool trans:![(SBool*)[args First] Value]];
            "
        ]
    ]
]