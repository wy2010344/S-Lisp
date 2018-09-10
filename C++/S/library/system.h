
#pragma once
#include "./buildIn.h"
namespace s{
    namespace library{
            

            class WriteFunc: public LibFunction {
            private:
                static WriteFunc * _in_;
            public:    
                static WriteFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "write";
                }
                Fun_Type ftype(){
                    return Function::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
                String * path=static_cast<String*>(args->First());
                args=args->Rest();
                String * content=static_cast<String*>(args->First());
                file::write(path->StdStr(),content->StdStr());
                return NULL;
            
                }
            };
            WriteFunc* WriteFunc::_in_=new WriteFunc();
            

            class ReadFunc: public LibFunction {
            private:
                static ReadFunc * _in_;
            public:    
                static ReadFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "read";
                }
                Fun_Type ftype(){
                    return Function::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
                String * path=static_cast<String*>(args->First());
                return new String(file::read(path->StdStr(),line_splits));
            
                }
            };
            ReadFunc* ReadFunc::_in_=new ReadFunc();
            

            class TypeFunc: public LibFunction {
            private:
                static TypeFunc * _in_;
            public:    
                static TypeFunc*instance(){
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
            TypeFunc* TypeFunc::_in_=new TypeFunc();
            

            class StringifyFunc: public LibFunction {
            private:
                static StringifyFunc * _in_;
            public:    
                static StringifyFunc*instance(){
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
            StringifyFunc* StringifyFunc::_in_=new StringifyFunc();
            

            class ApplyFunc: public LibFunction {
            private:
                static ApplyFunc * _in_;
            public:    
                static ApplyFunc*instance(){
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
            ApplyFunc* ApplyFunc::_in_=new ApplyFunc();
            

            class Str_eqFunc: public LibFunction {
            private:
                static Str_eqFunc * _in_;
            public:    
                static Str_eqFunc*instance(){
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
            Str_eqFunc* Str_eqFunc::_in_=new Str_eqFunc();
            

            class EqFunc: public LibFunction {
            private:
                static EqFunc * _in_;
            public:    
                static EqFunc*instance(){
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
            EqFunc* EqFunc::_in_=new EqFunc();
            

            class Char_atFunc: public LibFunction {
            private:
                static Char_atFunc * _in_;
            public:    
                static Char_atFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "char-at";
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
            Char_atFunc* Char_atFunc::_in_=new Char_atFunc();
            

            class Str_lengthFunc: public LibFunction {
            private:
                static Str_lengthFunc * _in_;
            public:    
                static Str_lengthFunc*instance(){
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
            Str_lengthFunc* Str_lengthFunc::_in_=new Str_lengthFunc();
            

            class Str_joinFunc: public LibFunction {
            private:
                static Str_joinFunc * _in_;
            public:    
                static Str_joinFunc*instance(){
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
                cs[size]='\0';
                string str(cs);
                delete [] cs;
                return new String(str);
			
                }
            };
            Str_joinFunc* Str_joinFunc::_in_=new Str_joinFunc();
            

            class IfFunc: public LibFunction {
            private:
                static IfFunc * _in_;
            public:    
                static IfFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "if";
                }
                Fun_Type ftype(){
                    return Function::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
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
			
                }
            };
            IfFunc* IfFunc::_in_=new IfFunc();
            

            class LogFunc: public LibFunction {
            private:
                static LogFunc * _in_;
            public:    
                static LogFunc*instance(){
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
            LogFunc* LogFunc::_in_=new LogFunc();
            

            class IsexistFunc: public LibFunction {
            private:
                static IsexistFunc * _in_;
            public:    
                static IsexistFunc*instance(){
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
            IsexistFunc* IsexistFunc::_in_=new IsexistFunc();
            

            class IsemptyFunc: public LibFunction {
            private:
                static IsemptyFunc * _in_;
            public:    
                static IsemptyFunc*instance(){
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
            IsemptyFunc* IsemptyFunc::_in_=new IsemptyFunc();
            

            class Str_reduce_rightFunc: public LibFunction {
            private:
                static Str_reduce_rightFunc * _in_;
            public:    
                static Str_reduce_rightFunc*instance(){
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
            Str_reduce_rightFunc* Str_reduce_rightFunc::_in_=new Str_reduce_rightFunc();
            

            class Str_reduce_leftFunc: public LibFunction {
            private:
                static Str_reduce_leftFunc * _in_;
            public:    
                static Str_reduce_leftFunc*instance(){
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
            Str_reduce_leftFunc* Str_reduce_leftFunc::_in_=new Str_reduce_leftFunc();
            

            class NotFunc: public LibFunction {
            private:
                static NotFunc * _in_;
            public:    
                static NotFunc*instance(){
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
            NotFunc* NotFunc::_in_=new NotFunc();
            

            class OrFunc: public LibFunction {
            private:
                static OrFunc * _in_;
            public:    
                static OrFunc*instance(){
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
            OrFunc* OrFunc::_in_=new OrFunc();
            

            class AndFunc: public LibFunction {
            private:
                static AndFunc * _in_;
            public:    
                static AndFunc*instance(){
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
            AndFunc* AndFunc::_in_=new AndFunc();
            

            class MEqFunc: public LibFunction {
            private:
                static MEqFunc * _in_;
            public:    
                static MEqFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "=";
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
                    ret=(last->Value()==current->Value());
                    last=current;
                    args=args->Rest();
                }
                return Bool::trans(ret);
            
                }
            };
            MEqFunc* MEqFunc::_in_=new MEqFunc();
            

            class MSmallerFunc: public LibFunction {
            private:
                static MSmallerFunc * _in_;
            public:    
                static MSmallerFunc*instance(){
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
            MSmallerFunc* MSmallerFunc::_in_=new MSmallerFunc();
            

            class MBiggerFunc: public LibFunction {
            private:
                static MBiggerFunc * _in_;
            public:    
                static MBiggerFunc*instance(){
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
            MBiggerFunc* MBiggerFunc::_in_=new MBiggerFunc();
            

            class SubFunc: public LibFunction {
            private:
                static SubFunc * _in_;
            public:    
                static SubFunc*instance(){
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
            SubFunc* SubFunc::_in_=new SubFunc();
            

            class AddFunc: public LibFunction {
            private:
                static AddFunc * _in_;
            public:    
                static AddFunc*instance(){
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
            AddFunc* AddFunc::_in_=new AddFunc();
            

            class Retain_countFunc: public LibFunction {
            private:
                static Retain_countFunc * _in_;
            public:    
                static Retain_countFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "retain-count";
                }
                Fun_Type ftype(){
                    return Function::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
            Base *b=args->First();
            return new Int(b->count);
            
                }
            };
            Retain_countFunc* Retain_countFunc::_in_=new Retain_countFunc();
            

            class LengthFunc: public LibFunction {
            private:
                static LengthFunc * _in_;
            public:    
                static LengthFunc*instance(){
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
            LengthFunc* LengthFunc::_in_=new LengthFunc();
            

            class ExtendFunc: public LibFunction {
            private:
                static ExtendFunc * _in_;
            public:    
                static ExtendFunc*instance(){
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
            ExtendFunc* ExtendFunc::_in_=new ExtendFunc();
            

            class RestFunc: public LibFunction {
            private:
                static RestFunc * _in_;
            public:    
                static RestFunc*instance(){
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
            RestFunc* RestFunc::_in_=new RestFunc();
            

            class FirstFunc: public LibFunction {
            private:
                static FirstFunc * _in_;
            public:    
                static FirstFunc*instance(){
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
            FirstFunc* FirstFunc::_in_=new FirstFunc();
            
        Node * library(){
            Node * m=buildIn();
            
            m=kvs::extend("write",WriteFunc::instance(),m);
            m=kvs::extend("read",ReadFunc::instance(),m);
            m=kvs::extend("type",TypeFunc::instance(),m);
            m=kvs::extend("stringify",StringifyFunc::instance(),m);
            m=kvs::extend("apply",ApplyFunc::instance(),m);
            m=kvs::extend("str-eq",Str_eqFunc::instance(),m);
            m=kvs::extend("eq",EqFunc::instance(),m);
            m=kvs::extend("char-at",Char_atFunc::instance(),m);
            m=kvs::extend("str-length",Str_lengthFunc::instance(),m);
            m=kvs::extend("str-join",Str_joinFunc::instance(),m);
            m=kvs::extend("if",IfFunc::instance(),m);
            m=kvs::extend("log",LogFunc::instance(),m);
            m=kvs::extend("exist?",IsexistFunc::instance(),m);
            m=kvs::extend("empty?",IsemptyFunc::instance(),m);
            m=kvs::extend("str-reduce-right",Str_reduce_rightFunc::instance(),m);
            m=kvs::extend("str-reduce-left",Str_reduce_leftFunc::instance(),m);
            m=kvs::extend("not",NotFunc::instance(),m);
            m=kvs::extend("or",OrFunc::instance(),m);
            m=kvs::extend("and",AndFunc::instance(),m);
            m=kvs::extend("=",MEqFunc::instance(),m);
            m=kvs::extend("<",MSmallerFunc::instance(),m);
            m=kvs::extend(">",MBiggerFunc::instance(),m);
            m=kvs::extend("-",SubFunc::instance(),m);
            m=kvs::extend("+",AddFunc::instance(),m);
            m=kvs::extend("retain-count",Retain_countFunc::instance(),m);
            m=kvs::extend("length",LengthFunc::instance(),m);
            m=kvs::extend("extend",ExtendFunc::instance(),m);
            m=kvs::extend("rest",RestFunc::instance(),m);
            m=kvs::extend("first",FirstFunc::instance(),m);
            return m;
        }
    };
};