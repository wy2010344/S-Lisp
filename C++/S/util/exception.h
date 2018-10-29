#pragma once
#include <iostream>
using namespace std;
namespace s{
    class LocationException{
    private:
        Location* loc;
        Location* right;
        string msg;
        class Stack{
            Stack *next;
            string path;
            Location *left;
            Location *right;
            string exp;
        public:
            Stack(string path,Location * left,Location* right,string exp,Stack * before){
                this->next=before;
                this->path=path;
                left->retain();
                this->left=left;
                right->retain();
                this->right=right;
                this->exp=exp;
            }
            ~Stack(){
                left->release();
                right->release();
                if(next!=NULL){
                    delete next;
                }
            }
            Stack * Next(){return next;}
            string & Path(){return path;}
            Location * Left(){return left;}
            Location * Right(){return right;}
            string & Exp(){return exp;}
        };
        Stack * stacks;
    public:
        LocationException(string msg,Location* left,Location* right=NULL){
            this->msg=msg;
            this->loc=left;
            stacks=NULL;
            loc->retain();
            this->right=right;
            if(right!=NULL){
                right->retain();
            }
        }
        string& Msg(){
            return msg;
        }
        void addStack(string path,Location * left,Location* right,string exp){
            stacks=new Stack(path,left,right,exp,stacks);
        }
        Location* Loc(){
            return loc;
        }
        string toString(){
            string e="";
            for(Stack * tmp=stacks;tmp!=NULL;tmp=tmp->Next()){
                e=e+tmp->Path()+"\t"+tmp->Left()->toString()+tmp->Right()->toString()+"\t"+tmp->Exp()+"\r\n";
            }
            e=e+loc->toString()+"\r\n";
            e=e+msg+"\r\n";
            return e;
        }
        ~LocationException(){
            loc->release();
            if(right!=NULL){
                right->release();
            }
            if(stacks!=NULL){
                delete stacks;
            }
        }
    };
};
