#pragma once
#include "./tokenize_base.h"
namespace s{
    class Tokenize:public TokenizeBase{
    public:
        Node * run(const string & txt){
            return list::reverseAndDelete(tokenize(txt,txt.size(),0,NULL));
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
                        flag+1
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
                        flag+1
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
                        flag
                    );
                }
            }else{
                return rest;
            }
        }
        Node* tokenize_split(
            const string& txt,
            const int length,
            const int flag,
            const char split,
            const token::Types type,
            Node *rest,
            const int start)
        {
            if(flag<length)
            {
                char c=txt[flag];
                if(c==split)
                {
                    string stre=str::stringFromEscape(txt.substr(start,flag-start),split);
                    return tokenize(
                        txt,
                        length,
                        flag+1,
                        new Node(
                            new Token(stre,type,start-1),
                            rest
                        )
                    );
                }else
                if(c=='\\')
                {
                    return tokenize_split(
                        txt,
                        length,
                        flag+2,//因为跳过了转义
                        split,
                        type,
                        rest,
                        start
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
                        start
                     );
                }
            }else{
                throw DefinedException("超出范围");
            }
        }
        Node * tokenize_id(
            const string & txt,
            const int length,
            const int flag,
            Node * rest,
            const int start
            ){
                if(flag<length)
                {
                    char c=txt[flag];
                    if(!(token::isBlank(c)  ||token::isQuoteLeft(c) || token::isQuoteRight(c)))
                    {
                        return tokenize_id(
                            txt,
                            length,
                            flag+1,
                            rest,
                            start
                         );
                    }else
                    {
                        Token *token=deal_id(txt,start,flag);
                        return tokenize(
                            txt,
                            length,
                            flag,
                            new Node(token,rest)
                         );
                    }
                }else{
                    Token *token=deal_id(txt,start,flag);
                    return new Node(token,rest);
                }
        }
       
    };
};
