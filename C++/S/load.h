#pragma once
#include <iostream>

#ifdef WIN32
#include <direct.h>
#else
#include <unistd.h>
#endif
#include<string>
#include"./tokenize/tokenize.h"
#include"./parse/parse.h"
#include "./interpret.h"
using namespace std;
namespace s{
	Node * StringSplit(string & in,char sp)
    {
        unsigned count_i=0;
        for(unsigned i=0;i<in.size();i++)
        {
            if(in[i]==sp)
            {
                count_i++;
            }
        }
        count_i++;

        Node *r=NULL;
        unsigned lastIndex=0,last=0,size_i=in.size();
        for(unsigned i=0;i<size_i;i++)
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
        static Base *run_e(string file_path,Node *baseScope)
        {
            Node * x=static_cast<Node*>(kvs::find1st(core,file_path));
            if(x!=NULL)
            {
                return x->First();
            }else{
                string sb=file::read(file_path);
                Node* scope=kvs::extend(new String("load"),new LoadFunc(file_path,baseScope),baseScope);
                Node *tokens=Tokenize().run(sb);
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
    Node * LoadFunc::core=NULL;
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
            Node * tokens=Tokenize().run(str->StdStr());
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
    Node* load(){
        Node *baseScope=library::better();
        baseScope=kvs::extend("core-library",baseScope,baseScope);//parse默认的作用域，可手动扩展
        baseScope=kvs::extend("parse",new ParseFunc(baseScope),baseScope);
        Node *cscope=baseScope;
        cscope->retain();
        Node *ext=static_cast<Node*>(LoadFunc::run_e("./library.lisp",cscope));
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
    void logException(Exception* e){
        cout<<"出现异常："<<e->Msg();
        if(e->type()==E_Type::Location){
            LocationException* ex=static_cast<LocationException*>(e);
            cout<<"在位置"<<ex->Index()<<endl;
        }else{
            cout<<endl;
        }
        delete e;
    }
}
