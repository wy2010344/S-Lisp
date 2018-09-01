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

    retain-count [
        cpp [
            run "
            Base *b=args->First();
            return new Int(b->count);
            "
        ]
    ]
    
    + [
        alias AddFunc
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
    ]
    - [
        alias SubFunc
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
    ]

    >  [
        alias MBiggerFunc
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
    ]

    < [
        alias MSmallerFunc
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
    ]

    = [
        alias MEqFunc
        cpp [
            run "
                bool ret=true;
                Int* last=static_cast<Int*>(args->First());
                args=args->Rest();
                while(args!=NULL && ret){
                    Int* current=static_cast<Int*>(args->First());
                    ret=(last->Value()==current->Value());
                    last=current;
                    args=args->Rest();
                }
                return Bool::trans(ret);
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
    ]

    not [
        cpp [
            run "
                Bool *b=static_cast<Bool*>(args->First());
                return Bool::trans(!b->Value());
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
	`判断列表为空，应该只支持列表和空才对`
	empty? [
		cpp [
			run "
                return Bool::trans(args->First()==NULL);
			"
		]
	]
	exist? [
		cpp [
			run "
                return Bool::trans(args->First()!=NULL);
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
                return Bool::trans(a==b);
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
                //空也是列表，判断空用empty?或exist?
                return Bool::trans(f==NULL || f->stype()==Base::sList);
			"
		]
	]
	function? [
		cpp [
			run "
                Base * f=args->First();
                return Bool::trans(f!=NULL && f->stype()==Base::sFunction);
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