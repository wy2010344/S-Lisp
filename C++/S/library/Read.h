#pragma once
namespace s{
	namespace library{
        class ReadFunc: public LibFunction {
        	char line_split;
        public:
            ReadFunc(char split):LibFunction(){
        		line_split=split;
        	}
            string toString(){
                return "read";
            }
            Fun_Type ftype(){
                return Function::fBuildIn;
            }
        protected:
            Base * run(Node * args){
	            String * path=static_cast<String*>(args->First());
	            return new String(file::read(path->StdStr(),line_split));
            }
        };
	}
}