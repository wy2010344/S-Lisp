#pragma once
#include <iostream>

#ifdef WIN32
#include <direct.h>
#else
#include <unistd.h>
#endif

#include <fstream> 
#include<string>
#include"./tokenize.h"
#include"./tree.h"
#include "./interpret.h"
using namespace std;
namespace s{
    Node * StringSplit(string & in,char sp)
    {
        int count_i=0;
        for(int i=0;i<in.size();i++)
        {
            if(in[i]==sp)
            {
                count_i++;
            }
        }
        count_i++;

        Node *r=NULL;
        int lastIndex=0,last=0,size_i=in.size();
        for(int i=0;i<size_i;i++)
        {
            if(in[i]==sp)
            {
                r=new Node(new String(in.substr(last,i-last)),r);
                last=i+1;
                lastIndex++;
            }
        }
        return new Node(new String(in.substr(last,size_i-last)),r);
    }
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
    class LoadFunc:public LibFunction{
        string base_path;
        Node *baseScope;
    public:
        string toString(){
            return "load";
        }
        Function_type ftype(){
            return Function_type::fBuildIn;
        }
        LoadFunc(string path,Node *scope){
            base_path=path;
            baseScope=scope;
        }
        static Node * core;
        static Base *run_e(string file,Node *baseScope)
        {
            Node * x=static_cast<Node*>(kvs::find1st(core,file));
            if(x!=NULL)
            {
                return x->First();
            }else{
                ifstream myfile(file.c_str());
                string tmp;
                if (!myfile.is_open())  
                {  
                    cout << "未成功打开文件:"<<file << endl;
                    return NULL;
                }  

                string sb;
                while(getline(myfile,tmp))
                {
                    sb+=tmp;
                    sb+="\n";
                }
                myfile.close();
                Node* scope=kvs::extend(new String("load"),new LoadFunc(file,baseScope),baseScope);
                //cout<<sb<<endl;
                //cout<<tokens->Length()<<endl;
                /*
                for(Node * tmp=tokens;tmp!=NULL;tmp=tmp->Rest())
                {
                    Token *t=static_cast<Token*>(tmp->First());
                    cout<<t->Value()<<"  "<<t->Type()<<endl;
                }
                */           
                Node *tokens=tokenize(sb);
                tokens->retain();
                BracketExp *exp=Parse(tokens);
                exp->retain();
                UserFunction *f=new UserFunction(exp,scope);
                f->retain();
                Base * base=NULL;
                try{
                    base=f->exec(NULL);
                }catch(LocationException & ex)
                {
                    ex.Msg(file+":"+ex.Msg());
                    throw ex;
                }
                core=kvs::extend(new String(file),new Node(base,NULL),core);
                if(base!=NULL){
                    base->release();
                }
                f->release();
                exp->release();
                tokens->release();
                return base;
            }
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
            return run_e(path,baseScope);
        }
    };
    class ParseFunc:public Function{
    private:
        Node *defaultScope;
    public:
        string toString(){
            return "parse";
        }
        Function_type ftype(){
            return Function_type::fBuildIn;
        }
        ParseFunc(Node *defaultScope):Function(){
            this->defaultScope=defaultScope;
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
            Node * tokens=tokenize(str->StdStr());
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
    Node * LoadFunc::core=NULL;
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
        Node *baseScope=library::library();
        baseScope=kvs::extend("core-library",baseScope,baseScope);//parse默认的作用域，可手动扩展
        baseScope=kvs::extend("parse",new ParseFunc(baseScope),baseScope);
        try{
            LoadFunc::run_e(file,baseScope);
        }catch(LocationException& e){
            cout<<"出现异常"<<e.Msg()<<"在位置"<<e.Index()<<endl;
        }catch(...){
            cout<<"出现未能捕获异常"<<endl;
        }
        //delete r;
        /*
        cout<<"请等待"<<endl;
        print(x,0);
        cout<<"已经结束"<<endl;
        delete tokens;
        delete x;
         */
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