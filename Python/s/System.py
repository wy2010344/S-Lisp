
# coding=utf-8

from Util import Util
from Function import Function,FunctionType
from Node import Node
class System: 
    @staticmethod
    def toString(v,trans_str):
        if v==None:
            return "[]"
        elif trans_str:
            if type(v)==basestring:
                return Util.stringToEscape(v,'"','"')
            else:
                return str(v)
        else:
            return str(v)
    @staticmethod
    def library():
        m = None;
        
        m=Node.kvs_extend("first",FirstFun(),m);
        m=Node.kvs_extend("rest",RestFun(),m);
        m=Node.kvs_extend("extend",ExtendFun(),m);
        m=Node.kvs_extend("length",LengthFun(),m);
        m=Node.kvs_extend("empty?",IsemptyFun(),m);
        m=Node.kvs_extend("exist?",IsexistFun(),m);
        m=Node.kvs_extend("log",LogFun(),m);
        m=Node.kvs_extend("toString",ToStringFun(),m);
        m=Node.kvs_extend("stringify",StringifyFun(),m);
        m=Node.kvs_extend("if",IfFun(),m);
        m=Node.kvs_extend("eq",EqFun(),m);
        return m;
    

class FirstFun(Function):
    def __str__(self):
        return "first"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return args.First().First()
            
    
    
    
                        

class RestFun(Function):
    def __str__(self):
        return "rest"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return args.First().Rest()
            
    
    
    
                        

class ExtendFun(Function):
    def __str__(self):
        return "extend"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return Node.extend(args.First(),args.Rest().First())
            
    
    
    
                        

class LengthFun(Function):
    def __str__(self):
        return "length"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return args.First().Length()
            
    
    
    
                        

class IsemptyFun(Function):
    def __str__(self):
        return "empty?"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return args.First()==None
            
    
    
    
                        

class IsexistFun(Function):
    def __str__(self):
        return "exist?"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return args.First()!=None
            
    
    
    
                        

class LogFun(Function):
    def __str__(self):
        return "log"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        sb=[]
        tmp=args
        while tmp!=None:
            sb.append(str(tmp.First()))
            sb.append(" ")
            tmp=tmp.Rest()
        print("".join(sb))
        return None
            
    
    
    
                        

class ToStringFun(Function):
    def __str__(self):
        return "toString"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        b=args.First()
        return System.toString(b,False)
            
    
    
    
                        

class StringifyFun(Function):
    def __str__(self):
        return "stringify"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        b=args.First()
        return System.toString(b,True)
            
    
    
    
                        

class IfFun(Function):
    def __str__(self):
        return "if"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return IfFun.base_run(args)
            
    
    
    @staticmethod
    def base_run(args):
        if args.First():
            return args.Rest().First()
        else:
            args=args.Rest().Rest()
            if args!=None:
                return args.First()
            else:
                return None
            
    
                        

class EqFun(Function):
    def __str__(self):
        return "eq"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        eq=True 
        old=args.First()
        t=args.Rest()
        while(eq and t!=None):
            eq=(t.First()==old)
            old=t.First()
            t=t.Rest()
        return eq
            
    
    
    
                        
