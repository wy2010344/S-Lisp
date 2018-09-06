#pragma once
namespace s{
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
        S_Type stype(){
            return Base::sBool;
        }
        static Bool * True;
        static Bool * False;
        static Bool * trans(bool b){
            if(b){
                return Bool::True;
            }else{
                return Bool::False;
            }
        }
    };
    Bool * Bool::True=new Bool(true);
    Bool * Bool::False=new Bool(false);
};