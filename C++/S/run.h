#pragma once
#include "./load.h"
namespace s{
    void print(BracketExp *x,int indent)
    {
        for(Node *tmp=x->Cache();tmp!=NULL;tmp=tmp->Rest()){
            Exp * e=static_cast<Exp*>(tmp->First());
            for(int i=0;i<indent;i++)
            {
                cout<<"    ";
            }
            cout<<e->Value()<<endl;
            if(e->isBracket())
            {

                BracketExp *exp=static_cast<BracketExp *>(e);
                print(exp,indent+1);
            }
        }
    }
    void run(const char * _file){
        //set_terminate(terminal_exception);
        string file=_file;
#ifdef WIN32
        string pwd=_getcwd(NULL,0);
#else
        string pwd=getcwd(NULL,0);
#endif
        for(int i=0;i<pwd.length();i++)
        {
            if(pwd[i]=='\\')
            {
                pwd[i]='/';
            }
        }
        pwd=pwd+"/";
        if(file[0]=='.')
        {
            file=LoadFunc::calAbsolutePath(pwd,file);
            cout<<"绝对路径"<<file<<endl;
        }

        Node *baseScope=load();
        try{        
            LoadFunc::run_e(file,baseScope);
        }catch(Exception* e){
            logException(e);
        }catch(...){
            cout<<"出现未能捕获异常"<<endl;
        }
        Node *t=LoadFunc::core;
        
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