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
	]

    ref-count [
        cpp [
            run "
            Base *b=args->First();
            return new Int(b->ref_count());
            "
        ]
    ]
    
    + [
        alias AddFun
        cpp [
            run "
                int all=0;
                for(Node * t=args;t!=NULL;t=t->Rest()){
                    Int * it=static_cast<Int*>(t->First());
                    all=all+it->Value();
                }
                return new Int(all);
            "
        ]
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
        js [
            run "
                return reduce(args,function(last,now){
                    return last+now;
                },0);
            "
        ]
    ]
    - [
        alias SubFun
        cpp [
            run "
                int all=static_cast<Int*>(args->First())->Value();
                args=args->Rest();
                for(Node * t=args;t!=NULL;t=t->Rest()){
                    Int * it=static_cast<Int*>(t->First());
                    all=all-it->Value();
                }
                return new Int(all);
            "
        ]
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
        js [
            run "
                var r=args.First();
                return reduce(args.Rest(),function(last,now){
                    return last-now;
                },r);
            "
        ]
    ]

    * [
        alias MultiFun
        js [
            run "
                return reduce(args,function(last,now){
                    return last*now;
                },1);
            "
        ]
    ]

    / [
        alias DivFun
        js [
            run "
                var r=args.First();
                return reduce(args.Rest(),function(last,now){
                    return last/now;
                },r);
            "
        ]
    ]

    parseInt [
        js [
            run "
                return parseInt(args.First());
            "
        ]
    ]

    >  [
        alias MBiggerFun
        cpp [
            run "
                bool ret=true;
                Int* last=static_cast<Int*>(args->First());
                args=args->Rest();
                while(args!=NULL && ret){
                    Int* current=static_cast<Int*>(args->First());
                    ret=(last->Value()>current->Value());
                    last=current;
                    args=args->Rest();
                }
                return Bool::trans(ret);
            "
        ]
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
        js [
            run "
                //数字
                return compare(args,check_is_number,function(last,now){
                    return (last>now);
                });
            "
        ]
    ]

    < [
        alias MSmallerFun
        cpp [
            run "
                bool ret=true;
                Int* last=static_cast<Int*>(args->First());
                args=args->Rest();
                while(args!=NULL && ret){
                    Int* current=static_cast<Int*>(args->First());
                    ret=(last->Value()<current->Value());
                    last=current;
                    args=args->Rest();
                }
                return Bool::trans(ret);
            "
        ]
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
        js [
            run "
                //数字
                return compare(args,check_is_number,function(last,now){
                    return (last<now);
                });
            "
        ]
    ]

    = [
        alias MEqFun
        cpp [
            other "
            static bool base_run(Node* args){
                bool ret=true;
                Int* last=static_cast<Int*>(args->First());
                args=args->Rest();
                while(args!=NULL && ret){
                    Int* current=static_cast<Int*>(args->First());
                    ret=(last->Value()==current->Value());
                    last=current;
                    args=args->Rest();
                }
                return ret;
            }
            "
            run "
                return Bool::trans(base_run(args));
            "
        ]
        C# [
            other "
            public static bool base_run(Node<Object> args){
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
            }
            "
            run "
                return base_run(args);
            "
        ]
        js [
            other "
                MEqFun.base_run=function(args){
                    return eq(args,check_is_number);
                }
            "
            run "
                return MEqFun.base_run(args);
            "
        ]
    ]
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
    ]

    str-substr [
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
            "
            var a=args.First();
            var split="";
            if(args.Rest()!=null){
                split=args.Rest().First();
            }
            return a.split(split);
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
    ]
    str-trim [
        js [
            run "
                var str=args.First();
                return str.trim();
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