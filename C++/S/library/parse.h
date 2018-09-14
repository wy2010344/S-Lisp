#pragma once

namespace s{
	namespace library{
		class ParseFunc:public Function{
	    private:
	        Node *defaultScope;
            char line_split;
	    public:
	        string toString(){
	            return "parse";
	        }
	        Fun_Type ftype(){
	            return Function::fBuildIn;
	        }
	        ParseFunc(Node *defaultScope,char split):Function(){
	            this->defaultScope=defaultScope;
                line_split=split;
	        }
	        Base * exec(Node *args){
	            String * str=static_cast<String*>(args->First());
	            args=args->Rest();
	            Node * scope=NULL;
	            if(args==NULL)
	            {
	                scope=defaultScope;
	            }else{
	                scope=static_cast<Node*>(args->First());
	            }
	            Node * tokens=Tokenize().run(str->StdStr(),line_split);
	            tokens->retain();
	            BracketExp * exp=Parse(tokens);
	            exp->retain();
	            UserFunction * f=new UserFunction(exp,scope);
	            f->retain();

	            Base *base=f->exec(NULL);

	            f->release();
	            exp->release();
	            tokens->release();
	            return base;
	            /*
	            有点难，晚些再实现。
	            */
	            //return str;
	        }
	    };
	}
}