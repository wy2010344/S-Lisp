
#pragma once
namespace s{
    namespace system{
            

        class FirstFun: public LibFunction {
        private:
            static FirstFun * _in_;
        public:    
            static FirstFun*instance(){
                return _in_;
            }
            string toString(){
                return "first";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                return (static_cast<Node *>(args->First()))->First();
			
            }
        };
        FirstFun* FirstFun::_in_=new FirstFun();
        

        class RestFun: public LibFunction {
        private:
            static RestFun * _in_;
        public:    
            static RestFun*instance(){
                return _in_;
            }
            string toString(){
                return "rest";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
				return (static_cast<Node *>(args->First()))->First();
			
            }
        };
        RestFun* RestFun::_in_=new RestFun();
        

        class ExtendFun: public LibFunction {
        private:
            static ExtendFun * _in_;
        public:    
            static ExtendFun*instance(){
                return _in_;
            }
            string toString(){
                return "extend";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
				return new Node(args->First(),static_cast<Node*>(args->Rest()->First()));
			
            }
        };
        ExtendFun* ExtendFun::_in_=new ExtendFun();
        

        class LengthFun: public LibFunction {
        private:
            static LengthFun * _in_;
        public:    
            static LengthFun*instance(){
                return _in_;
            }
            string toString(){
                return "length";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                return new Int(((Node *)args->First())->Length());
			
            }
        };
        LengthFun* LengthFun::_in_=new LengthFun();
        

        class Ref_countFun: public LibFunction {
        private:
            static Ref_countFun * _in_;
        public:    
            static Ref_countFun*instance(){
                return _in_;
            }
            string toString(){
                return "ref-count";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
            Base *b=args->First();
            return new Int(b->ref_count());
            
            }
        };
        Ref_countFun* Ref_countFun::_in_=new Ref_countFun();
        

        class AddFun: public LibFunction {
        private:
            static AddFun * _in_;
        public:    
            static AddFun*instance(){
                return _in_;
            }
            string toString(){
                return "+";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                int all=0;
                for(Node * t=args;t!=NULL;t=t->Rest()){
                    Int * it=static_cast<Int*>(t->First());
                    all=all+it->Value();
                }
                return new Int(all);
            
            }
        };
        AddFun* AddFun::_in_=new AddFun();
        

        class SubFun: public LibFunction {
        private:
            static SubFun * _in_;
        public:    
            static SubFun*instance(){
                return _in_;
            }
            string toString(){
                return "-";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                int all=static_cast<Int*>(args->First())->Value();
                args=args->Rest();
                for(Node * t=args;t!=NULL;t=t->Rest()){
                    Int * it=static_cast<Int*>(t->First());
                    all=all-it->Value();
                }
                return new Int(all);
            
            }
        };
        SubFun* SubFun::_in_=new SubFun();
        

        class MBiggerFun: public LibFunction {
        private:
            static MBiggerFun * _in_;
        public:    
            static MBiggerFun*instance(){
                return _in_;
            }
            string toString(){
                return ">";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
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
            
            }
        };
        MBiggerFun* MBiggerFun::_in_=new MBiggerFun();
        

        class MSmallerFun: public LibFunction {
        private:
            static MSmallerFun * _in_;
        public:    
            static MSmallerFun*instance(){
                return _in_;
            }
            string toString(){
                return "<";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
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
            
            }
        };
        MSmallerFun* MSmallerFun::_in_=new MSmallerFun();
        

        class MEqFun: public LibFunction {
        private:
            static MEqFun * _in_;
        public:    
            static MEqFun*instance(){
                return _in_;
            }
            string toString(){
                return "=";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
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
            
        protected:
            Base * run(Node * args){
                
                return Bool::trans(base_run(args));
            
            }
        };
        MEqFun* MEqFun::_in_=new MEqFun();
        

        class AndFun: public LibFunction {
        private:
            static AndFun * _in_;
        public:    
            static AndFun*instance(){
                return _in_;
            }
            string toString(){
                return "and";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                bool init=true;
                Node *t=args;
                while(t!=NULL && init){
                    Bool *b=static_cast<Bool*>(t->First());
                    init=b->Value();
                    t=t->Rest();
                }
                return Bool::trans(init);
            
            }
        };
        AndFun* AndFun::_in_=new AndFun();
        

        class OrFun: public LibFunction {
        private:
            static OrFun * _in_;
        public:    
            static OrFun*instance(){
                return _in_;
            }
            string toString(){
                return "or";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                bool init=false;
                Node *t=args;
                while(t!=NULL && (!init)){
                    Bool *b=static_cast<Bool*>(t->First());
                    init=b->Value();
                    t=t->Rest();
                }
                return Bool::trans(init);
            
            }
        };
        OrFun* OrFun::_in_=new OrFun();
        

        class NotFun: public LibFunction {
        private:
            static NotFun * _in_;
        public:    
            static NotFun*instance(){
                return _in_;
            }
            string toString(){
                return "not";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                Bool *b=static_cast<Bool*>(args->First());
                return Bool::trans(!b->Value());
            
            }
        };
        NotFun* NotFun::_in_=new NotFun();
        

        class IsemptyFun: public LibFunction {
        private:
            static IsemptyFun * _in_;
        public:    
            static IsemptyFun*instance(){
                return _in_;
            }
            string toString(){
                return "empty?";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                return Bool::trans(args->First()==NULL);
			
            }
        };
        IsemptyFun* IsemptyFun::_in_=new IsemptyFun();
        

        class IsexistFun: public LibFunction {
        private:
            static IsexistFun * _in_;
        public:    
            static IsexistFun*instance(){
                return _in_;
            }
            string toString(){
                return "exist?";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                return Bool::trans(args->First()!=NULL);
			
            }
        };
        IsexistFun* IsexistFun::_in_=new IsexistFun();
        

        class LogFun: public LibFunction {
        private:
            static LogFun * _in_;
        public:    
            static LogFun*instance(){
                return _in_;
            }
            string toString(){
                return "log";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                for (Node * tmp=args; tmp!=NULL; tmp=tmp->Rest()) {
                    Base * v=tmp->First();
                    if(v==NULL){
                        cout<<"[]";
                    }else{
                        cout<<v->toString();
                    }
                    cout<<"  ";
                }
                cout<<endl;
                return NULL;
			
            }
        };
        LogFun* LogFun::_in_=new LogFun();
        

        class IfFun: public LibFunction {
        private:
            static IfFun * _in_;
        public:    
            static IfFun*instance(){
                return _in_;
            }
            string toString(){
                return "if";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
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
            
        protected:
            Base * run(Node * args){
                
                return base_run(args);
			
            }
        };
        IfFun* IfFun::_in_=new IfFun();
        

        class EqFun: public LibFunction {
        private:
            static EqFun * _in_;
        public:    
            static EqFun*instance(){
                return _in_;
            }
            string toString(){
                return "eq";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                Base * a=args->First();
                Base * b=args->Rest()->First();
                return Bool::trans(a==b);
            
            }
        };
        EqFun* EqFun::_in_=new EqFun();
        

        class ApplyFun: public LibFunction {
        private:
            static ApplyFun * _in_;
        public:    
            static ApplyFun*instance(){
                return _in_;
            }
            string toString(){
                return "apply";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                Function *f=static_cast<Function*>(args->First());
                Node *f_args=static_cast<Node*>(args->Rest()->First());
                Base* b=f->exec(f_args);
                if(b!=NULL){
                    b->eval_release();/*从函数出来都默认加了1，故需要eval_release再传递给下一个表达式*/
                }
                return b;
            
            }
        };
        ApplyFun* ApplyFun::_in_=new ApplyFun();
        

        class StringifyFun: public LibFunction {
        private:
            static StringifyFun * _in_;
        public:    
            static StringifyFun*instance(){
                return _in_;
            }
            string toString(){
                return "stringify";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                return new String(args->First()->toString());
			
            }
        };
        StringifyFun* StringifyFun::_in_=new StringifyFun();
        

        class TypeFun: public LibFunction {
        private:
            static TypeFun * _in_;
        public:    
            static TypeFun*instance(){
                return _in_;
            }
            string toString(){
                return "type";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                Base *b=args->First();
                string s;
                if(b==NULL){
                    s="list";
                }else{
                    Base::S_Type t=b->stype();
                    if(t==Base::sList){
                        s="list";
                    }else
                    if(t==Base::sFunction){
                        s="function";
                    }else
                    if(t==Base::sInt){
                        s="int";
                    }else
                    if(t==Base::sString){
                        s="string";
                    }else
                    if(t==Base::sBool){
                        s="bool";
                    }else
                    if(t==Base::sUser){
                        s="user";
                    }else
                    {
                        if(t==Base::sToken){
                            s="token";
                        }else
                        if(t==Base::sExp){
                            s="exp";
                        }else
                        if(t==Base::sLocation){
                            s="location";
                        }
                    }
                }

                return new String(s);
            
            }
        };
        TypeFun* TypeFun::_in_=new TypeFun();
        

        class Str_eqFun: public LibFunction {
        private:
            static Str_eqFun * _in_;
        public:    
            static Str_eqFun*instance(){
                return _in_;
            }
            string toString(){
                return "str-eq";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                String *s1=static_cast<String*>(args->First());
                args=args->Rest();
                String *s2=static_cast<String*>(args->First());
                return Bool::trans(s1->StdStr()==s2->StdStr());
            
            }
        };
        Str_eqFun* Str_eqFun::_in_=new Str_eqFun();
        

        class Str_lengthFun: public LibFunction {
        private:
            static Str_lengthFun * _in_;
        public:    
            static Str_lengthFun*instance(){
                return _in_;
            }
            string toString(){
                return "str-length";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                String *str=static_cast<String*>(args->First());
                return new Int(str->StdStr().size());
            
            }
        };
        Str_lengthFun* Str_lengthFun::_in_=new Str_lengthFun();
        

        class Str_charAtFun: public LibFunction {
        private:
            static Str_charAtFun * _in_;
        public:    
            static Str_charAtFun*instance(){
                return _in_;
            }
            string toString(){
                return "str-charAt";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                String *str=static_cast<String*>(args->First());
                Int * i=static_cast<Int*>(args->Rest()->First());
                char x[]={str->StdStr()[i->Value()],'\0'};
                return new String(x);
            
            }
        };
        Str_charAtFun* Str_charAtFun::_in_=new Str_charAtFun();
        

        class Str_substrFun: public LibFunction {
        private:
            static Str_substrFun * _in_;
        public:    
            static Str_substrFun*instance(){
                return _in_;
            }
            string toString(){
                return "str-substr";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
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
            
            }
        };
        Str_substrFun* Str_substrFun::_in_=new Str_substrFun();
        

        class Str_joinFun: public LibFunction {
        private:
            static Str_joinFun * _in_;
        public:    
            static Str_joinFun*instance(){
                return _in_;
            }
            string toString(){
                return "str-join";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
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
                cs[size]='\0';
                string str(cs);
                delete [] cs;
                return new String(str);
            
            }
        };
        Str_joinFun* Str_joinFun::_in_=new Str_joinFun();
        

        class Str_reduce_leftFun: public LibFunction {
        private:
            static Str_reduce_leftFun * _in_;
        public:    
            static Str_reduce_leftFun*instance(){
                return _in_;
            }
            string toString(){
                return "str-reduce-left";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                String * stre=static_cast<String*>(args->First());
                args=args->Rest();
                Function * f=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * init=args->First();
                unsigned size=stre->StdStr().size();
                for(unsigned i=0;i<size;i++){
                    char c[]={stre->StdStr()[i],'\0'};
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
            
            }
        };
        Str_reduce_leftFun* Str_reduce_leftFun::_in_=new Str_reduce_leftFun();
        

        class Str_reduce_rightFun: public LibFunction {
        private:
            static Str_reduce_rightFun * _in_;
        public:    
            static Str_reduce_rightFun*instance(){
                return _in_;
            }
            string toString(){
                return "str-reduce-right";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                String * stre=static_cast<String*>(args->First());
                args=args->Rest();
                Function * f=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * init=args->First();
                unsigned size=stre->StdStr().size();
                for(unsigned i=size-1;i!=0;i--){
                    char c[]={stre->StdStr()[i],'\0'};
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
            
            }
        };
        Str_reduce_rightFun* Str_reduce_rightFun::_in_=new Str_reduce_rightFun();
        

        class QuoteFun: public LibFunction {
        private:
            static QuoteFun * _in_;
        public:    
            static QuoteFun*instance(){
                return _in_;
            }
            string toString(){
                return "{(first args ) }";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                return args->First();
            
            }
        };
        QuoteFun* QuoteFun::_in_=new QuoteFun();
        

        class ListFun: public LibFunction {
        private:
            static ListFun * _in_;
        public:    
            static ListFun*instance(){
                return _in_;
            }
            string toString(){
                return "{args }";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                return args;
			
            }
        };
        ListFun* ListFun::_in_=new ListFun();
        

        class IstypeFun: public LibFunction {
        private:
            static IstypeFun * _in_;
        public:    
            static IstypeFun*instance(){
                return _in_;
            }
            string toString(){
                return "{(let (x n ) args ) (str-eq (type x ) n ) }";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                Base *b=args->First();
                args=args->Rest();
                bool ret=false;
                string & type=static_cast<String*>(args->First())->StdStr();
                if(b==NULL){
                    ret=(type=="list");
                }else{
                    Base::S_Type t=b->stype();
                    if(t==Base::sList){
                        ret=(type=="list");
                    }else
                    if(t==Base::sFunction){
                        ret=(type=="function");
                    }else
                    if(t==Base::sInt){
                        ret=(type=="int");
                    }else
                    if(t==Base::sString){
                        ret=(type=="string");
                    }else
                    if(t==Base::sBool){
                        ret=(type=="bool");
                    }else
                    if(t==Base::sUser){
                        ret=(type=="user");
                    }else{
                        if(t==Base::sToken){
                            ret=(type=="token");
                        }else
                        if(t==Base::sExp){
                            ret=(type=="exp");
                        }else
                        if(t==Base::sLocation){
                            ret=(type=="location");
                        }
                    }
                }
                return Bool::trans(ret);
            
            }
        };
        IstypeFun* IstypeFun::_in_=new IstypeFun();
        

        class CallFun: public LibFunction {
        private:
            static CallFun * _in_;
        public:    
            static CallFun*instance(){
                return _in_;
            }
            string toString(){
                return "call";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
            
        protected:
            Base * run(Node * args){
                
                Function* f=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * b=f->exec(args);
                if(b!=NULL){
                    b->eval_release();
                }
                return b;
            
            }
        };
        CallFun* CallFun::_in_=new CallFun();
        

        class MNotEqFun: public LibFunction {
        private:
            static MNotEqFun * _in_;
        public:    
            static MNotEqFun*instance(){
                return _in_;
            }
            string toString(){
                return "{(not (apply = args ) ) }";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                return Bool::trans(!MEqFun::base_run(args));
            
            }
        };
        MNotEqFun* MNotEqFun::_in_=new MNotEqFun();
        

        class ReverseFun: public LibFunction {
        private:
            static ReverseFun * _in_;
        public:    
            static ReverseFun*instance(){
                return _in_;
            }
            string toString(){
                return "{(let (xs ) args ) (reduce xs {(let (init x ) args ) (extend x init ) } [] ) }";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                Node * list=static_cast<Node*>(args->First());
                Node *r=NULL;
                while(list!=NULL){
                    r=new Node(list->First(),r);
                    list=list->Rest();
                }
                return r;
            
            }
        };
        ReverseFun* ReverseFun::_in_=new ReverseFun();
        

        class Empty_funFun: public LibFunction {
        private:
            static Empty_funFun * _in_;
        public:    
            static Empty_funFun*instance(){
                return _in_;
            }
            string toString(){
                return "{}";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                return NULL;
            
            }
        };
        Empty_funFun* Empty_funFun::_in_=new Empty_funFun();
        

        class DefaultFun: public LibFunction {
        private:
            static DefaultFun * _in_;
        public:    
            static DefaultFun*instance(){
                return _in_;
            }
            string toString(){
                return "{(let (a d ) args ) (if (exist? a ) a d ) }";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                Base * v=args->First();
                if(v!=NULL){
                    return v;
                }else{
                    args=args->Rest();
                    Base * d=args->First();
                    return d;
                }
            
            }
        };
        DefaultFun* DefaultFun::_in_=new DefaultFun();
        

        class If_runFun: public LibFunction {
        private:
            static If_runFun * _in_;
        public:    
            static If_runFun*instance(){
                return _in_;
            }
            string toString(){
                return "{(let (a b c ) args ) (let x (default (if a b c ) ) ) (x ) }";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                Base * fun=IfFun::base_run(args);
                Base * b=NULL;
                if(fun!=NULL){
                    b=static_cast<Function*>(fun)->exec(NULL);
                    if(b!=NULL){
                        b->eval_release();
                    }
                }
                return b;
            
            }
        };
        If_runFun* If_runFun::_in_=new If_runFun();
        

        class LoopFun: public LibFunction {
        private:
            static LoopFun * _in_;
        public:    
            static LoopFun*instance(){
                return _in_;
            }
            string toString(){
                return "{(let (f init ) args loop this ) (let (will init ) (f init ) ) (if-run will {(loop f init ) } {init } ) }";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                Function * f=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * init=NULL;
                if(args!=NULL){
                    init=args->First();
                }
                bool will=true;
                while(will){
                    Node * o=static_cast<Node*>(f->exec(list::extend(init,NULL)));
                    will=static_cast<Bool*>(o->First())->Value();
                    init=o->Rest()->First();
                    if(init!=NULL){
                        init->retain();
                        o->release();
                        init->eval_release();
                    }else{
                        o->release();
                    }
                }
                return init;
            
            }
        };
        LoopFun* LoopFun::_in_=new LoopFun();
        

        class ReduceFun: public LibFunction {
        private:
            static ReduceFun * _in_;
        public:    
            static ReduceFun*instance(){
                return _in_;
            }
            string toString(){
                return "{(let (xs run init ) args reduce this ) (if-run (exist? xs ) {(let (x ...xs ) xs ) (let init (run init x ) ) (reduce xs run init ) } {init } ) }";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                Node *list=static_cast<Node*>(args->First());
                args=args->Rest();
                Function *f=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * init=args->First();
                while(list!=NULL){
                    Base * x=list->First();
                    Node *nargs=new Node(init,new Node(x,NULL));
                    nargs->retain();
                    Base* n_init=f->exec(nargs);
                    nargs->release();
                    if(n_init!=NULL){
                        n_init->eval_release();
                    }
                    init=n_init;
                    list=list->Rest();
                }
                return init;
            
            }
        };
        ReduceFun* ReduceFun::_in_=new ReduceFun();
        

        class Kvs_find1stFun: public LibFunction {
        private:
            static Kvs_find1stFun * _in_;
        public:    
            static Kvs_find1stFun*instance(){
                return _in_;
            }
            string toString(){
                return "{(let (key kvs ) args find1st this ) (let (k v ...kvs ) args ) (if-run (str-eq k key ) {v } {(find1st key kvs ) } ) }";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                Node* kvs_map=static_cast<Node*>(args->First());
                args=args->Rest();
                String* key=static_cast<String*>(args->First());
                return kvs::find1st(kvs_map,key->StdStr());
			
            }
        };
        Kvs_find1stFun* Kvs_find1stFun::_in_=new Kvs_find1stFun();
        

        class Kvs_extendFun: public LibFunction {
        private:
            static Kvs_extendFun * _in_;
        public:    
            static Kvs_extendFun*instance(){
                return _in_;
            }
            string toString(){
                return "{(let (k v kvs ) args ) (extend k (extend v kvs ) ) }";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            
        protected:
            Base * run(Node * args){
                
                String* key=static_cast<String*>(args->First());
                args=args->Rest();
                Base* val=args->First();
                args=args->Rest();
                Node* kvs_map=static_cast<Node*>(args->First());
                return kvs::extend(key,val,kvs_map);
			
            }
        };
        Kvs_extendFun* Kvs_extendFun::_in_=new Kvs_extendFun();
        
        Node * library(){
            Node * m=NULL;
            m=kvs::extend("true",Bool::True,m);
            m=kvs::extend("false",Bool::False,m);
            
            m=kvs::extend("first",FirstFun::instance(),m);
            m=kvs::extend("rest",RestFun::instance(),m);
            m=kvs::extend("extend",ExtendFun::instance(),m);
            m=kvs::extend("length",LengthFun::instance(),m);
            m=kvs::extend("ref-count",Ref_countFun::instance(),m);
            m=kvs::extend("+",AddFun::instance(),m);
            m=kvs::extend("-",SubFun::instance(),m);
            m=kvs::extend(">",MBiggerFun::instance(),m);
            m=kvs::extend("<",MSmallerFun::instance(),m);
            m=kvs::extend("=",MEqFun::instance(),m);
            m=kvs::extend("and",AndFun::instance(),m);
            m=kvs::extend("or",OrFun::instance(),m);
            m=kvs::extend("not",NotFun::instance(),m);
            m=kvs::extend("empty?",IsemptyFun::instance(),m);
            m=kvs::extend("exist?",IsexistFun::instance(),m);
            m=kvs::extend("log",LogFun::instance(),m);
            m=kvs::extend("if",IfFun::instance(),m);
            m=kvs::extend("eq",EqFun::instance(),m);
            m=kvs::extend("apply",ApplyFun::instance(),m);
            m=kvs::extend("stringify",StringifyFun::instance(),m);
            m=kvs::extend("type",TypeFun::instance(),m);
            m=kvs::extend("str-eq",Str_eqFun::instance(),m);
            m=kvs::extend("str-length",Str_lengthFun::instance(),m);
            m=kvs::extend("str-charAt",Str_charAtFun::instance(),m);
            m=kvs::extend("str-substr",Str_substrFun::instance(),m);
            m=kvs::extend("str-join",Str_joinFun::instance(),m);
            m=kvs::extend("str-reduce-left",Str_reduce_leftFun::instance(),m);
            m=kvs::extend("str-reduce-right",Str_reduce_rightFun::instance(),m);
            m=kvs::extend("quote",QuoteFun::instance(),m);
            m=kvs::extend("list",ListFun::instance(),m);
            m=kvs::extend("type?",IstypeFun::instance(),m);
            m=kvs::extend("call",CallFun::instance(),m);
            m=kvs::extend("!=",MNotEqFun::instance(),m);
            m=kvs::extend("reverse",ReverseFun::instance(),m);
            m=kvs::extend("empty-fun",Empty_funFun::instance(),m);
            m=kvs::extend("default",DefaultFun::instance(),m);
            m=kvs::extend("if-run",If_runFun::instance(),m);
            m=kvs::extend("loop",LoopFun::instance(),m);
            m=kvs::extend("reduce",ReduceFun::instance(),m);
            m=kvs::extend("kvs-find1st",Kvs_find1stFun::instance(),m);
            m=kvs::extend("kvs-extend",Kvs_extendFun::instance(),m);
            return m;
        }
    };
};