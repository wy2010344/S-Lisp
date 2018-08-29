
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
                Function_type ftype(){
                    return Function_type::fUser;
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
                Function_type ftype(){
                    return Function_type::fUser;
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
                Function_type ftype(){
                    return Function_type::fUser;
                }
            protected:
                Base * run(Node * args){
                    
                Function * If=IfFunc::instance();
                Base*  run=If->exec(args);
                if(run!=NULL){
                    Base * b=(static_cast<Function*>(run))->exec(NULL);
                    run->release();/*从函数出来都加了1，release*/
                    b->eval_release();/*从函数出来都加了1*/
                    return b;
                }else{
                    return NULL;
                }
            
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
                Function_type ftype(){
                    return Function_type::fUser;
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
                Function_type ftype(){
                    return Function_type::fUser;
                }
            protected:
                Base * run(Node * args){
                    
                return NULL;
            
                }
            };
            Empty_funFunc* Empty_funFunc::_in_=new Empty_funFunc();
            

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
                Function_type ftype(){
                    return Function_type::fUser;
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
                Function_type ftype(){
                    return Function_type::fUser;
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
            m=kvs::extend("if-run",If_runFunc::instance(),m);
            m=kvs::extend("default",DefaultFunc::instance(),m);
            m=kvs::extend("empty-fun",Empty_funFunc::instance(),m);
            m=kvs::extend("quote",QuoteFunc::instance(),m);
            m=kvs::extend("list",ListFunc::instance(),m);
            return m;
        }
    }
};