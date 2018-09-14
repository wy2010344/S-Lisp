#pragma once
#ifdef WIN32
#include <direct.h>
#else
#include <unistd.h>
#endif
namespace s{
	namespace library{
	    class LoadFunc:public LibFunction{
	        string base_path;
	        Node *baseScope;
	        static bool onLoad;
	        char line_split;
	    public:
	        string toString(){
	            return "load";
	        }
	        Fun_Type ftype(){
	            return Function::fBuildIn;
	        }
	        LoadFunc(string path,Node *scope,char split){
	            base_path=path;
	            baseScope=scope;
	            line_split=split;
	        }
	        static Node * core;
	        static Base *run_e(string file_path,Node *baseScope,char line_split)
	        {
	            if(onLoad){
	                throw new DefinedException("禁止在加载期间加载，避免循环加载的问题");
	            }else{
	                Node * x=static_cast<Node*>(kvs::find1st(core,file_path));
	                if(x!=NULL)
	                {
	                    return x->First();
	                }else{
	                    onLoad=true;
	                    string sb=file::read(file_path,line_split);
	                    Node* scope=kvs::extend(new String("load"),new LoadFunc(file_path,baseScope,line_split),baseScope);
	                    Node *tokens=Tokenize().run(sb,line_split);
	                    tokens->retain();
	                    BracketExp *exp=Parse(tokens);
	                    exp->retain();
	                    UserFunction *f=new UserFunction(exp,scope);
	                    f->retain();
	                    Base * base=NULL;
	                    try{
	                        base=f->exec(NULL);
	                    }catch(Exception* ex){
	                        ex->Msg(file_path+":"+ex->Msg());
	                        throw ex;
	                    }
	                    core=kvs::extend(new String(file_path),new Node(base,NULL),core);
	                    if(base!=NULL){
	                        base->release();
	                    }
	                    f->release();
	                    exp->release();
	                    tokens->release();
	                    onLoad=false;
	                    return base;
	                }
	            }
	        }
			static Node * StringSplit(string & in,char sp)
		    {
		        Node *r=NULL;
		        unsigned last=0,size_i=in.size();
		        for(unsigned i=0;i<size_i;i++)
		        {
		            if(in[i]==sp)
		            {
		                r=new Node(new String(in.substr(last,i-last)),r);
		                last=i+1;
		            }
		        }
		        return new Node(new String(in.substr(last,size_i-last)),r);
		    }
	        static string calAbsolutePath(string & base,string &relative)
	        {
	            Node *b=StringSplit(base,'/');
	            b=list::restAndDelete(b);
	            Node *r=list::reverseAndDelete(StringSplit(relative,'/'));
	            for(Node *t=r;t!=NULL;t=t->Rest()){
	                String *s=static_cast<String*>(t->First());
	                if(s->StdStr()==".")
	                {
	                    //忽略
	                }else
	                if(s->StdStr()=="")
	                {
	                    //忽略
	                }else
	                if(s->StdStr()=="..")
	                {
	                    b=list::restAndDelete(b);
	                }else
	                {
	                    b=new Node(s,b);
	                }
	            }
	            b=list::reverseAndDelete(b);
	            string s;
	            for(Node *t=b;t!=NULL;t=t->Rest())
	            {
	                s+=static_cast<String*>(t->First())->StdStr();
	                if(t->Rest()!=NULL)
	                {
	                    s+="/";
	                }
	            }
	            delete b;
	            delete r;
	            return s;
	        }
	    protected:
	        Base * run(Node * args){
	            String* r_path=static_cast<String*>(args->First());
	            string path=calAbsolutePath(base_path,r_path->StdStr());
	            return run_e(path,baseScope,line_split);
	        }
	    };
	    Node * LoadFunc::core=NULL;
	    bool LoadFunc::onLoad=false;
	}
}