#pragma once
namespace s{
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
        S_Type stype(){
            return Base::sList;
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
                if(b->stype()==Base::sFunction)
                {
                    Function *f=static_cast<Function*>(b);
                    if(f->ftype()==Function::fBuildIn)
                    {
                        return "'"+f->toString();
                    }else{
                        return f->toString();
                    }
                }else
                if(b->stype()==Base::sBool){
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
};