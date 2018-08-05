#pragma once
#include <iostream>
#include "./node.h"
using namespace std;
namespace s{
    namespace token{
        bool isBlank(char c)
        {
            return (c==' ' || c=='\t' || c=='\r' || c=='\n');
        }
        bool notNumber(char c)
        {
            return ('0'>c || c>'9');
        }
        bool isQuoteLeft(char c){
            return (c=='(' || c=='[' || c=='{');
        }
        bool isQuoteRight(char c){
            return (c=='}' || c==']' || c==')');
        }
        enum Types{
            BracketLeft,
            BracketRight,
            Comment,
            Prevent,
            Str,
            Id,
            Num
        };
    };
    class Token:public Base{
    public:
        Token(string value,token::Types type,int index):Base(){
            //cout<<"TOKEN:"<<value<<"  "<<type<<endl;
            this->value=value;
            this->type=type;
            this->index=index;
        }
        string & Value(){
            return this->value;
        }
        token::Types Type(){
            return this->type;
        }
        int Index(){
            return this->index;
        }
    private:
        int index;
        string value;
        token::Types type;
    };
    Node* tokenize(
        const string & txt,
        const int length,
        const int flag,
        Node * rest//整体链表
        );//值链表
    Node* tokenize_split(
        const string& txt,
        const int length,
        const int flag,
        char split,
        token::Types type,
        Node *rest,
        Node * cache,
        const bool trans)
    {
        if(flag<length)
        {
            char c=txt[flag];
            if(c==split)
            {
                if(!trans)
                {
                    if(cache!=NULL)
                    {
                        string str=list::toStringAndDelete(cache);
                        return tokenize(
                            txt,
                            length,
                            flag+1,
                            new Node(
                            new Token(str,type,flag-str.size()-2),
                                rest
                            )
                        );
                    }else{
                        throw "提前结束"+flag;
                    }
                }else{
                    return tokenize_split(
                        txt,
                        length,
                        flag+1,
                        split,
                        type,
                        rest,
                        new Node(new Char(c),cache),
                        false
                     );
                }
            }
            //
            if(c=='\\')
            {
                Node *ck=NULL;
                if(trans)
                {
                    //上次转义，本次不转义
                    ck=new Node(new Char(c),cache);
                }else
                {
                    //本次转义
                    ck=cache;
                }
                return tokenize_split(
                    txt,
                    length,
                    flag+1,
                    split,
                    type,
                    rest,
                    ck,//是否应该记录其中？
                    !trans
                 );
            }else
            {
                return tokenize_split(
                    txt,
                    length,
                    flag+1,
                    split,
                    type,
                    rest,
                    new Node(new Char(c),cache),
                    false
                 );
            }
        }else{
            throw "超出范围";
        }
    }
    bool isInt(string & id)
    {
        bool ret=true;
        for(int i=0;i<id.size();i++)
        {
            char c=id[0];
            if(token::notNumber(c)){
                ret=false;
            }
        }
        return ret;
    }
    Node * tokenize_id(
        const string & txt,
        const int length,
        const int flag,
        Node * rest,
        Node * cache
        ){
            if(flag<length)
            {
                char c=txt[flag];
                if(!token::isBlank(c) && !token::isQuoteLeft(c) && !token::isQuoteRight(c))
                {
                    return tokenize_id(
                        txt,
                        length,
                        flag+1,
                        rest,
                        new Node(new Char(c),cache)
                     );
                }else
                {
                    string Id=list::toStringAndDelete(cache);
                    int index=flag-Id.size()-1;//减去当前不属于
                    Token *token;
                    /*
                      各种自定义类型，未来如果支持负数、小数，从这里扩展，乃至如red语言中支持邮箱、路径等类型。
                      但数值计算始终是属于函数对字符串的处理。宿主语言库提供优化的数值计算函数。
                      因为s-lisp无强类型，只有运行时动态检查出类型，跟动态用字符串转化为特定类型一样的报错体验。
                    */
                    if (Id[0]=='\'') {
                        //阻止求值
                        token=new Token(Id.substr(1,Id.size()-1),token::Types::Prevent,index);
                    }else
                    if (isInt(Id)){
                        //转成Int，方便数值计算
                        token=new Token(Id,token::Types::Num,index);
                    }else
                    {
                        //ID类型
                        token=new Token(Id,token::Types::Id,index);
                    }
                    return tokenize(
                        txt,
                        length,
                        flag,
                        new Node(token,rest)
                     );
                }
            }else{
                throw "超出范围";
            }
    }
    /*
    txt
    length
    flag
    */
    Node* tokenize(
        const string & txt,
        const int length,
        const int flag,
        Node * rest//整体链表
        )//值链表
    {
        if(flag<length)
        {
            char c=txt[flag];
            //开始
            if(token::isBlank(c))
            {
                return tokenize(
                    txt,
                    length,
                    flag+1,
                    rest
                 );
            }else
            if(token::isQuoteLeft(c))
            {
                char cs[2]={c,'\0'};
                return tokenize(
                    txt,
                    length,
                    flag+1,
                    new Node(
                        new Token(string(cs),token::Types::BracketLeft,flag),
                        rest
                    )
                 );
            }else
            if(token::isQuoteRight(c))
            {
                char cs[2]={c,'\0'};
                return tokenize(
                    txt,
                    length,
                    flag+1,
                    new Node(
                        new Token(string(cs),token::Types::BracketRight,flag),
                        rest
                    )
                 );
            }
            if(c=='"'){
                return tokenize_split(
                    txt,
                    length,
                    flag+1,
                    '"',
                    token::Types::Str,
                    rest,
                    NULL,
                    false
                );
            }
            else
            if(c=='`')
            {
                //注释
                return tokenize_split(
                    txt,
                    length,
                    flag+1,
                    '`',
                    token::Types::Comment,
                    rest,
                    NULL,
                    false
                );
            }else
            {
                //默认都识别为ID/字符串。
                //id类型
                return tokenize_id(
                    txt,
                    length,
                    flag+1,
                    rest,
                    new Node(new Char(c),NULL)
                );
            }
        }else{
            return rest;
        }
    }

    Node* tokenize(const string & txt)
    {
        Node * token=tokenize(txt,txt.size(),0,NULL);
        token=list::reverseAndDelete(token);
        return token;
    }
};