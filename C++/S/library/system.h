
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
                String * path=static_cast<String*>(args->First());
                return new String(file::read(path->StdStr()));
            
                }
            };
            ReadFunc* ReadFunc::_in_=new ReadFunc();
            

            class IsfunctionFunc: public LibFunction {
            private:
                static IsfunctionFunc * _in_;
            public:    
                static IsfunctionFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "function?";
                }
                Function_type ftype(){
                    return Function_type::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
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
			
                }
            };
            IsfunctionFunc* IsfunctionFunc::_in_=new IsfunctionFunc();
            

            class IslistFunc: public LibFunction {
            private:
                static IslistFunc * _in_;
            public:    
                static IslistFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "list?";
                }
                Function_type ftype(){
                    return Function_type::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
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
			
                }
            };
            IslistFunc* IslistFunc::_in_=new IslistFunc();
            

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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
                String *s1=static_cast<String*>(args->First());
                args=args->Rest();
                String *s2=static_cast<String*>(args->First());
                if(s1->StdStr()==s2->StdStr())
                {
                    return Bool::True;
                }else{
                    return Bool::False;
                }
			
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
                Base * a=args->First();
                Base * b=args->Rest()->First();
                if(a==b){
                    return Bool::True;
                }else{
                    return Bool::False;
                }
            
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
                if(args->First()==NULL){
                    return Bool::False;
                }else{
                    return Bool::True;
                }
			
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
                if(args->First()==NULL){
                    return Bool::True;
                }else{
                    return Bool::False;
                }
			
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
                }
            protected:
                Base * run(Node * args){
                    
                Bool *b=static_cast<Bool*>(args->First());
                if(b->Value()){
                    return Bool::False;
                }else{
                    return Bool::True;
                }
            
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                if(init){
                    return Bool::True;
                }else{
                    return Bool::False;
                }
            
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                if(init){
                    return Bool::True;
                }else{
                    return Bool::False;
                }
            
                }
            };
            AndFunc* AndFunc::_in_=new AndFunc();
            

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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
                Function_type ftype(){
                    return Function_type::fBuildIn;
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
            m=kvs::extend("function?",IsfunctionFunc::instance(),m);
            m=kvs::extend("list?",IslistFunc::instance(),m);
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