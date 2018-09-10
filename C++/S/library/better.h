
#pragma once
#include "./system.h"
namespace s{
    namespace library{
        

            class Kvs_extendFunc: public LibFunction {
            private:
                static Kvs_extendFunc * _in_;
            public:    
                static Kvs_extendFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "{ ( let ( k v kvs ) args ) ( extend k ( extend v kvs ) ) }";
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
            Kvs_extendFunc* Kvs_extendFunc::_in_=new Kvs_extendFunc();
            

            class Kvs_find1stFunc: public LibFunction {
            private:
                static Kvs_find1stFunc * _in_;
            public:    
                static Kvs_find1stFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "{ ( let ( key kvs ) args find1st this ) ( let ( k v ...kvs ) args ) ( if-run ( str-eq k key ) { v } { find1st key kvs } ) }";
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
            Kvs_find1stFunc* Kvs_find1stFunc::_in_=new Kvs_find1stFunc();
            

            class ReduceFunc: public LibFunction {
            private:
                static ReduceFunc * _in_;
            public:    
                static ReduceFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "reduce";
                }
                Fun_Type ftype(){
                    return Function::fBuildIn;
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
            ReduceFunc* ReduceFunc::_in_=new ReduceFunc();
            

            class If_runFunc: public LibFunction {
            private:
                static If_runFunc * _in_;
            public:    
                static If_runFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "{ ( let ( a b c ) args ) ( let x ( default ( if a b c ) ) ) ( x ) }";
                }
                Fun_Type ftype(){
                    return Function::fUser;
                }
            protected:
                Base * run(Node * args){
                    
                Bool * cond=static_cast<Bool*>(args->First());
                args=args->Rest();
                Function * trueR=static_cast<Function*>(args->First());
                args=args->Rest();
                Base * b=NULL;
                if(cond->Value()){
                    b=trueR->exec(NULL);
                }else{
                    if(args!=NULL){
                        Function *theF=static_cast<Function*>(args->First());
                        b=theF->exec(NULL);
                    }
                }
                /*从函数出来都加了1，release*/
                if(b!=NULL){
                    b->eval_release();
                }
                return b;
            
                }
            };
            If_runFunc* If_runFunc::_in_=new If_runFunc();
            

            class DefaultFunc: public LibFunction {
            private:
                static DefaultFunc * _in_;
            public:    
                static DefaultFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "{ ( let ( a d ) args ) ( if ( exist? a ) a d ) }";
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
            DefaultFunc* DefaultFunc::_in_=new DefaultFunc();
            

            class Empty_funFunc: public LibFunction {
            private:
                static Empty_funFunc * _in_;
            public:    
                static Empty_funFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "{ }";
                }
                Fun_Type ftype(){
                    return Function::fUser;
                }
            protected:
                Base * run(Node * args){
                    
                return NULL;
            
                }
            };
            Empty_funFunc* Empty_funFunc::_in_=new Empty_funFunc();
            

            class ReverseFunc: public LibFunction {
            private:
                static ReverseFunc * _in_;
            public:    
                static ReverseFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "reverse";
                }
                Fun_Type ftype(){
                    return Function::fBuildIn;
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
            ReverseFunc* ReverseFunc::_in_=new ReverseFunc();
            

            class IstypeFunc: public LibFunction {
            private:
                static IstypeFunc * _in_;
            public:    
                static IstypeFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "type?";
                }
                Fun_Type ftype(){
                    return Function::fBuildIn;
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
            IstypeFunc* IstypeFunc::_in_=new IstypeFunc();
            

            class QuoteFunc: public LibFunction {
            private:
                static QuoteFunc * _in_;
            public:    
                static QuoteFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "{ ( first args ) }";
                }
                Fun_Type ftype(){
                    return Function::fUser;
                }
            protected:
                Base * run(Node * args){
                    
            	return args->First();
			
                }
            };
            QuoteFunc* QuoteFunc::_in_=new QuoteFunc();
            

            class ListFunc: public LibFunction {
            private:
                static ListFunc * _in_;
            public:    
                static ListFunc*instance(){
                    return _in_;
                }
                string toString(){
                    return "{ args }";
                }
                Fun_Type ftype(){
                    return Function::fUser;
                }
            protected:
                Base * run(Node * args){
                    
                return args;
			
                }
            };
            ListFunc* ListFunc::_in_=new ListFunc();
            
        Node * better(){
            Node * m=library();
            
            m=kvs::extend("kvs-extend",Kvs_extendFunc::instance(),m);
            m=kvs::extend("kvs-find1st",Kvs_find1stFunc::instance(),m);
            m=kvs::extend("reduce",ReduceFunc::instance(),m);
            m=kvs::extend("if-run",If_runFunc::instance(),m);
            m=kvs::extend("default",DefaultFunc::instance(),m);
            m=kvs::extend("empty-fun",Empty_funFunc::instance(),m);
            m=kvs::extend("reverse",ReverseFunc::instance(),m);
            m=kvs::extend("type?",IstypeFunc::instance(),m);
            m=kvs::extend("quote",QuoteFunc::instance(),m);
            m=kvs::extend("list",ListFunc::instance(),m);
            return m;
        }
    }
};