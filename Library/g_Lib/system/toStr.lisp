[
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
            sb.append(System.toString(tmp.First(),True))
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
]