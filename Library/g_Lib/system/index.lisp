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
	]
	log [
		cpp [
			run "
                for (Node * tmp=args; tmp!=NULL; tmp=tmp->Rest()) {
                    Base * v=tmp->First();
                    if(v==NULL){
                        cout<<\"[]\";
                    }else{
                        cout<<v->toString();
                    }
                    cout<<\"  \";
                }
                cout<<endl;
                return NULL;
			"
		]
        C# [
            run "
                StringBuilder sb = new StringBuilder();
                args.toString(sb);
                Console.WriteLine(sb.ToString());
                return null;
            "
        ]
        js [
            run "
                var cs=[];
                for(var t=args;t!=null;t=t.Rest()){
                    cs.push(p.log_trans(t.First()));
                }
                p.log(cs);
            "
        ]
        OC [
            run "
                NSLog(@\"%@\",[SNode description:args]);
                return nil;
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
                return [IfFun base_run:args];
            "
        ]
	]
    `是否是同一个内存对象`
    eq [
        cpp [
            run "
                Base * a=args->First();
                Base * b=args->Rest()->First();
                return Bool::trans(a==b);
            "
        ]
        C# [
            run "
                Object a=args.First();
                args=args.Rest();
                Object b=args.First();
                return a==b;
            "
        ]
        js [
            run "
                return eq(args,function(){return true;});
            "
        ]
    ]
	`apply函数`
    apply [
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
    ]
	stringify [
		cpp [
			run "
                return new String(args->First()->toString());
			"
		]
        C# [
            run "
                StringBuilder sb=new StringBuilder();
                Node<Object>.toString(sb, args.First(), false);
                return sb.ToString();
            "
        ]
        js [
            run "
                //类似于JSON.stringify，没想好用toString还是stringify;
                return args.First().toString();  
            "
        ]
	]
    type [
        cpp [
            run "
                Base *b=args->First();
                string s;
                if(b==NULL){
                    s=\"list\";
                }else{
                    Base::S_Type t=b->stype();
                    if(t==Base::sList){
                        s=\"list\";
                    }else
                    if(t==Base::sFunction){
                        s=\"function\";
                    }else
                    if(t==Base::sInt){
                        s=\"int\";
                    }else
                    if(t==Base::sString){
                        s=\"string\";
                    }else
                    if(t==Base::sBool){
                        s=\"bool\";
                    }else
                    if(t==Base::sUser){
                        s=\"user\";
                    }else
                    {
                        if(t==Base::sToken){
                            s=\"token\";
                        }else
                        if(t==Base::sExp){
                            s=\"exp\";
                        }else
                        if(t==Base::sLocation){
                            s=\"location\";
                        }
                    }
                }

                return new String(s);
            "
        ]
        C# [
            other "
                public static String base_run(Object b){
                    if(b==null){
                        return \"list\";
                    }else{
                        if(b is Node<Object>)
                        {
                            return \"list\";
                        }else if(b is Function)
                        {
                            return \"function\";
                        }else if(b is int)
                        {
                            return \"int\";
                        }else if(b is String)
                        {
                            return \"string\";
                        }else if(b is bool)
                        {
                            return \"bool\";
                        }else{
                            if(b is Token)
                            {
                                return \"token\";
                            }else if(b is Exp)
                            {
                                return \"exp\";
                            }else if(b is Location)
                            {
                                return \"location\";
                            }else{
                                return \"user\";
                            }
                        }
                    }
                }
            "
            run "
                Object b=args.First();
                return base_run(b);
            "
        ]
        js [
            other "
                TypeFun.base_run=function(n){
                    if(n==null){
                        return \"list\";
                    }else{
                        if(p.isList(n)){
                            return \"list\";
                        }else
                        if(p.isFun(n)){
                            return \"function\";
                        }else{
                            var t=typeof(n);
                            if(t==\"string\"){
                                return \"string\";
                            }else
                            if(t==\"boolean\"){
                                return \"bool\";
                            }else
                            if(t==\"number\"){
                                if(n%1===0){
                                    return \"int\";
                                }else{
                                    return \"float\";
                                }
                            }else{
                                return t;
                            }
                        }
                    }
                }
            "
            run "
                var n=args.First();
                return TypeFun.base_run(n);
            "
        ]
    ]
]