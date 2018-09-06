#pragma once
#include <iostream>
using namespace std;
namespace s{
    class Exception{
    private:
        string msg;
    public:
        Exception(string msg){
            this->msg=msg;
        }
        string& Msg(){
            return msg;
        }
        enum Exception_Type{
            Exception_Location,
            Exception_Defined
        };
        virtual Exception_Type type()=0;
        void Msg(string _msg)
        {
            msg=_msg;
        }
        virtual ~Exception(){}
    };
    class LocationException:public Exception{
    private:
        Location* loc;
    public:
        LocationException(string msg,Location* loc):Exception(msg)
        {
            this->loc=loc;
            loc->retain();
        }
        Exception_Type type(){
            return Exception::Exception_Location;
        }
        Location* Loc(){
            return loc;
        }
        ~LocationException(){
            loc->release();
        }
    };
    class DefinedException:public Exception{
    public:
        DefinedException(string msg):Exception(msg){}
        Exception_Type type(){
            return Exception::Exception_Defined;
        }
    };
};
