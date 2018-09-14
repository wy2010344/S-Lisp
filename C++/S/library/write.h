#pragma once
namespace s{
	namespace library{
        class WriteFunc: public LibFunction {
        public:
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
	}
}