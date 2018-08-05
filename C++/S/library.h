#pragma once
#include <sstream>
using namespace std;
namespace s{
    class Int:public Base{
    private:
        int value;
        string cache;
    public:
        Int(string & k):Base(){
            this->value=atoi(k.c_str());
            this->cache=k;
        }
        Int(int k):Base(){
            this->value=k;
            stringstream stream;
            stream<<k;
            this->cache=stream.str();
        }
        virtual string toString(){
            return cache;
        }
        int Value(){
            return value;
        }
    };
    class Bool:public Base{
    private:
        bool value;
        Bool(bool v):Base(){
            this->value=v;
        }
    public:
        bool Value(){
            return value;
        }
        string toString(){
            if(value){
                return "$bool:true";
            }else{
                return "$bool:false";
            }
        }
        static Bool * True;
        static Bool * False;
    };
    Bool * Bool::True=new Bool(true);
    Bool * Bool::False=new Bool(false);
    Base *interpret(Exp * e,Node * scope);
    class Function:public Base{
    public:
        Function():Base(){}
        virtual Base * exec(Node * args)=0;
        string toString(){
            return "$function";
        }
    };
    class LibFunction:public Function{
    public:
        LibFunction():Function(){}
        virtual Base * exec(Node* args)
        {
            Base* ret=run(args);
            if(ret!=NULL)
            {
                ret->retain();
            }
            return ret;
        }
        string toString(){
            return "$function:lib";
        }
    protected:
        virtual Base * run(Node * args)=0;
    };
    namespace library{
        class MatchFunc:public LibFunction{
            Node * kvs_map;
        public:
            MatchFunc(Node *kvs_map):LibFunction(){
                this->kvs_map=kvs_map;
                this->kvs_map->retain();
            }
            virtual ~MatchFunc(){
                kvs_map->release();
            }
            string toString(){
                return "$function:match";
            }
        protected:
            Base * run(Node * args){
                String* key=static_cast<String*>(args->First());
                return kvs::find1st(kvs_map,key->StdStr());
            }
        };
        
        class FirstFunc:public LibFunction{
        public:
            string toString(){
                return "$function:first";
            }
        protected://接受一个数组参数
            Base * run(Node * args){
                return (static_cast<Node *>(args->First()))->First();
            }
        };
        class RestFunc:public LibFunction{
        public:
            string toString(){
                return "$function:rest";
            }
        protected://接受一个数组参数
            Base * run(Node * args){
                return (static_cast<Node *>(args->First()))->Rest();
            }
        };
        class LengthFunc:public LibFunction{
        public:
            string toString(){
                return "$function:length";
            }
        protected:
            Base * run(Node * args){
                return new Int(((Node *)args->First())->Length());
            }
        };
        class EmptyFunc:public LibFunction{
        public:
            string toString(){
                return "$function:empty";
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
        class LogFunc:public LibFunction{
        public:
            string toString(){
                return "$function:log";
            }
        protected:
            Base * run(Node * args){
                for (Node * tmp=args; tmp!=NULL; tmp=tmp->Rest()) {
                    Base * v=tmp->First();
                    if(v==NULL){
                        cout<<"null";
                    }else{
                        cout<<v->toString();
                    }
                    cout<<"  ";
                }
                cout<<endl;
                return NULL;
            }
        };
        class IfFunc:public LibFunction{
        public:
            string toString(){
                return "$function:if";
            }
        protected:
            Base * run(Node * args){
                Bool * cond=static_cast<Bool*>(args->First());
                Base * ret=NULL;
                if (cond==Bool::True) {
                    ret=args->Rest()->First();
                }else{
                    ret=args->Rest()->Rest()->First();
                }
                return ret;
            }
        };
        class QuoteFunc:public LibFunction{
        public:
            string toString(){
                return "$function:quote";
            }
        protected:
            Base * run(Node * args){
                return args->First();
            }
        };
        class KVSFind1stFunc:public LibFunction{
        public:
            string toString(){
                return "$function:kvs-find1st";
            }
        protected:
            Base * run(Node *args){
                Node* kvs_map=static_cast<Node*>(args->First());
                args=args->Rest();
                String* key=static_cast<String*>(args->First());
                return kvs::find1st(kvs_map,key->StdStr());
            }
        };
        class KVSExtendFunc:public LibFunction{
        public:
            string toString(){
                return "$function:kvs-extend";
            }
        protected:
            Base * run(Node *args){
                String* key=static_cast<String*>(args->First());
                args=args->Rest();
                Base* val=args->First();
                args=args->Rest();
                Node* kvs_map=static_cast<Node*>(args->First());
                return kvs::extend(key,val,kvs_map);
            }
        };
        class StrJoinFunc:public LibFunction{
        public:
            string toString(){
                return "$function:str-join";
            }
        protected:
            Base * run(Node *args){
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
                char *cs=new char[size+1];

                int d=0;
                for(Node *t=vs;t!=NULL;t=t->Rest())
                {
                    String * s=static_cast<String*>(t->First());
                    for(int i=0;i<s->StdStr().size();i++)
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
        class StrLengthFunc:public LibFunction{
        public:
            string toString(){
                return "$function:str-length";
            }
        protected:
            Base * run(Node *args){
                String *str=static_cast<String*>(args->First());
                return new Int(str->StdStr().size());
            }
        };
        class CharAtFunc:public LibFunction{
        public:
            string toString(){
                return "$function:char-at";
            }
        protected:
            Base * run(Node *args){
                String *str=static_cast<String*>(args->First());
                Int * i=static_cast<Int*>(args->Rest()->First());
                return new Char(str->StdStr()[i->Value()]);
            }
        };
        class StrEqualFunc:public LibFunction{
        public:
            string toString(){
                return "$function:str-equal";
            }
        protected:
            Base * run(Node *args){
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
        /*也与quote对应*/
        class ListFunc:public LibFunction{
        public:
            string toString(){
                return "$function:list";
            }
        protected:
            Base * run(Node *args){
                return args;
            }
        };
        class ToStringFunc:public LibFunction{
        public:
            string toString(){
                return "$function:toString";
            }
        protected:
            Base * run(Node *args){
                return new String(args->First()->toString());
            }
        };
        class IsListFunc:public LibFunction{
        public:
            string toString(){
                return "$function:list?";
            }
        protected:
            Base * run(Node *args){
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
        class IsFuncFunc:public LibFunction{
        public:
            string toString(){
                return "$function:function?";
            }
        protected:
            Base * run(Node *args){
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
        class CacheReturnFunc:public LibFunction{
        public:
            CacheReturnFunc():LibFunction(){
                this->cache_c=NULL;
            }
            virtual ~CacheReturnFunc(){
                if(this->cache_c!=NULL)
                {
                    this->cache_c->release();
                }
            }
            string toString(){
                cout<<this->count<<"  "<<this->cache_c<<endl;
                return "$function:cache-run";
            }
        protected:
            Base * run(Node *args){
                Base *r=NULL;
                if(args==NULL){
                    r=this->cache_c;
                }else
                {
                    Base *ncache=args->First();
                    if(this->cache_c!=ncache){
                        if(this->cache_c!=NULL)
                        {
                            this->cache_c->release();
                        }

                        if(ncache!=NULL)
                        {
                            ncache->retain();
                        }
                        this->cache_c=ncache;
                    }
                }
                return r;
            }
            Base *cache_c;
        };
        class CacheFunc:public Function{
        public:
            Base * exec(Node *args){
                CacheReturnFunc* crf=new CacheReturnFunc();
                crf->retain();
                crf->exec(args);
                return crf;
            }
            string toString(){
                return "$function:cache";
            }
        };
        Node * library(){
            Node * m=NULL;
            m=kvs::extend("true", Bool::True,m);
            m=kvs::extend("false", Bool::False,m);
            m=kvs::extend("quote",new QuoteFunc(),m);
            m=kvs::extend("first",new FirstFunc(),m);
            m=kvs::extend("rest", new RestFunc(),m);
            m=kvs::extend("length", new LengthFunc(),m);
            m=kvs::extend("empty", new EmptyFunc(),m);
            m=kvs::extend("log", new LogFunc(),m);
            m=kvs::extend("if", new IfFunc(),m);
            m=kvs::extend("kvs-find1st",new KVSFind1stFunc(),m);
            m=kvs::extend("kvs-extend",new KVSExtendFunc(),m);
            m=kvs::extend("str-join",new StrJoinFunc(),m);
            m=kvs::extend("str-length",new StrLengthFunc(),m);
            m=kvs::extend("charAt",new CharAtFunc(),m);
            m=kvs::extend("str-eq",new StrEqualFunc(),m);
            m=kvs::extend("list",new ListFunc(),m);
            m=kvs::extend("toString",new ToStringFunc(),m);
            m=kvs::extend("list?",new IsListFunc(),m);
            m=kvs::extend("function?",new IsFuncFunc(),m);
            m=kvs::extend("cache",new CacheFunc(),m);
            return m;
        }
    };
};