[
    first [
        C# [
            run "
                return (args.First() as Node<Object>).First();
            "
        ]
    ]
    rest [
        C# [
            run "
                return (args.First() as Node<Object>).Rest();
            "
        ]
    ]
    extend [
        C# [
            run "
                return Node<Object>.extend(args.First(),(args.Rest().First() as Node<Object>));
            "
        ]
    ]
    
    length [
        C# [
            run "
                return (args.First() as Node<Object>).Length();
            "
        ]
    ]
    
    + [
        alias AddFun
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
    ]
    
    - [
        alias SubFun
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
    ]
    
    > [
        alias MBiggerFunc
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
    ]
    
    < [
        alias MSmaiierFun
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
    ]
    = [
        alias MEqFunc
        C# [
            run "
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
            "
        ]
    ]
    and [
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
    ]
    
    or [
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
    ]
    
    not [
        C# [
            run "
                return !(bool)args.First();
            "
        ]
    ]
    
    empty? [
        C# [
            run "
                return args.First()==null;
            "
        ]
    ]
    
    exist? [
        C# [
            run "
                return args.First()!=null;
            "
        ]
    ]
    
    log [
        C# [
            run "
                StringBuilder sb = new StringBuilder();
                args.toString(sb);
                Console.WriteLine(sb.ToString());
                return null;
            "
        ]
    ]
    
    if [
        C# [
            run "
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
            "
        ]
    ]
    
    eq [
        C# [
            run "
                Object a=args.First();
                args=args.Rest();
                Object b=args.First();
                return a==b;
            "
        ]
    ]
    
    apply [
        C# [
            run "
                Function f=args.First() as Function;
                args=args.Rest();
                return f.exec(args);
            "
        ]
    ]
    
    stringify [
        C# [
            run "
                StringBuilder sb=new StringBuilder();
                Node<Object>.toString(sb, args.First(), false);
                return sb.ToString();
            "
        ]
    ]
    
    type [
        C# [
            run "
                Object b=args.First();
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
            "
        ]
    ]
    
    str-eq [
        C# [
            run "
                String a=args.First() as String;
                args=args.Rest();
                String b=args.First() as String;
                return a==b;
            "
        ]
    ]
    str-length [
        C# [
            run "
                String a=args.First() as String;
                return a.Length;
            "
        ]
    ]
    str-charAt [
        C# [
            run "
                String a=args.First() as String;
                args=args.Rest();
                int b=(int)args.First();
                return \"\"+a[b];
            "
        ]
    ]
    
    str-substr [
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
    ]
    
    str-join [
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
    ]
]