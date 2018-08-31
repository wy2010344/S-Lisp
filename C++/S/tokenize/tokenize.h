#pragma once
#include "./tokenize_base.h"
namespace s{
    class Tokenize:public TokenizeBase{
    public:
    	Node * run(const string & txt){
    		const unsigned length=txt.size();
    		unsigned flag=0;
    		Node* rest=NULL;
    		while(flag<length){
    			char c=txt[flag];
    			if(token::isBlank(c)){
                    flag++;
    			}else
    			if(token::isQuoteLeft(c)){
                    char cs[2]={c,'\0'};
                    rest=new Node(
                        new Token(string(cs),token::Types::BracketLeft,flag),
                        rest
                    );
                    flag++;
    			}else
    			if(token::isQuoteRight(c)){
                    char cs[2]={c,'\0'};
                    rest=new Node(
                        new Token(string(cs),token::Types::BracketRight,flag),
                        rest
                    );
                    flag++;
    			}else
    			if(c=='"'){
    				tokenize_split(txt,length,rest,flag,token::Types::Str,'"');
    			}else
    			if(c=='`'){
    				tokenize_split(txt,length,rest,flag,token::Types::Comment,'`');
    			}else
    			{
    				tokenize_ID(txt,length,rest,flag);
    			}
    		}
    		return rest;
    	}
    	void tokenize_split(
    		const string &txt,
    		const unsigned length,
    		Node*&rest,
    		unsigned & flag,
    		const token::Types type,
    		const char split
    	){
    		bool unbreak=true;
            flag++;
            unsigned start=flag;
            unsigned trans_time=0;
    		while((flag<length) && unbreak){
    			char c=txt[flag];
    			if(c==split){
                    string stre=txt.substr(start,flag-start);
                    if(trans_time!=0){
                        stre=str::stringFromEscape(stre,split,trans_time);
                    }
    				rest=new Node(
                        new Token(stre,type,start-1),
                        rest
                    );
                    flag++;
    				unbreak=false;
    			}else{
	    			if(c=='\\'){
                        trans_time++;
	    				flag++;
	    			}
	    			flag++;
    			}
    		}
    		if(unbreak){
    			throw new DefinedException("超出范围");
    		}
    	}

    	void tokenize_ID(
    		const string &txt,
    		const unsigned length,
    		Node*&rest,
    		unsigned & flag
    	){
    		bool unbreak=true;
    		unsigned start=flag;
    		while((flag<length) && unbreak){
    			char c=txt[flag];
                if(!(token::isBlank(c)  ||token::isQuoteLeft(c) || token::isQuoteRight(c)))
                {
                	flag++;
                }else{
                    Token *token=deal_id(txt,start,flag);
                    rest=new Node(token,rest);
                	unbreak=false;
                }
    		}
    		if(unbreak){
                Token *token=deal_id(txt,start,flag);
                rest=new Node(token,rest);
    		}
    	}
    };
}
