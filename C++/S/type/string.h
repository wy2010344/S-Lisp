#pragma once
namespace s{
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
            return str::stringToEscape(std_str,'"','"');
        }
        S_Type stype(){
            return Base::sString;
        }
    private:
        string std_str;
    };
};