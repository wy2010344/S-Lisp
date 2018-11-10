[
    str-eq [
        cpp [
            run "
                String *s1=static_cast<String*>(args->First());
                args=args->Rest();
                String *s2=static_cast<String*>(args->First());
                return Bool::trans(s1->StdStr()==s2->StdStr());
            "
        ]
        C# [
            run "
                String a=args.First() as String;
                args=args.Rest();
                String b=args.First() as String;
                return a==b;
            "
        ]
        js [
            run "
                return eq(args,function(s){
                    if(s && s.constructor==String){
                        return true;
                    }else{
                        return false;
                    }
                });
            "
        ]
    ]
    str-length [
        cpp [
            run "
                String *str=static_cast<String*>(args->First());
                return new Int(str->StdStr().size());
            "
        ] 
        C# [
            run "
                String a=args.First() as String;
                return a.Length;
            "
        ]
        js [
            run "
                var str=args.First();
                return str.length;
            "
        ]
        python [
            run "
        return len(args.First())
            "
        ]
    ]
    str-charAt [
        cpp [
            run "
                String *str=static_cast<String*>(args->First());
                Int * i=static_cast<Int*>(args->Rest()->First());
                char x[]={str->StdStr()[i->Value()],'\\0'};
                return new String(x);
            "
        ]
        C# [
            run "
                String a=args.First() as String;
                args=args.Rest();
                int b=(int)args.First();
                return \"\"+a[b];
            "
        ]
        js [
            run "
                var str=args.First();
                args=args.Rest();
                var index=args.First();  
                return str[index];
            "
        ]
        python [
            run "
        return args.First()[args.Rest().First()]
            "
        ]
    ]

    str-substr [
        `
        参数1：字符串
        参数2：开始位置
        参数3：长度
        `
        cpp [
            run "
                String * stre=static_cast<String*>(args->First());
                args=args->Rest();
                Int * begin=static_cast<Int*>(args->First());
                args=args->Rest();
                if(args==NULL){
                    return new String(stre->StdStr().substr(begin->Value()));
                }else{
                    Int * len=static_cast<Int*>(args->First());
                    return new String(stre->StdStr().substr(begin->Value(),len->Value()));
                }
            "
        ]
        C# [
            run "
                String a=args.First() as String;
                args=args.Rest();
                int begin=(int)args.First();
                args=args.Rest();
                if(args==null)
                {
                    return a.Substring(begin);
                }else
                {
                    return a.Substring(begin,(int)args.First());
                }
            "
        ]
        js [
            run "
                var a=args.First();
                args=args.Rest();
                var begin=args.First();
                args=args.Rest();
                if(args==null){
                    return a.substr(begin);
                }else{
                    return a.substr(begin,args.First());
                }
            "
        ]
        python [
            run "
        a=args.First()
        args=args.Rest()
        begin=args.First()
        args=args.Rest()
        if args==None:
            return a[begin:len(a)]
        else:
            return a[begin:(begin+args.First())]
            "
        ]
    ]
    str-join [
        cpp [
            run "
                Node * vs=static_cast<Node*>(args->First());
                args=args->Rest();
                int split_size=0;
                String *split=NULL;
                if(args!=NULL)
                {
                    split=static_cast<String*>(args->First());
                    split_size=split->StdStr().size();
                }
                int size=0;
                for(Node *t=vs;t!=NULL;t=t->Rest())
                {
                    String * s=static_cast<String*>(t->First());
                    size+=s->StdStr().size()+split_size;
                }
                size=size-split_size;
                char *cs=new char[size+1];

                int d=0;
                for(Node *t=vs;t!=NULL;t=t->Rest())
                {
                    String * s=static_cast<String*>(t->First());
                    for(unsigned i=0;i<s->StdStr().size();i++)
                    {
                        cs[d]=s->StdStr()[i];
                        d++;
                    }
                    if(t->Rest()!=NULL && split_size!=0)
                    {
                        for(int i=0;i<split_size;i++){
                            cs[d]=split->StdStr()[i];
                            d++;
                        }
                    }
                }
                cs[size]='\\0';
                string str(cs);
                delete [] cs;
                return new String(str);
            "
        ]
        C# [
            run "
                Node<Object> vs=args.First() as Node<Object>;
                args=args.Rest();
                String split=\"\";
                if(args!=null)
                {
                    split=args.First() as String;
                }
                StringBuilder sb=new StringBuilder();
                for(Node<Object> tmp=vs;tmp!=null;tmp=tmp.Rest())
                {
                    sb.Append(tmp.First() as String);
                    if(tmp.Rest()!=null)
                    {
                        sb.Append(split);
                    }
                }
                return sb.ToString();
            "
        ]
        js [
            run "
                //字符串
                var array=args.First();
                var split=\"\";
                if(args.Rest()!=null){
                    split=args.Rest().First();
                }
                var r=\"\";
                for(var t=array;t!=null;t=t.Rest()){
                    r=r+t.First()+split;
                }
                return r.substr(0,r.length-split.length);
            "
        ]
        python [
            run "
        array=args.First()
        split=\"\";
        args=args.Rest()
        if args!=None:
            split=args.First()
        sb=[]
        while array!=None:
            sb.append(array.First())
            array=array.Rest()
        return split.join(sb)
            "
        ]
    ]
    str-split [
        C# [
            run "
                String str = args.First() as String;
                args = args.Rest();
                String split = \"\";
                if (args != null)
                {
                    split = args.First() as String;
                }
                Node<Object> r = null;
                if (split == \"\")
                {
                    for (int i = str.Length-1; i>-1; i--)
                    {
                        r = Node<Object>.extend(str[i] + \"\", r);
                    }
                }
                else
                {
                    int last_i = 0;
                    while (last_i >-1)
                    {
                        int new_i = str.IndexOf(split, last_i);
                        if (new_i > -1)
                        {
                            r = Node<Object>.extend(str.Substring(last_i, new_i - last_i), r);
                            last_i = new_i+split.Length;
                        }
                        else
                        {
                            //最后
                            r = Node<Object>.extend(str.Substring(last_i), r);
                            last_i = new_i;
                        }
                    }
                    r = Node<Object>.reverse(r);
                }
                return r;
            "
        ]
        js [
            run "
                var a=args.First();
                var split=\"\";
                args=args.Rest()
                if(args!=null){
                    split=args.First();
                }
                return a.split(split);
            "
        ]

        python [
            run "
        a=args.First()
        split=\"\"
        args=args.Rest()
        if args!=None:
            split=args.First()
        if split==\"\":
            sb=[]
            i=0
            len_a=len(a)
            while i<len_a:
                sb.append(a[i])
                i=i+1
            return sb
        else:
            return a.split(split)
            "
        ]
    ]
    str-upper[
        C# [
            run "
                return (args.First() as String).ToUpper();
            "
        ]
        js [
            run "
                return args.First().toUpperCase();
            "
        ]
        python [
            run "
        return args.First().upper()
            "
        ]
    ]
    str-lower[
        C# [
            run "
                return (args.First() as String).ToLower();
            "
        ]
        js [
            run "
                return args.First().toLowerCase();
            "
        ]
        python [
            run "
        return args.First().lower()
            "
        ]
    ]
    str-trim [
        js [
            run "
                var str=args.First();
                return str.trim();
            "
        ]
        python [
            run "
        return args.First().strip()
            "
        ]
    ]
    str-indexOf [
        js [
            run "
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.indexOf(v);
            "
        ]
        python [
            run "
        stre=args.First()
        args=args.Rest()
        v=args.First()
        return stre.find(v)
            "
        ]
    ]
    str-lastIndexOf [
        js [
            run "
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.lastIndexOf(v);
            "
        ]
    ]
    str-startsWith [
        js [
            run "
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.startsWith(v);
            "
        ]
    ]
    str-endsWith [
        js [
            run "
                var str=args.First();
                args=args.Rest();
                var v=args.First();
                return str.endsWith(v);
            "
        ]
    ]
    str-reduce-left [
        cpp [
            run "
                String * stre=static_cast<String*>(args->First());
                args=args->Rest();
                Function * f=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * init=args->First();
                unsigned size=stre->StdStr().size();
                for(unsigned i=0;i<size;i++){
                    char c[]={stre->StdStr()[i],'\\0'};
                    String *cs=new String(string(c));
                    Int* is=new Int(i);
                    Node *targs=new Node(init,new Node(cs,new Node(is,NULL)));
                    targs->retain();
                    Base *new_init=f->exec(targs);
                    targs->release();
                    if(new_init!=NULL){
                        new_init->eval_release();
                    }
                    init=new_init;
                }
                return init;
            "
        ]
    ]
    `参考str-reduce-left`
    str-reduce-right [
        cpp [
            run "
                String * stre=static_cast<String*>(args->First());
                args=args->Rest();
                Function * f=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * init=args->First();
                unsigned size=stre->StdStr().size();
                for(unsigned i=size-1;i!=0;i--){
                    char c[]={stre->StdStr()[i],'\\0'};
                    String *cs=new String(string(c));
                    Int* is=new Int(i);
                    Node *targs=new Node(init,new Node(cs,new Node(is,NULL)));
                    targs->retain();
                    Base *new_init=f->exec(targs);
                    targs->release();
                    if(new_init!=NULL){
                        new_init->eval_release();
                    }
                    init=new_init;
                }
                return init;
            "
        ]
    ]
]