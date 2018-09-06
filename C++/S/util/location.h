#pragma once
#include <sstream>
namespace s{
    class Location:public Base
    {
        unsigned r,c,i;
    public:
        Location(unsigned _r,unsigned _c,unsigned _i):Base(){
            this->r=_r;
            this->c=_c;
            this->i=_i;
        }
        unsigned row(){
            return r;
        }
        unsigned col(){
            return c;
        }
        unsigned index(){
            return i;
        }
        S_Type stype(){
            return Base::sLoaction;
        }
        string toString(){
            ostringstream oss;  //创建一个格式化输出流
            oss<<"{第"<<(r+1)<<"行,"<<(c+1)<<"列}";             //把值传入流中
            return oss.str();
        }
    };
};