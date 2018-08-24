
#pragma once
#include "./buildIn.h"
#include "./better.h"
namespace s{
    namespace library{


        class WriteFunc: public LibFunction {
        public:
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

        class ReadFunc: public LibFunction {
        public:
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

        class IsfunctionFunc: public LibFunction {
        public:
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

        class IslistFunc: public LibFunction {
        public:
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

        class StringifyFunc: public LibFunction {
        public:
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

        class ApplyFunc: public LibFunction {
        public:
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
                return f->exec(f_args);

            }
        };

        class Str_eqFunc: public LibFunction {
        public:
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

        class EqFunc: public LibFunction {
        public:
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

        class Char_atFunc: public LibFunction {
        public:
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

        class Str_lengthFunc: public LibFunction {
        public:
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

        class Str_joinFunc: public LibFunction {
        public:
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

        class IfFunc: public LibFunction {
        public:
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

        class LogFunc: public LibFunction {
        public:
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

        class IsexistFunc: public LibFunction {
        public:
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

        class IsemptyFunc: public LibFunction {
        public:
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

        class LengthFunc: public LibFunction {
        public:
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

        class ExtendFunc: public LibFunction {
        public:
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

        class RestFunc: public LibFunction {
        public:
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

        class FirstFunc: public LibFunction {
        public:
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
        Node * library(){
            Node * m=buildIn();

            m=kvs::extend("write",new WriteFunc(),m);
            m=kvs::extend("read",new ReadFunc(),m);
            m=kvs::extend("function?",new IsfunctionFunc(),m);
            m=kvs::extend("list?",new IslistFunc(),m);
            m=kvs::extend("stringify",new StringifyFunc(),m);
            m=kvs::extend("apply",new ApplyFunc(),m);
            m=kvs::extend("str-eq",new Str_eqFunc(),m);
            m=kvs::extend("eq",new EqFunc(),m);
            m=kvs::extend("char-at",new Char_atFunc(),m);
            m=kvs::extend("str-length",new Str_lengthFunc(),m);
            m=kvs::extend("str-join",new Str_joinFunc(),m);
            m=kvs::extend("if",new IfFunc(),m);
            m=kvs::extend("log",new LogFunc(),m);
            m=kvs::extend("exist?",new IsexistFunc(),m);
            m=kvs::extend("empty?",new IsemptyFunc(),m);
            m=kvs::extend("length",new LengthFunc(),m);
            m=kvs::extend("extend",new ExtendFunc(),m);
            m=kvs::extend("rest",new RestFunc(),m);
            m=kvs::extend("first",new FirstFunc(),m);
            return better(m);
        }
    };
};
