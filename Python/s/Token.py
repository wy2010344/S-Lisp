# coding=utf-8

from Code import Code
from Node import Node

class TokenType:
    Token_Prevent=0
    Token_Int=1
    Token_Bool=2
    Token_String=3
    Token_Id=4
    Token_BracketLeft=5
    Token_BracketRight=6
    Token_Comment=7
class Token:
    def __init__(self,value,old_value,token_type,loc):
        self.value=value
        self.old_value=old_value
        self.token_type=token_type
        self.loc=loc
        
    def __str__(self):
        return self.old_value
    
    @staticmethod
    def isBlank(c):
        return (c==' ' or c=='\t' or c=='\r' or c=='\n')
    
    @staticmethod
    def notNumber(c):
        return (c<'0' or '9'<c)
    
    @staticmethod
    def isQuoteLeft(c):
        return (c=='(' or c=='[' or c=='{')
    
    @staticmethod
    def isQuoteRight(c):
        return (c=='}' or c==']' or c==')')
    
    @staticmethod
    def isInt(id):
        ret=True
        i=0
        len_id=len(id)
        while i<len_id:
            c=id[i]
            if Token.notNumber(c):
                ret=False
            i=i+1
        return ret
    
    @staticmethod
    def deal_id(code,loc):
        start=loc.Index()
        id=code.substr(start,code.index())
        id_len=len(id)
        if id[0]=='\'':
            if id_len==1:
                raise LocationException(loc,"单个‘不允许")
            else:
                return Token(id[1:id_len],id,TokenType.Token_Prevent,loc)
        elif Token.isInt(id):
            return Token(id,id,TokenType.Token_Int,loc)
        elif id=="true" or id=="false":
            return Token(id,id,TokenType.Token_Bool,loc)
        else:
            return Token(id,id,TokenType.Token_Id,loc)
            
            
    @staticmethod
    def tokenize_split(code,tokens,type,loc,end):
        unbreak=True
        start=code.index()
        trans_time=0
        while (code.noEnd() and unbreak):
            c=code.current()
            if c==end:
                stre=code.substr(start,code.index())
                if trans_time!=0:
                    stre=stre
                tokens=Node.extend(
                    Token(stre,code.substr(start-1,code.index()+1),type,loc),
                    tokens
                )
                code.shift()
                unbreak=False
            else:
                if c=='\\':
                    trans_time=trans_time+1
                    code.shift()
                code.shift()
        if unbreak:
            raise LocationException(loc,"超出范围仍未结束")
        return tokens           
    
    @staticmethod
    def tokenize_ID(code,loc,tokens):
        unbreak=True
        while (code.noEnd() and unbreak):
            c=code.current()
            if (not (Token.isBlank(c) or Token.isQuoteLeft(c) or Token.isQuoteRight(c) or c=='"' or c=='`')):
                code.shift()
            else:
                token=Token.deal_id(code,loc)
                tokens=Node.extend(token,tokens)
                unbreak=False
        if unbreak:
            token=Token.deal_id(code,loc)
            tokens=Node.extend(token,tokens)
        return tokens
    
    @staticmethod
    def run(txt,lineSplit):
        code=Code(txt,lineSplit)
        tokens=None
        while code.noEnd():
            c=code.current()
            if Token.isBlank(c):
                code.shift()
            elif Token.isQuoteLeft(c):
                tokens=Node.extend(
                    Token(c,c,TokenType.Token_BracketLeft,code.currentLoc()),
                    tokens
                )
                code.shift()
            elif Token.isQuoteRight(c):
                tokens=Node.extend(
                    Token(c,c,TokenType.Token_BracketRight,code.currentLoc()),
                    tokens
                )
                code.shift()
            elif c=='"':
                loc=code.currentLoc()
                code.shift()
                tokens=Token.tokenize_split(code,tokens,TokenType.Token_String,loc,'"')
            elif c=='`':
                loc=code.currentLoc()
                code.shift()
                tokens=Token.tokenize_split(code,tokens,TokenType.Token_Comment,loc,'`')
            else:
                tokens=Token.tokenize_ID(code,code.currentLoc(),tokens)
        return tokens
    

'''
tokens=Token.run("(log 98 [a b {} \"abcde\" c d e])", "\n")
print(tokens)
'''