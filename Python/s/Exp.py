# coding=utf-8

from Token import Token,TokenType
from Location import Location
from Node import Node
from LocationException import LocationException

class ExpType:
    Exp_Large=0
    Exp_Medium=1
    Exp_Small=2
    Exp_String=3
    Exp_Int=4
    Exp_Bool=5
    Exp_Id=6
    Exp_Let=7
    Exp_LetId=8
    Exp_LetSmall=9
    Exp_LetRest=10
    
class Exp:
    """docstring for Exp"""
    def __init__(self, type):
        self.exp_type=type
        
    def exception(self,msg):
        pass
    def warn(self,msg):
        pass
        
    def isBracket(self):
        pass
    
    @staticmethod
    def resetLetID(k):
        if k.value.find('.')<0:
            return AtomExp(ExpType.Exp_LetId,k.token)
        else:
            raise k.exception("let表达式中，"+str(k)+"不是合法的key-id类型")
    
    @staticmethod    
    def resetLetSmall(small):
        vs=small.r_children
        children=None
        if vs!=None:
            k=vs.First()
            vs=vs.Rest()
            if k.exp_type==ExpType.Exp_Id:
                v=k.value
                if v.find("...")==0:
                    v=v[3:len(v)]
                    if v.find('.')<0:
                        children=Node.extend(
                            AtomExp(
                                ExpType.Exp_LetRest,
                                Token(v,k.token.old_value,k.token.token_type,k.token.loc)
                            ),
                            children
                        )
                    else:
                        raise k.exception("let表达式中，"+str(k)+"不是合法的剩余匹配ID")
                else:
                    children=Node.extend(Exp.resetLetID(k),children)
            while vs!=None:
                k=vs.First()
                vs=vs.Rest()
                if k.exp_type==ExpType.Exp_Small:
                    children=Node.extend(Exp.resetLetSmall(k),children)
                elif k.exp_type==ExpType.Exp_Id:
                    children=Node.extend(Exp.resetLetID(k),children)
                else:
                    raise k.exception("Let表达式中，不是合法的key类型"+str(k))
        return BracketExp(
            ExpType.Exp_LetSmall,
            small.left,
            children,
            small.right
        )
    
    @staticmethod
    def resetLetVKS(vks):
        children=None
        while vks!=None:
            v=vks.First()
            children=Node.extend(v,children)
            vks=vks.Rest()
            if vks!=None:
                k=vks.First()
                vks=vks.Rest()
                if k.exp_type==ExpType.Exp_Id:
                    children=Node.extend(Exp.resetLetID(k),children)
                elif k.exp_type==ExpType.Exp_Small:
                    if k.children==None:
                        raise k.warn("Let表达式中无意义 的空()，请检查："+str(v))
                    children=Node.extend(Exp.resetLetSmall(k),children)
                else:
                    raise k.exception("let表达式中，不合法的key类型:"+str(k))
            else:
                raise v.exception("let表达式中期待与value:"+str(v)+"匹配，却结束了let表达式")
        return children
    
    @staticmethod
    def check_Large(vs):
        while vs!=None:
            v=vs.First()
            vs=vs.Rest()
            if vs!=None:
                t=v.exp_type
                if (not (t==ExpType.Exp_Let or t==ExpType.Exp_Small or t==ExpType.Exp_Medium)):
                    v.warn("函数中定义无意义的表达式，请检查"+str(v))
    
    @staticmethod
    def Parse(tokens):
        root_loc=Location(0,0,0)
        root_left=Token("{","{",TokenType.Token_BracketLeft,root_loc)
        root_right=Token("}","}",TokenType.Token_BracketRight,root_loc)
        exp=BracketExp(ExpType.Exp_Large,root_left,None,root_right,None)
        caches=Node.extend(exp,None)
        xs=tokens
        children=None
        while xs!=None:
            x=xs.First()
            xs=xs.Rest()
            if x.token_type==TokenType.Token_BracketRight:
                tp=None
                if x.value==")":
                    tp=ExpType.Exp_Small
                elif x.value=="]":
                    tp=ExpType.Exp_Medium
                elif x.value=="}":
                    tp=ExpType.Exp_Large
                else:
                    raise LocationException(x.loc,"不正常的左匹配")
                caches=Node.extend(
                    BracketExp(tp,None,children,x),
                    caches
                )
                children=None
            elif x.token_type==TokenType.Token_BracketLeft:
                cache=caches.First()
                r_children=None
                tp=cache.exp_type
                if tp==ExpType.Exp_Large:
                    Exp.check_Large(children)
                else:
                    r_children=Node.reverse(children)
                caches_parent=caches.Rest()
                if caches_parent!=None:
                    p_exp=caches_parent.First()
                    if p_exp.exp_type==ExpType.Exp_Large:
                        #父表达式为函数
                        if tp==ExpType.Exp_Small:
                            if children==None:
                                raise LocationException(x.loc,"不允许空的()")
                            else:
                                first=children.First()
                                if (first.exp_type==ExpType.Exp_Id and first.value=="let"):
                                    tp=ExpType.Exp_Let
                                    if children.Length()==1:
                                        raise first.exception("不允许空的let表达式")
                                    else:
                                        children=Node.extend(
                                            children.First(),
                                            Exp.resetLetVKS(Node.reverse(children.Rest()))
                                        )
                                else:
                                    if (not (first.exp_type==ExpType.Exp_Large or first.exp_type==ExpType.Exp_Id or first.exp_type==ExpType.Exp_Small)):
                                        raise first.exception("函数调用第一个应该是id或{}或()，而不是"+str(first.exp_type)+str(first))
                children=Node.extend(
                    BracketExp(
                        tp,
                        x,
                        children,
                        cache.right,
                        r_children
                    ),
                    cache.children
                )
                caches=caches_parent
            else:
                tp=None
                deal=True
                if x.token_type==TokenType.Token_String:
                    tp=ExpType.Exp_String
                elif x.token_type==TokenType.Token_Int:
                    tp=ExpType.Exp_Int
                elif x.token_type==TokenType.Token_Bool:
                    tp=ExpType.Exp_Bool
                else:
                    parent=caches.First()
                    if parent.exp_type==ExpType.Exp_Medium:
                        if x.token_type==TokenType.Token_Prevent:
                            if(x.value=="true" or x.value=="false" or Token.isInt(x.value)):
                                raise LocationException(x.loc,"中括号中转义寻找作用域上的"+x.value)
                            tp=ExpType.Exp_Id
                        elif x.token_type==TokenType.Token_Id:
                            tp=ExpType.Exp_String
                        else:
                            deal=False
                    else:
                        if x.token_type==TokenType.Token_Prevent:
                            tp=ExpType.Exp_String
                        elif x.token_type==TokenType.Token_Id:
                            tp=ExpType.Exp_Id
                        else:
                            deal=False
                            
                if deal:
                    e=None
                    if tp==ExpType.Exp_Id:
                        e=IDExp(x)
                    elif tp==ExpType.Exp_Int:
                        e=IntExp(x)
                    elif tp==ExpType.Exp_Bool:
                        e=BoolExp(x)
                    else:
                        e=AtomExp(tp,x)
                    children=Node.extend(e,children)
        Exp.check_Large(children)
        return BracketExp(ExpType.Exp_Large,root_left,children,root_right)
class AtomExp(Exp):
    def __init__(self,type,token):
        Exp.__init__(self, type)
        self.token=token
        self.value=token.value
        
    def exception(self, msg):
        return LocationException(self.token.loc,msg)
    def warn(self, msg):
        print(str(self.token.loc)+msg)
        
    def isBracket(self):
        return False
    
    def __str__(self):
        return self.token.old_value
    
class IntExp(AtomExp):
    def __init__(self,token):
        AtomExp.__init__(self,ExpType.Exp_Int,token)
        self.int_value=int(token.value)
        
class BoolExp(AtomExp):
    def __init__(self,token):
        AtomExp.__init__(self,ExpType.Exp_Bool,token)
        self.bool_value=True if token.value=="true"  else False
        
class IDExp(AtomExp):
    def __init__(self,token):
        AtomExp.__init__(self,ExpType.Exp_Id,token)
        v=token.value
        len_v=len(v)
        if (v[0]=='.' or v[len_v-1]=='.'):
           self.paths=None
        else:
            i=0
            last_i=0
            r=None
            has_error=False
            while i<len_v:
                c=v[i]
                if c=='.':
                    node=v[last_i:i]
                    last_i=i+1
                    if node=="":
                        has_error=True
                    else:
                        r=Node.extend(node,r)
                i=i+1
            r=Node.extend(v[last_i:len_v],r)
            if has_error:
                raise LocationException(token.loc,v+"不是合法的ID，不允许连续的。")
            else:
                self.paths=Node.reverse(r)
                
class BracketExp(Exp):
    def __init__(self,type,left,children,right,r_children=None):
        Exp.__init__(self, type)
        self.left=left
        self.children=children
        self.right=right
        self.r_children=r_children
        
    def warn(self, msg):
        print(msg,self.left.loc,self.right.loc)
        
    def exception(self, msg):
        return LocationException(self.left.loc,msg)
    
    def isBracket(self):
        return True
    
    def __str__(self):
        sb=[];
        sb.append(self.left.old_value)
        t=self.children
        while t!=None:
            sb.append(str(t.First()))
            sb.append(" ")
            t=t.Rest()
        sb.append(self.right.old_value)
        return "".join(sb)

'''
tokens=Token.run("99 (let a 76 (b (c d)...e) []) (log 98 [a b {} \"abcde\" c d e])", "\n")
print(tokens)
try:
    exps=Exp.Parse(tokens)
    print(exps)
except LocationException,lex:
    print str(lex)
'''