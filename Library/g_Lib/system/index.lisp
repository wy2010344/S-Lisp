[
	`first函数`
	first [
		cpp [
			run "
                return (static_cast<Node *>(args->First()))->First();
			"
		]
        C# [
            run "
                return (args.First() as Node<Object>).First();
            "
        ]
        js [
            run "
                var v=args.First();
                return v.First();
            "
        ]
        OC [
            run "
                return [(SNode*)[args First] First];
            "
        ]
        python [
            run "
        return args.First().First()
            "
        ]
	]
	rest [
		cpp [
			run "
				return (static_cast<Node *>(args->First()))->First();
			"
		]
        C# [
            run "
                return (args.First() as Node<Object>).Rest();
            "
        ]
        js [
            run "
                var v=args.First();
                return v.Rest();
            "
        ]
        OC [
            run "
                return [(SNode*)[args First] Rest];
            "
        ]
        python [
            run "
        return args.First().Rest()
            "
        ]
	]
	extend [
		cpp [
			run "
				return new Node(args->First(),static_cast<Node*>(args->Rest()->First()));
			"
		]
        C# [
            run "
                return Node<Object>.extend(args.First(),(args.Rest().First() as Node<Object>));
            "
        ]
        js [
            run "
                return lib.s.extend(args.First(),args.Rest().First());
            "
        ]
        OC [
            run "
                return [SNode extend:[args First] with:(SNode*)[[args Rest] First]];
            "
        ]
        python [
            run "
        return Node.extend(args.First(),args.Rest().First())
            "
        ]
	]
	length [
		cpp [
			run "
                return new Int(((Node *)args->First())->Length());
			"
		]
        C# [
            run "
                return (args.First() as Node<Object>).Length();
            "
        ]
        js [
            run "
                return args.First().Length();
            "
        ]
        OC [
            run "
                return [NSNumber numberWithInt:[(SNode*)[args First] Length]];
            "
        ]
        python [
            run "
        return args.First().Length()
            "
        ]
	]

    ref-count [
        cpp [
            run "
                Base *b=args->First();
                return new Int(b->ref_count());
            "
        ]
        OC [
            run "
                return [NSNumber numberWithLong:[[args First] retainCount]-1];
            "
        ]
    ]

	`判断列表为空，应该只支持列表和空才对`
	empty? [
		cpp [
			run "
                return Bool::trans(args->First()==NULL);
			"
		]
        C# [
            run "
                return args.First()==null;
            "
        ]
        js [
            run "
                return (args.First()==null);
            "
        ]

        OC [
            run "
                return [SBool trans:[args First]==nil];
            "
        ]

        python [
            run "
        return args.First()==None
            "
        ]
	]
	exist? [
		cpp [
			run "
                return Bool::trans(args->First()!=NULL);
			"
		]
        C# [
            run "
                return args.First()!=null;
            "
        ]
        js [
            run "
                return (args.First()!=null);
            "
        ]

        OC [
            run "
                return [SBool trans:[args First]!=nil];
            "
        ]

        python [
            run "
        return args.First()!=None
            "
        ]
	]
	if [
		cpp [
            other "
            static Base * base_run(Node * args){
                Bool * cond=static_cast<Bool*>(args->First());
                Base * ret=NULL;
                args=args->Rest();
                if (cond==Bool::True) {
                    ret=args->First();
                }else{
                    args=args->Rest();
                    if(args!=NULL){
                        ret=args->First();
                    }
                }
                return ret;
            }
            "
			run "
                return base_run(args);
			"
		]
        C# [
            other "
            public static Object base_run(Node<Object> args){
                bool c=(bool)args.First();
                args=args.Rest();
                if(c){
                    return args.First();
                }else{
                    args=args.Rest();
                    if(args!=null)
                    {
                        return args.First();
                    }else{
                        return null;
                    }
                }
            }

            "
            run "
                return base_run(args);
            "
        ]
        js [
            other "
            IfFun.base_run=function(args){
                if(args.First()){
                    return args.Rest().First();
                }else{
                    args=args.Rest().Rest();
                    if(args){
                        return args.First();
                    }else{
                        return null;
                    }
                }
            };
            "
            run "
                return IfFun.base_run(args);
            "
        ]
        OC [
            other "
            + (NSObject*)base_run:(SNode*)args{
                SBool* c=(SBool*)[args First];
                args=[args Rest];
                if([c Value]){
                    return [args First];
                }else{
                    args=[args Rest];
                    if(args!=nil){
                        return [args First];
                    }else{
                        return nil;
                    }
                }
            }
            "
            run "
                return [S_IfFun base_run:args];
            "
        ]
        python [
            other "
    @staticmethod
    def base_run(args):
        if args.First():
            return args.Rest().First()
        else:
            args=args.Rest().Rest()
            if args!=None:
                return args.First()
            else:
                return None
            "
            run "
        return IfFun.base_run(args)
            "
        ]
	]
    `是否是同一个内存对象`
    eq [
        cpp [
            run "
                Base * old=args->First();
                bool eq=true;
                Node * t=args->Rest();
                while(eq && t!=NULL){
                    eq=t->First()==old;
                    old=t->First();
                    t=t->Rest();
                }
                return Bool::trans(eq);
            "
        ]
        C# [
            run "
                Object old=args.First();
                bool eq=true;
                Node<Object> t=args.Rest();
                while(eq && t!=null){
                    eq=t.First()==old;
                    old=t.First();
                    t=t.Rest();
                }
                return eq;
            "
        ]
        js [
            run "
                return eq(args,function(){return true;});
            "
        ]
        OC [
            run "
                BOOL eq=YES;
                NSObject* old=[args First];
                SNode* t=[args Rest];
                while(eq && t!=nil){
                    eq=([t First]==old);
                    old=[t First];
                    t=[t Rest];
                }
                return [SBool trans:eq];
            "
        ]
        python [
            run "
        eq=True 
        old=args.First()
        t=args.Rest()
        while(eq and t!=None):
            eq=(t.First()==old)
            old=t.First()
            t=t.Rest()
        return eq
            "
        ]
    ]
    apply [
        `
            计划这样改造这个函数，只有两个参数时，按原计划。
            N个参数时，后面的计算结果依次返回作前面的参数
        `
        cpp [
            run "
                Function *f=static_cast<Function*>(args->First());
                Node *f_args=static_cast<Node*>(args->Rest()->First());
                Base* b=f->exec(f_args);
                if(b!=NULL){
                    b->eval_release();/*从函数出来都默认加了1，故需要eval_release再传递给下一个表达式*/
                }
                return b;
            "
        ]
        C# [
            run "
                Function f=args.First() as Function;
                args=args.Rest();
                return f.exec(args.First() as Node<Object>);
            "
        ]
        js [
            run "
                var run=args.First();
                args=args.Rest();
                return run.exec(args.First());
            "
        ]

        OC [
            run "
                SFunction* f=(SFunction*)[args First];
                SNode* n_args=(SNode*)[[args Rest] First];
                NSObject* b=[f exec:n_args];
                if(b!=nil){
                    [SBase SEvalRelease:b];
                }
                return b;
            "
        ]

        python [
            run "
        f=args.First()
        n_args=args.Rest().First()
        return f.exe(n_args)
            "
        ]
    ]
]