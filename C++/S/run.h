#pragma once
#include "./s.h"
#include"./tokenize/tokenize.h"
#include"./parse/parse.h"
#include "./interpret.h"
#include "./library/load.h"
namespace s{
    void run(const char * _file,Node* baseScope,char line_split){
        //set_terminate(terminal_exception);
        string file=_file;
#ifdef WIN32
        string pwd=_getcwd(NULL,0);
#else
        string pwd=getcwd(NULL,0);
#endif
        for(unsigned i=0;i<pwd.length();i++)
        {
            if(pwd[i]=='\\')
            {
                pwd[i]='/';
            }
        }
        pwd=pwd+"/";
        if(file[0]=='.')
        {
            file=library::PathOfFunc::calAbsolutePath(pwd,file);
            cout<<"绝对路径"<<file<<endl;
        }
        try{
            library::LoadFunc::run_e(file,baseScope,line_split);
        }catch(LocationException* e){
            cout<<"出现异常\r\n"<<e->toString();
            delete e;
        }catch(...){
            cout<<"出现未能捕获异常"<<endl;
        }
        Node *t=library::LoadFunc::core;

        if(t==NULL)
        {
            baseScope->retain();
            baseScope->release();
        }else{
            t->retain();
            t->release();
        }
        Base::print_node();
        cout<<"开始回收"<<endl;
        Base::clear();
        Base::print_node();
    }
};
