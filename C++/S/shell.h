#pragma once

#ifdef WIN32
#include <windows.h>
#else
#include <csignal>
#include <signal.h>
#endif
#include "./s.h"
#include"./tokenize/tokenize.h"
#include"./parse/parse.h"
#include "./interpret.h"
namespace s{
    namespace shell{
        bool come;
        Node *baseScope;
        QueueRun *qr;
        char buff[500];
        void init(Node *bScope){
            baseScope=bScope;
            qr=new QueueRun(baseScope);
            come=true;
        }
        void destroy(){
            baseScope->retain();
            baseScope->release();
            Node *t=library::LoadFunc::core;
            if(t!=NULL){
                t->retain();
                t->release();
            }
            Base::print_node();
            cout<<"开始回收"<<endl;
            Base::clear();
            delete qr;
            Base::print_node();
            cout<<"输入任意字符，按回车退出"<<endl;
            cin>>buff;
        }
        void _run(const char lineSplit){
            string cache="";
            while(come){
                string tmp="";
                cout<<"<=";
                cin.getline(buff,500);
                tmp=string(buff);
                if(tmp=="``"){
                    bool will=true;
                    while(will){
                        cin.getline(buff,500);
                        tmp=string(buff);
                        if(tmp=="``"){
                            will=false;
                        }else{
                            cache=cache+tmp+lineSplit;
                        }
                    }
                    //多行
                }else{
                    cache=tmp;
                }
                if(cache=="exit"){
                    come=false;
                }else{
                    Node * tokens=Tokenize().run(cache,lineSplit);
                    if(tokens!=NULL){
                        tokens->retain();
                        BracketExp * exp=Parse(tokens);
                        exp->retain();

                        Base* r=NULL;
                        /*想要每次回收，似乎并不容易*/
                        try{
                            r=qr->exec(exp);
                        }catch(LocationException* e){
                            cout<<"出现异常\r\n"<<e->toString();
                            delete e;
                        }catch(...){
                            cout<<"出现异常"<<endl;
                        }

                        cout<<"=>";
                        if(r==NULL)
                        {
                            cout<<"[]"<<endl;
                        }else
                        {
                            cout<<r->toString()<<endl;
                            r->retain();
                            r->release();
                        }
                        exp->release();
                        tokens->release();
                        Base::print_node();

                        cache="";
                        cout<<endl;
                    }
                }
            }
        }
#ifdef WIN32
        bool ctrlhandler( DWORD fdwctrltype )
        {
            /*
CTRL_C_EVENT - 当用户按下了CTRL+C,或者由GenerateConsoleCtrlEvent API发出.
CTRL_BREAK_EVENT - 用户按下CTRL+BREAK, 或者由GenerateConsoleCtrlEvent API发出.
CTRL_CLOSE_EVENT - 当试图关闭控制台程序，系统发送关闭消息。
CTRL_LOGOFF_EVENT - 用户退出时，但是不能决定是哪个用户.
CTRL_SHUTDOWN_EVENT - 当系统被关闭时.
            */
            switch( fdwctrltype )
            {
            case CTRL_C_EVENT:
            case CTRL_CLOSE_EVENT:
            case CTRL_BREAK_EVENT:
            case CTRL_LOGOFF_EVENT:
            case CTRL_SHUTDOWN_EVENT:
                come=false;
                destroy();
                exit(0);
                return true;
            default:
                return false;
            }
        }
        void run(Node* bScope,char line_split){
            init(bScope);
            if(SetConsoleCtrlHandler((PHANDLER_ROUTINE)ctrlhandler,true))
            {
                _run(line_split);
            }
            destroy();
        }
#else
        void sig_handler(int sig)
        {
            //退出循环，向后执行
            come=false;
            destroy();
            exit(0);
        }
        void run(Node* bScope){
            /**
            https://zhidao.baidu.com/question/1766690354480323100.html
主要信号及说明：
SIGHUP 挂起信号
SIGINT 中断信号
SIGQUIT 退出信号
SIGILL 非法指令
SIGTRAP 跟踪/断点中断
SIGABRT 放弃
SIGFPE 浮点异常
SIGKILL 删除（不能捕获或者忽略）
SIGBUS 总线错误
SIGEGV分段错误
SIGSYS 系统调用错误参数
SIGPIPE 管道错误
SIGALRM 闹钟
SIGTERM 软件终止
SIGUSR1 用户信号1
            */

            /*
            signal(1,sig_handler);//SIGINT
            signal(2,sig_handler);//SIGHUP
            signal(3,sig_handler);//SIGQUIT
            signal(15,sig_handler);//SIGTERM
            */

            signal(SIGINT,sig_handler);//1
            signal(SIGTERM,sig_handler);//15
#ifdef SIGHUP
            signal(SIGHUP,sig_handler);//2
#endif
#ifdef SIGQUIT
            signal(SIGQUIT,sig_handler);//3
#endif
            /*
            signal(SIGINT,sig_handler);//1
            signal(SIGHUP,sig_handler);//2
            signal(SIGQUIT,sig_handler);//3
            signal(SIGTERM,sig_handler);//15
            */
            init(bScope);
            _run(line_split);
            destroy();
        }
#endif
    };
}
