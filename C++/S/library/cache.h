#pragma once
namespace s{
	namespace library{
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
            Fun_Type ftype(){
                return Function::fCache;
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
            Fun_Type ftype(){
                return Function::fBuildIn;
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
	}
}