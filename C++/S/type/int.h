#pragma once
#include <stdlib.h>
namespace s{
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
        S_Type stype(){
            return Base::sInt;
        }
    };
};