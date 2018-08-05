#include <iostream>
#include "./run.h"
using namespace std;
int main(int argc,char* argv[])
{
	if(argc>1)
	{
		char* file=argv[1];
		cout<<file<<endl;
		s::run(file);
	}else
	{
		/**
		按理说应该做控制台，但cin效果似乎不好还是我测试问题？总之先做了文件的
		*/
		cout<<"需要一个文件参数"<<endl;
	}
	return 0;
}