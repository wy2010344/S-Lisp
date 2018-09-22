#pragma once
#include <iostream>
using namespace std;
namespace s{
    class LocationException{
    private:
        Location* loc;
        string msg;
        class Stack{
            Stack *next;
            string path;
            Location *loc;
            string exp;
        public:
            Stack(string path,Location * loc,string exp,Stack * before){
                this->next=before;
                this->path=path;
                loc->retain();
                this->loc=loc;
                this->exp=exp;
            }
            ~Stack(){
                loc->release();
                if(next!=NULL){
                    delete next;
                }
            }
            Stack * Next(){return next;}
            string & Path(){return path;}
            Location * Loc(){return loc;}
            string & Exp(){return exp;}
        };
        Stack * stacks;
    public:
        LocationException(string msg,Location* loc)
        {
            this->loc=loc;
            this->msg=msg;
            stacks=NULL;
            loc->retain();
        }
        string& Msg(){
            return msg;
        }
        void addStack(string path,Location * loc,string exp){
            stacks=new Stack(path,loc,exp,stacks);
        }
        Location* Loc(){
            return loc;
        }
        string toString(){
            string e="";
            for(Stack * tmp=stacks;tmp!=NULL;tmp=tmp->Next()){
                e=e+tmp->Path()+"\t"+tmp->Loc()->toString()+"\t"+tmp->Exp()+"\r\n";
            }
            e=e+loc->toString()+"\r\n";
            e=e+msg+"\r\n";
            return e;
        }
        ~LocationException(){
            loc->release();
            if(stacks!=NULL){
                delete stacks;
            }
        }
    };
};
