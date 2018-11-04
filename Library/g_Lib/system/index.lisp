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
	log [
		cpp [
			run "
                for (Node * tmp=args; tmp!=NULL; tmp=tmp->Rest()) {
                    Base * v=tmp->First();
                    cout<<system::toString(v,true)<<\" \";
                }
                cout<<endl;
                return NULL;
			"
		]
        C# [
            run "
                StringBuilder sb = new StringBuilder();
                for(Node<Object> t=args;t!=null;t=t.Rest()){
                    sb.Append(System.toString(t.First(),true)).Append(\" \");
                }
                Console.WriteLine(sb.ToString());
                return null;
            "
        ]
        js [
            run "
                var cs=[];
                for(var t=args;t!=null;t=t.Rest()){
                    cs.push(p.toString(t.First(),true));
                }
                p.log(cs);
            "
        ]
        OC [
            run "
                NSMutableString* str=[NSMutableString new];
                for (SNode* t=args; t!=nil; t=[t Rest]) {
                    [str appendString:[SSystem toString:[t First] trans:YES]];
                    [str appendString:@\" \"];
                }
                NSLog(@\"%@\",str);
                [SBase SEvalRelease:str];
                return nil;
            "
        ]
        python [
            run "
        sb=[]
        tmp=args
        while tmp!=None:
            sb.append(str(tmp.First()))
            sb.append(\" \")
            tmp=tmp.Rest()
        print(\"\".join(sb))
        return None
            "
        ]
	]
    toString [
        `
        String->不变
        Int->String
        Bool->String
        List->[]
        Function->{}
        `
        cpp [
            run "
                Base* b=args->First();
                return new String(system::toString(b,false));
            "
        ]

        C# [
            run "
                Object b=args.First();
                return System.toString(b,false);
            "
        ]

        js [
            run "
                var b=args.First();
                return p.toString(b,false);
            "
        ]

        OC [
            run "
                NSObject* b=[args First];
                return [SSystem toString:b trans:NO];
            "
        ]

        python [
            run "
        b=args.First()
        return System.toString(b,False)
            "
        ]
    ]
    stringify [
        `
            和log不同，log如果是字符串，会加上引号log函数是多个，返回值显示是1个。
            log函数对每一个转换，不是单纯的列表内
            只是转换成字符串类型
        `
        cpp [
            run "
                Base* b=args->First();
                return new String(system::toString(b,true));
            "
        ]
        C# [
            run "
                Object b=args.First();
                return System.toString(b,true);
            "
        ]
        js [
            run "
                var b=args.First();  
                return p.toString(b,true);
            "
        ]

        OC [
            run "
                NSObject* b=[args First];
                return [SSystem toString:b trans:YES];
            "
        ]
        python [
            run "
        b=args.First()
        return System.toString(b,True)
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
    ]
    type [
        cpp [
            other 
            ({
                (let (init def out release)
                    (reduce 
                        [
                            list
                            string
                            function
                            int
                            bool
                            user
                            token
                            exp
                            location
                        ]
                        {
                            (let ((init def out release) v) args)
                            (list
                                (extend (str-join ["s_" 'v "=new String(\"" 'v "\");" "s_" 'v "->retain();"]) init)
                                (extend (str-join ["String* s_" 'v ";"]) def)
                                (extend (str-join ["String* S_" 'v "(){ return s_" 'v ";}"]) out)
                                (extend (str-join ["s_" 'v "->release();"]) release)
                            )
                        }
                        [[] [] [] []]
                    )
                )
                (str-join
                    [
                        "TypeFun(){"
                            (str-join init "\n")
                        "}"
                        "~TypeFun(){"
                            (str-join release "\n")
                        "}"
                        (str-join out "\n")
                        "
                        String* base_run(Base* b){
                            String* s;
                            if(b==NULL){
                                s=s_list;
                            }else{
                                Base::S_Type t=b->stype();
                                if(t==Base::sList){
                                    s=s_list;
                                }else
                                if(t==Base::sFunction){
                                    s=s_function;
                                }else
                                if(t==Base::sInt){
                                    s=s_int;
                                }else
                                if(t==Base::sString){
                                    s=s_string;
                                }else
                                if(t==Base::sBool){
                                    s=s_bool;
                                }else
                                if(t==Base::sUser){
                                    s=s_user;
                                }else
                                {
                                    if(t==Base::sToken){
                                        s=s_token;
                                    }else
                                    if(t==Base::sExp){
                                        s=s_exp;
                                    }else
                                    if(t==Base::sLocation){
                                        s=s_location;
                                    }
                                }
                            }
                            return s;
                        }
                        private:
                        "
                        (str-join def "\n")
                    ]
                    "\n"
                )
            })
            run "
                Base *b=args->First();
                return base_run(b);
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
        OC [
            other "
            +(NSString*)base_run:(NSObject*)n{
                if(n==nil){
                    return @\"list\";
                }else{
                    if([n isKindOfClass:[SNode class]]){
                        return @\"list\";
                    }else
                    if([n isKindOfClass:[SFunction class]]){
                        return @\"function\";
                    }else
                    if([n isKindOfClass:[SBool class]]){
                        return @\"bool\";
                    }else
                    if([n isKindOfClass:[NSString class]]){
                        return @\"string\";
                    }else
                    if([n isKindOfClass:[NSNumber class]]){
                        return @\"int\";
                    }else{
                        return @\"\";
                    }
                }
            }
            "
            run "
                return [S_TypeFun base_run:[args First]];
            "
        ]
    ]
]