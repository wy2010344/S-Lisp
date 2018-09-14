#pragma once
namespace s{
	namespace library{
        /*这个函数本质上是可被用户函数替换的，但又要被匹配所使用*/
        class MatchFunc:public LibFunction{
            Node * kvs_map;
        public:
            string toString(){
                return "{(let (k) args) (kvs-find1st kvs k)}";
            }
            Fun_Type ftype(){
                return Function::fUser;
            }
            MatchFunc(Node *kvs_map){
                this->kvs_map=kvs_map;
                this->kvs_map->retain();
            }
            virtual ~MatchFunc(){
                kvs_map->release();
            }
        protected:
            Base * run(Node * args){
                String* key=static_cast<String*>(args->First());
                return kvs::find1st(kvs_map,key->StdStr());
            }
        };
	}
}