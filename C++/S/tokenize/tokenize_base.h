#pragma once
#include <iostream>
#include "../node.h"
using namespace std;
namespace s{
    class Token:public Base{
    public:
        enum Token_Type{
            Token_BracketLeft,
            Token_BracketRight,
            Token_Comment,
            Token_Prevent,
            Token_Str,
            Token_Id,
            Token_Num
        };
        Token(string value,Token_Type type,int index):Base(){
            //cout<<"TOKEN:"<<value<<"  "<<type<<endl;
            this->value=value;
            this->type=type;
            this->index=index;
        }
        string & Value(){
            return this->value;
        }
        Token_Type token_type(){
            return this->type;
        }
        int Index(){
            return this->index;
        }
        S_Type stype(){
            return Base::sToken;
        }
        string toString(){
            return "$Token";
        }
    private:
        int index;
        string value;
        Token_Type type;
    };
    class TokenizeBase{
    public:
        static bool isBlank(char c)
        {
            return (c==' ' || c=='\t' || c=='\r' || c=='\n');
        }
        static bool notNumber(char c)
        {
            return ('0'>c || c>'9');
        }
        static bool isQuoteLeft(char c){
            return (c=='(' || c=='[' || c=='{');
        }
        static bool isQuoteRight(char c){
            return (c=='}' || c==']' || c==')');
        }

        static bool isInt(const string & id)
        {
            bool ret=true;
            for(unsigned i=0;i<id.size();i++)
            {
                char c=id[0];
                if(notNumber(c)){
                    ret=false;
                }
            }
            return ret;
        }
        /*
        各种自定义类型，未来如果支持负数、小数，从这里扩展，乃至如red语言中支持邮箱、路径等类型。
        但数值计算始终是属于函数对字符串的处理。宿主语言库提供优化的数值计算函数。
        因为s-lisp无强类型，只有运行时动态检查出类型，跟动态用字符串转化为特定类型一样的报错体验。
        */
        Token* deal_id(
            const string & txt,
            const int start,
            const int flag){
            string Id=txt.substr(start,flag-start);
            Token *token;
            if (Id[0]=='\'') {
                //阻止求值
                if(Id.size()==1){
                    throw new DefinedException("单个'不允许");
                }else{
                    token=new Token(Id.substr(1,Id.size()-1),Token::Token_Prevent,start);
                }
            }else
            if (isInt(Id)){
                //转成Int，方便数值计算
                token=new Token(Id,Token::Token_Num,start);
            }else
            {
                //ID类型
                token=new Token(Id,Token::Token_Id,start);
            }
            return token;
        }

        virtual Node* run(const string& txt)=0;
    };
}
