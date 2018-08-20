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
            Function_type ftype(){
                return Function_type::fBetter;
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
        /*带有缓存性质的函数，副作用的集中*/
        class CacheReturnFunc:public LibFunction{
        public:
            CacheReturnFunc(){
                this->cache_c=NULL;
            }
            virtual ~CacheReturnFunc(){
                if(this->cache_c!=NULL)
                {
                    this->cache_c->release();
                }
            }
            string toString(){
                return "[]";
            }
            Function_type ftype(){
                return Function_type::fCache;
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
        /*生成缓存性质的函数，本质上可被用户函数替代，嵌套，但这个是根*/
        class CacheFunc:public Function{
        public:
            Function_type ftype(){
                return Function_type::fBuildIn;
            }
            string toString(){
                return "cache";
            }
            Base * exec(Node *args){
                CacheReturnFunc* crf=new CacheReturnFunc();
                crf->retain();
                crf->exec(args);
                return crf;
            }
        };
        Node* buildIn(){
            Node *m=NULL;
            m=kvs::extend("true", Bool::True,m);
            m=kvs::extend("false", Bool::False,m);
            m=kvs::extend("cache",new CacheFunc(),m);
            return m;
        }
    };
};