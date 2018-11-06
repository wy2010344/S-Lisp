# coding=utf-8
from Function import Function,FunctionType
from Util import Util

class Node:
    """docstring for Node"""
    def __init__(self, first,rest):
        self.first = first
        self.rest = rest
        if rest==None:
            self.length=1
        else:
            self.length=rest.Length()+1

    def Length(self):
        return self.length

    def First(self):
        return self.first
    
    def Rest(self):
        return self.rest
    
    @staticmethod
    def toString_one(sb,o):
        if o==None:
            sb.append("[]")
        elif isinstance(o,Node):
            o.toString(sb)
        elif isinstance(o,basestring):
            sb.append(Util.stringToEscape(o,'"','"'))
        elif isinstance(o,Function):
            if o.Function_type()==FunctionType.Fun_buildIn:
                sb.append("'")
                sb.append(str(o))
            elif o.Function_type()==FunctionType.Fun_user:
                sb.append(str(o))
            elif o.Function_type()==FunctionType.Fun_cache:
                sb.append("[]")
            else:
                sb.append(str(o))
        elif isinstance(o,bool):
            if o:
                sb.append("true")
            else:
                sb.append("false")
        elif isinstance(o,int):
            sb.append(str(o))
        else:
            vx=str(o)
            if(vx==None):
                sb.append("[]")
            else:
                sb.append("'")
                sb.append(vx)
    
    def toString(self,sb):
        sb.append("[")
        Node.toString_one(sb,self.first)
        tmp=self.rest
        while tmp!=None:
            sb.append(" ")
            Node.toString_one(sb,tmp.First());
            tmp=tmp.Rest()
        sb.append("]")
        
    def __str__(self):
        sb=[];
        self.toString(sb)
        return "".join(sb)
    
    
    @staticmethod
    def extend(v,vs):
        return Node(v,vs)
    
    @staticmethod
    def kvs_extend(k,v,kvs):
        return Node(k,Node(v,kvs))
    
    @staticmethod
    def kvs_find1st(kvs,k):
        unfind=True
        r=None
        while(kvs!=None and unfind):
            key=kvs.First()
            kvs=kvs.Rest()
            if key==k:
                unfind=False
                r=kvs.First()
            kvs=kvs.Rest()
        return r
    
    @staticmethod
    def reverse(vs):
        r=None
        tmp=vs
        while tmp!=None:
            r=Node.extend(tmp.First(), r)
            tmp=tmp.Rest()
        return r
    
    @staticmethod
    def list(*args):
        r=None
        len_c=len(args)-1
        while len_c>-1:
            r=Node.extend(args[len_c],r)
            len_c=len_c-1
        return r