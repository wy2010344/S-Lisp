#include <iostream>
using namespace std;
#include "./run.h"
#include "./shell.h"
#include "./library/system.h"
#include "./library/parse.h"
#include "./library/read.h"
#include "./library/write.h"
namespace s{
	Node* load(char line_split){
	    Node *baseScope=system::library();
        baseScope=kvs::extend("read",new library::ReadFunc(line_split),baseScope);
        baseScope=kvs::extend("write",new library::WriteFunc(),baseScope);
	    baseScope=kvs::extend("core-library",baseScope,baseScope);//parse默认的作用域，可手动扩展
        baseScope=kvs::extend("parse",new library::ParseFunc(baseScope,line_split),baseScope);
	    Node *cscope=baseScope;
	    cscope->retain();
        Node *ext=static_cast<Node*>(library::LoadFunc::run_e("./library.lisp",cscope,line_split));
	    Node *t=ext;
	    while(t!=NULL){
	        String* key=static_cast<String*>(t->First());
	        t=t->Rest();
	        Base * value=t->First();
	        t=t->Rest();
	        baseScope=kvs::extend(key,value,baseScope);
	    }
	    cscope->eval_release();
	    Node::print_node();
	    return baseScope;
	}
}
int main(int argc,char* argv[])
{
    char lineSplit='\n';
	if(argc>1)
	{
		char* file=argv[1];
		cout<<file<<endl;
		s::run(file,s::load(lineSplit),lineSplit);
	}else
	{
		/**
		按理说应该做控制台，但cin效果似乎不好还是我测试问题？总之先做了文件的
		*/
        s::shell::run(s::load(lineSplit),lineSplit);
	}
	return 0;
}
