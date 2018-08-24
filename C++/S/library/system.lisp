[
	`first函数`
	first [
		cpp [
			run "
                return (static_cast<Node *>(args->First()))->First();
			"
		]
	]
	rest [
		cpp [
			run "
				return (static_cast<Node *>(args->First()))->First();
			"
		]
	]
	extend [
		cpp [
			run "
				return new Node(args->First(),static_cast<Node*>(args->Rest()->First()));
			"
		]
	]
	length [
		cpp [
			run "
                return new Int(((Node *)args->First())->Length());
			"
		]
	]
	`判断列表为空，应该只支持列表和空才对`
	empty? [
		cpp [
			run "
                if(args->First()==NULL){
                    return Bool::True;
                }else{
                    return Bool::False;
                }
			"
		]
	]
	exist? [
		cpp [
			run "
                if(args->First()==NULL){
                    return Bool::False;
                }else{
                    return Bool::True;
                }
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
	]
	if [
		cpp [
			run "
                Bool * cond=static_cast<Bool*>(args->First());
                Base * ret=NULL;
                if (cond==Bool::True) {
                    ret=args->Rest()->First();
                }else{
                    args=args->Rest()->Rest();
                    if(args!=NULL){
                        ret=args->First();
                    }
                }
                return ret;
			"
		]
	]
	str-join [
		cpp [
			run "
                Node * vs=static_cast<Node*>(args->First());
                Node * split_base=args->Rest();
                int split_size=0;
                String *split=NULL;
                if(split_base!=NULL)
                {
                    split=static_cast<String*>(split_base->First());
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
	]
	str-length [
		cpp [
			run "
                String *str=static_cast<String*>(args->First());
                return new Int(str->StdStr().size());
			"
		]
	]
	char-at [
		cpp [
			run "
                String *str=static_cast<String*>(args->First());
                Int * i=static_cast<Int*>(args->Rest()->First());
                char x[]={str->StdStr()[i->Value()],'\\0'};
                return new String(x);
			"
		]
	]
    `是否是同一个内存对象`
    eq [
        cpp [
            run "
                Base * a=args->First();
                Base * b=args->Rest()->First();
                if(a==b){
                    return Bool::True;
                }else{
                    return Bool::False;
                }
            "
        ]
    ]
	str-eq [
		cpp [
			run "
                String *s1=static_cast<String*>(args->First());
                args=args->Rest();
                String *s2=static_cast<String*>(args->First());
                if(s1->StdStr()==s2->StdStr())
                {
                    return Bool::True;
                }else{
                    return Bool::False;
                }
			"
		]
	]
	`apply函数`
    apply [
        cpp [
            run "
                Function *f=static_cast<Function*>(args->First());
                Node *f_args=static_cast<Node*>(args->Rest()->First());
                return f->exec(f_args);
            "
        ]
    ]
	stringify [
		cpp [
			run "
                return new String(args->First()->toString());
			"
		]
	]
	list? [
		cpp [
			run "
                Base * f=args->First();
                if(f==NULL){
                    return Bool::False;
                }else{
                    if(dynamic_cast<Node*>(f)==NULL)
                    {
                        return Bool::False;
                    }else{
                        return Bool::True;
                    }
                }
			"
		]
	]
	function? [
		cpp [
			run "
                Base * f=args->First();
                if(f==NULL){
                    return Bool::False;
                }else{
                    if(dynamic_cast<Function*>(f)==NULL)
                    {
                        return Bool::False;
                    }else{
                        return Bool::True;
                    }
                }
			"
		]
	]

    read [
        cpp [
            run "
                String * path=static_cast<String*>(args->First());
                return new String(file::read(path->StdStr()));
            "
        ]
    ]

    write [
        cpp [
            run "
                String * path=static_cast<String*>(args->First());
                args=args->Rest();
                String * content=static_cast<String*>(args->First());
                file::write(path->StdStr(),content->StdStr());
                return NULL;
            "
        ]
    ]
]