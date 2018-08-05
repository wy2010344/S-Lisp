#pragma once
#include <iostream>

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
        string& Msg(){
            return msg;
        }
        int Index(){
            return index;
        }
    };
    string stringToEscape(string& v)
    {
        string r="\"";
        for(int i=0;i<v.size();i++)
        {
            char c=v[i];
            if(c=='\\')
            {
                r=r+"\\\\";
            }else
            if(c=='"')
            {
                r=r+"\\\"";
            }else
            {
                r=r+c;
            }
        }
        r=r+"\"";
        return r;
    }
    class Base{
    public:
        static int addsize;//增加的
        static int subsize;//减少的
        int id;
        int count;
        virtual string toString(){
            return "$Base";
        }
    public:
        Base(){
            count=0;
            id=addsize;
            //cout<<"正在生成:"<<id<<endl;
            addsize++;
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
            subsize++;
            //cout<<"销毁:"<<id<<endl;
        }
    };
    int Base::addsize=0;
    int Base::subsize=0;
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
        string toString(){
            string r="[";
            if(first!=NULL)
            {
                r=r+first->toString();
            }else
            {
                r=r+"null";
            }
            r=r+" ";
            for(Node *t=rest;t!=NULL;t=t->Rest())
            {
                Base * k=t->First();
                if(k!=NULL)
                {
                    r=r+k->toString();
                }else
                {
                    r=r+"null";
                }
                r=r+" ";
            }
            r=r+"]";
            return r;//"$Node";//"$Node:"+First()->toString()+","+Rest()->toString();
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
            char cs[2]={c,'\0'};
            return string(cs);
        }
    private:
        char c;
    };
    class String:public Base{
    public:
        String(string str):Base()
        {
            this->str=str;
        }
        string & StdStr(){
            return str;
        }
        string toString(){
            return stringToEscape(str);
        }
    private:
        string str;
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
    /*
     2166
    namespace map{
        Node * extend(Node * map,string key,Base * value)
        {
            Node * kv=new Node(new String(key),new Node(value,NULL));
            return new Node(kv,map);
        }
        Base * find1st(Node * map,string & key)
        {
            if(map==NULL){
                return NULL;
            }else{
                Node * kv=(Node*)map->First();
                String * k=static_cast<String *>(kv->First());
                if(k->StdStr()==key){
                    return kv->Rest()->First();
                }else{
                    return find1st(map->Rest(),key);
                }
            }
        }
    };
     */
};