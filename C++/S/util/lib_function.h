#pragma once
namespace s{
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
}