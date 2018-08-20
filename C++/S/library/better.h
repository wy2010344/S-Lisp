
#pragma once
namespace s{
    namespace library{
        

        class Kvs_extendFunc: public LibFunction {
        public:    
            string toString(){
                return "{ ( let ( k v kvs ) args ) ( extend k ( extend v kvs ) ) }";
            }
            Function_type ftype(){
                return Function_type::fBetter;
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

        class Kvs_find1stFunc: public LibFunction {
        public:    
            string toString(){
                return "{ ( let ( key kvs ) args find1st this ) ( let ( k v ...kvs ) args ) ( if-run ( str-eq k key ) { v } { find1st key kvs } ) }";
            }
            Function_type ftype(){
                return Function_type::fBetter;
            }
        protected:
            Base * run(Node * args){
                
                Node* kvs_map=static_cast<Node*>(args->First());
                args=args->Rest();
                String* key=static_cast<String*>(args->First());
                return kvs::find1st(kvs_map,key->StdStr());
			
            }
        };

        class QuoteFunc: public LibFunction {
        public:    
            string toString(){
                return "{ ( first args ) }";
            }
            Function_type ftype(){
                return Function_type::fBetter;
            }
        protected:
            Base * run(Node * args){
                
            	return args->First();
			
            }
        };

        class ListFunc: public LibFunction {
        public:    
            string toString(){
                return "{ args }";
            }
            Function_type ftype(){
                return Function_type::fBetter;
            }
        protected:
            Base * run(Node * args){
                
                return args;
			
            }
        };
        Node * better(Node *m){
            
                            m=kvs::extend("kvs-extend",new Kvs_extendFunc(),m);
                            m=kvs::extend("kvs-find1st",new Kvs_find1stFunc(),m);
                            m=kvs::extend("quote",new QuoteFunc(),m);
                            m=kvs::extend("list",new ListFunc(),m);
            return m;
        }
    }
};