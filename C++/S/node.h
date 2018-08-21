#pragma once
#include <iostream>
#include <sstream>
#include <fstream>/*file*/
#include "gc.h"
using namespace std;

namespace s{
    class LocationException{
    private:
        string msg;
        int index;
    public:
        LocationException(string msg,int index)
        {
            this->msg=msg;
            this->index=index;
        }
        void Msg(string _msg)
        {
            msg=_msg;
        }
        string& Msg(){
            return msg;
        }
        int Index(){
            return index;
        }
    };
    class DefinedException{
    private:
        string msg;
    public:
        DefinedException(string msg){
            this->msg=msg;
        }
        string& Msg(){
            return msg;
        }
    };
    namespace str{
        char trans_map[]={'n','\n','r','\r','t','\t'};
        char trans_from_char(char c,bool & unfind){
            char x=' ';
            for(unsigned i=0;i<sizeof(trans_map);i++){
                char key=trans_map[i];
                i++;
                char value=trans_map[i];
                if(key==c){
                    unfind=false;
                    x=value;
                }
            }
            return x;
        }
        char trans_to_char(char c,bool & unfind){
            char x=' ';
            for(unsigned i=0;i<sizeof(trans_map);i++){
                char value=trans_map[i];
                i++;
                char key=trans_map[i];
                if(key==c){
                    unfind=false;
                    x=value;
                }
            }
            return x;
        }
        string stringToEscape(string& v)
        {
            unsigned size=v.size();
            for(unsigned i=0;i<v.size();i++){
                char c=v[i];
                if(c=='\\'){
                    size+=1;
                }else
                if(c=='"'){
                    size+=1;
                }else
                {
                    bool unfind=true;
                    trans_to_char(c,unfind);
                    if(!unfind){
                        size+=1;
                    }
                }
            }
            int ref=1;
            unsigned i=0;
            char* buff=new char[size+3];
            buff[0]='"';
            while(i<v.size())
            {
                char c=v[i];
                if(c=='\\'){
                    buff[ref]='\\';
                    ref++;
                    buff[ref]='\\';
                }else
                if(c=='"'){
                    buff[ref]='\\';
                    ref++;
                    buff[ref]='"';
                }else
                {
                    bool unfind=true;
                    char x=trans_to_char(c,unfind);
                    if(unfind){
                        buff[ref]=c;
                    }else{
                        buff[ref]='\\';
                        ref++;
                        buff[ref]=x;
                    }
                }
                i++;
                ref++;
            }
            buff[size+1]='"';
            buff[size+2]='\0';
            string r(buff);
            delete[] buff;
            return r;
        }
    };
    /*
    对base进行区分
    */
    enum Base_type{
        xList,
        xInt,
        xChar,
        xString,
        xBool,
        xToken,
        xExp,
        xFunction
    };
    class Base{
#ifdef DEBUG
    private:
        static int addsize;//增加的
        static int subsize;//减少的
    public:
        static gc::LNode* lnode;
        /*回收*/
        static void clear(){
            while(lnode!=NULL)
            {
                Base * b=lnode->value;
                b->retain();
                while(b->_ref_()!=1){
                    b->release();
                }
                b->release();
            }
        }
        static void print_node(){
            cout<<"合计新增:"<<Base::addsize<<"  合计减少:"<<Base::subsize<<"  生存数量:"<<(Base::addsize-Base::subsize)<<endl;
        }
        int id;
        static void add_to_link(Base * b){
            addsize++;
            b->id=addsize;
            gc::LNode *n=new gc::LNode();
            n->value=b;
            n->next=lnode;
            lnode=n;
        }
        static void remove_on_link(Base *b){
            subsize++;

            if(lnode!=NULL){
                //有可能在链上找不到
                gc::LNode *t=lnode;
                if(t->value==b)
                {
                    lnode=t->next;
                    delete t;
                }else{
                    gc::LNode *pre=t;
                    t=t->next;
                    bool will=true;
                    while(t!=NULL && will)
                    {
                        if(t->value==b)
                        {
                            pre->next=t->next;
                            delete t;
                            will=false;
                        }else{
                            pre=t;
                            t=t->next;
                        }
                    }
                }
            }
        }
#else
    public:
        static void clear(){
        }
        static void eval_clear(){
        }
        static void print_node(){
        }
        static void add_to_link(Base * b){
        }
        static void remove_on_link(Base *b){
        }
#endif
        int count;
        virtual string toString()=0;
    public:
        Base(){
            count=0;
            //cout<<"正在生成:"<<id<<endl;
            add_to_link(this);
        }
        /**
        retain与release是替代new与delete
        构造时默认retain，就像JAVA中Object默认占用内存，必须手动release一次
        在哪儿构造的在哪去销毁。

        但是Node等，每次添加时，增加了一次引用，则原New的地方没有销毁到
        New与Release必须成对存在。
        它的作用域并不是自动retain和release的，尤其是传统的语句占用原变量名。
        所以应对的时候还是C++的手动编程。
        */
        void retain(){
            count++;
        }
        //在函数中特殊的存在，恢复成无计数状态。
        void eval_release(){
            count--;
        }
        void release()
        {
            count--;
            if(count==0)
            {
                delete this;//删除自身
                //this->~Base();
            }
            /*
            else
            if(count<0){
                cout<<id<<"小于0:"<<count<<endl;
            }
            */
        }
        int _ref_(){
            return count;
        }
        virtual ~Base(){
            remove_on_link(this);
            //cout<<"销毁:"<<id<<endl;
        }
        virtual Base_type xtype()=0;
    };
#ifdef DEBUG
    int Base::addsize=0;
    int Base::subsize=0;
    gc::LNode* Base::lnode=NULL;
#endif
    enum Function_type{
        fBuildIn,//内置函数
        fBetter,//迁移到C++优化函数
        fUser,//用户函数，只有一个
        fCache//cache函数，只有一个
    };
    class Node;
    class Function:public Base{
    public:
        Function():Base(){}
        virtual Base * exec(Node * args)=0;
        Base_type xtype(){
            return Base_type::xFunction;
        }
        virtual Function_type ftype()=0;
    };
    class String:public Base{
    public:
        String(string std_str):Base()
        {
            this->std_str=std_str;
        }
        string & StdStr(){
            return std_str;
        }
        string toString(){
            return str::stringToEscape(std_str);
        }
        Base_type xtype(){
            return Base_type::xString;
        }
    private:
        string std_str;
    };
    class Node:public Base{
    public:
        Node(Base * first,Node *rest):Base()
        {
            this->first=first;
            this->rest=rest;

            if(first!=NULL){
                first->retain();
            }
            if(rest!=NULL)
            {
                rest->retain();
                length=rest->Length()+1;
            }else{
                length=1;
            }
        }
        int Length(){
            return length;
        }
        Base* First(){
            return first;
        }
        Node* Rest(){
            return rest;
        }
        virtual ~Node(){
            if(first!=NULL){
                first->release();
            }
            if(rest!=NULL){
                rest->release();
            }
        }
        Base_type xtype(){
            return Base_type::xList;
        }
        string toString(){
            string r="["+toString(first)+" ";
            for(Node *t=rest;t!=NULL;t=t->Rest())
            {
                Base * k=t->First();
                r=r+toString(k)+" ";
            }
            r=r+"]";
            return r;//"$Node";//"$Node:"+First()->toString()+","+Rest()->toString();
        }
        static string toString(Base * b)
        {
            if(b==NULL){
                return "[]";
            }else{
                if(b->xtype()==Base_type::xFunction)
                {
                    Function *f=static_cast<Function*>(b);
                    if(f->ftype()==Function_type::fBuildIn)
                    {
                        return "'"+f->toString();
                    }else{
                        return f->toString();
                    }
                }else
                if(b->xtype()==Base_type::xBool){
                    return "'"+b->toString();
                }else
                {
                    return b->toString();
                }
            }
        }
    private:
        int length;
        Base* first;
        Node *rest;
    };
    class Char:public Base{
    public:
        Char(char c):Base(){
            this->c=c;
        }
        char Value(){
            return c;
        }
        string toString(){
            return toString(c);
        }
        Base_type xtype(){
            return Base_type::xChar;
        }
        static string toString(char c){
            char cs[2]={c,'\0'};
            return string(cs);
        }
    private:
        char c;
    };
    class Int:public Base{
    private:
        int value;
        string cache;
    public:
        Int(string & k):Base(){
            this->value=atoi(k.c_str());
            this->cache=k;
        }
        Int(int k):Base(){
            this->value=k;
            stringstream stream;
            stream<<k;
            this->cache=stream.str();
        }
        virtual string toString(){
            return cache;
        }
        int Value(){
            return value;
        }
        Base_type xtype(){
            return Base_type::xInt;
        }
    };
    class Bool:public Base{
    private:
        bool value;
        Bool(bool v):Base(){
            this->value=v;
        }
    public:
        bool Value(){
            return value;
        }
        string toString(){
            if(value){
                return "true";
            }else{
                return "false";
            }
        }
        Base_type xtype(){
            return Base_type::xBool;
        }
        static Bool * True;
        static Bool * False;
    };
    Bool * Bool::True=new Bool(true);
    Bool * Bool::False=new Bool(false);
    class LibFunction:public Function{
    public:
        Base * exec(Node* args)
        {
            Base* ret=run(args);
            if(ret!=NULL)
            {
                ret->retain();
            }
            return ret;
        }
    protected:
        virtual Base * run(Node * args)=0;
    };
    namespace list{
        string toStringAndDelete(Node * node)
        {
            char* cs=new char[node->Length()+1];
            Node *tmp=node;
            for(int i=node->Length();i!=0;i--)
            {
                char c=static_cast<Char *>(tmp->First())->Value();
                cs[i-1]=c;
                tmp=tmp->Rest();
            }
            cs[node->Length()]='\0';
            string str=string(cs);
            delete [] cs;
            delete node;
            //cout<<"N"<<node->id<<"v"<<node->_ref_()<<endl;
            return str;
        }
        Node * reverse(Node * node)
        {
            Node* r=NULL;
            for(Node *tmp=node;tmp!=NULL;tmp=tmp->Rest())
            {
                r=new Node(tmp->First(),r);
            }
            return r;
        }
        Node* reverseAndDelete(Node* node){
            Node* r=reverse(node);
            delete node;
            return r;
        }
        Node * restAndDelete(Node * b)
        {
            Node *old=b;
            b=b->Rest();
            old->retain();
            b->retain();
            old->release();
            b->eval_release();
            return b;
        }
    };
    //1997个，对比使用map2166
    namespace kvs{
        /*与list添加头保持一致*/
        Node * extend(String * key,Base *value,Node * kvs){
            return new Node(key,new Node(value,kvs));
        }
        Node * extend(string key,Base * value,Node * kvs){
            return extend(new String(key), value,kvs);
        }
        Base * find1st(Node * kvs,string & key){
            if (kvs==NULL) {
                return NULL;
            }else{
                String * k=static_cast<String*>(kvs->First());
                kvs=kvs->Rest();
                if (k->StdStr()==key) {
                    return kvs->First();
                }else{
                    return find1st(kvs->Rest(), key);
                }
            }
        }
    };
    namespace file{
        string read(string & path){
            ifstream myfile(path.c_str());
            string tmp;
            if (!myfile.is_open())  
            {  
                cout << "未成功打开文件:"<<path << endl;
                return NULL;
            }  
            string sb;
            while(getline(myfile,tmp))
            {
                sb+=tmp;
                sb+="\n";
            }
            myfile.close();
            return sb;
        }
        void write(string & path,string & content){
            ofstream f1(path.c_str());
            f1<<content;
            f1.close();
        }
    }
};
