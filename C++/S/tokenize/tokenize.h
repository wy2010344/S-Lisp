#pragma once
#include "./tokenize_base.h"
namespace s{
    class Tokenize:public TokenizeBase{
    public:
    	Node * run(const string & txt,const char linesplit){
            Code* code=new Code(txt,linesplit);
    		Node* rest=NULL;
    		while(code->noEnd()){
    			char c=code->current();
    			if(isBlank(c)){
                    code->shift();
    			}else
    			if(isQuoteLeft(c)){
                    char cs[2]={c,'\0'};
                    rest=new Node(
                        new Token(new String(string(cs)),Token::Token_BracketLeft,code->currentLoc()),
                        rest
                    );
                    code->shift();
    			}else
    			if(isQuoteRight(c)){
                    char cs[2]={c,'\0'};
                    rest=new Node(
                        new Token(new String(string(cs)),Token::Token_BracketRight,code->currentLoc()),
                        rest
                    );
                    code->shift();
    			}else
    			if(c=='"'){
    				rest=tokenize_split(code,rest,Token::Token_String,code->currentLoc(),'"');
    			}else
    			if(c=='`'){
    				rest=tokenize_split(code,rest,Token::Token_Comment,code->currentLoc(),'`');
    			}else
    			{
    				rest=tokenize_ID(code,code->currentLoc(),rest);
    			}
    		}
    		delete code;
    		return rest;
    	}
    	Node* tokenize_split(
            Code *code,
    		Node*rest,
    		const Token::Token_Type type,
            Location* loc,
    		const char end
    	){
    		bool unbreak=true;
            code->shift();
            unsigned start=code->index();
            unsigned trans_time=0;
    		while(code->noEnd() && unbreak){
    			char c=code->current();
    			if(c==end){
                    string stre=code->substr(start,code->index());
                    if(trans_time!=0){
                        stre=str::stringFromEscape(stre,end,trans_time);
                    }
    				rest=new Node(
                        new Token(new String(stre),type,loc),
                        rest
                    );
                    code->shift();
    				unbreak=false;
    			}else{
	    			if(c=='\\'){
                        trans_time++;
                        code->shift();
	    			}
                    code->shift();
    			}
    		}
    		if(unbreak){
                throw new LocationException("超出范围",loc);
    		}
            return rest;
    	}

    	Node* tokenize_ID(
    		Code *code,
            Location* loc,
    		Node* rest
    	){
    		bool unbreak=true;
    		while(code->noEnd() && unbreak){
    			char c=code->current();
                if(!(isBlank(c)  || isQuoteLeft(c) || isQuoteRight(c) || c=='"' || c=='`'))
                {
                    code->shift();
                }else{
                    Token *token=deal_id(code,loc);
                    rest=new Node(token,rest);
                	unbreak=false;
                }
    		}
    		if(unbreak){
                Token *token=deal_id(code,loc);
                rest=new Node(token,rest);
    		}
            return rest;
    	}
    };
}
