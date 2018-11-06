# coding=utf-8

from Node import Node
from Exp import ExpType
from Function import Function,FunctionType
from LocationException import LocationException
import traceback

class QueueRun:
    def __init__(self,scope):
        self.scope=scope
        
    '''
    执行多个表达式
    '''
    def exe(self,exp):
        ret=None
        tmp=exp.children
        while tmp!=None:
            ret=self.run(tmp.First())
            tmp=tmp.Rest()
        return ret
    '''
    执行单个表达式
    '''
    def run(self,exp):
        if exp.exp_type==ExpType.Exp_Let:
            cs=exp.children.Rest()
            while cs!=None:
                key=cs.First()
                cs=cs.Rest()
                value=QueueRun.interpret(cs.First(),self.scope)
                cs=cs.Rest()
                if key.exp_type==ExpType.Exp_LetId:
                    self.scope=QueueRun.kvs_extend(key.value,value,self.scope)
                elif key.exp_type==ExpType.Exp_LetSmall:
                    self.scope=QueueRun.letSmallMatch(key,value,self.scope)
            return None
        else:
            return QueueRun.interpret(exp,self.scope)
        
    @staticmethod
    def kvs_extend(key,value,kvs):
        return Node.kvs_extend(key,value,kvs)
    
    @staticmethod
    def letSmallMatch(small,v,scope):
        ks=small.children
        if (v==None or isinstance(v,Node)):
            vs=v 
            while ks!=None:
                v=None
                if vs!=None:
                    v=vs.First()
                k=ks.First()
                ks=ks.Rest()
                
                if k.exp_type==ExpType.Exp_LetId:
                    scope=QueueRun.kvs_extend(k.value,v,scope)
                elif k.exp_type==ExpType.Exp_LetSmall:
                    scope=QueueRun.letSmallMatch(k,v,scope)
                elif k.exp_type==ExpType.Exp_LetRest:
                    scope=QueueRun.kvs_extend(k.value,vs,scope)
                else:
                    raise k.exception("异常匹配"+str(k)+str(k.exp_type))
                
                if vs!=None:
                    vs=vs.Rest()
            return scope
        else:
            raise small.exception(str(v)+"不是合法的List类型，无法参与元组匹配："+str(small))
    @staticmethod
    def getPath(scope):
        path=None 
        tmp=scope
        while (tmp!=None and path==None):
            key=tmp.First()
            tmp=tmp.Rest()
            if key=="pathOf":
                if isinstance(tmp.First(),Function):
                    pathOf=tmp.First()
                    path=pathOf.exe(None)
            tmp=tmp.Rest()
        return path
    
    @staticmethod
    def calNode(list,scope):
        r=None
        x=list
        while x!=None:
            r=Node.extend(QueueRun.interpret(x.First(),scope),r)
            x=x.Rest()
        return r
    
    @staticmethod
    def match_Exception(scope,msg,e):
        return e.exception(getPath(scope)+":\t"+msg)
    
    @staticmethod
    def error_throw(msg,exp,scope,children):
        return exp.exception(msg+":\r\n"+str(children)+"\r\n"+str(exp.children.First())+"\r\n"+str(exp.children))
    
    @staticmethod
    def interpret(exp,scope):
        if exp.exp_type==ExpType.Exp_Small:
            children=QueueRun.calNode(exp.r_children,scope)
            o=children.First()
            if isinstance(o,Function):
                try:
                    return o.exe(children.Rest())
                except LocationException,lex:
                    lex.addStack(
                        QueueRun.getPath(scope),
                        exp.left.loc,
                        exp.right.loc,
                        str(exp)
                    )
                    raise lex
                except BaseException,ex:
                    exstr = traceback.format_exc()
                    print(exstr)
                    raise QueueRun.error_throw("函数执行内部错误",exp,scope,children)
            else:
                if o==None:
                    raise QueueRun.error_throw("未找到函数定义",exp,scope,children)
                else:
                    raise QueueRun.error_throw("不是函数",exp,scope,children)
        elif exp.exp_type==ExpType.Exp_Medium:
            return QueueRun.calNode(exp.r_children,scope);
        elif exp.exp_type==ExpType.Exp_Large:
            return UserFunction(exp,scope)
        elif exp.exp_type==ExpType.Exp_String:
            return exp.value
        elif exp.exp_type==ExpType.Exp_Int:
            return exp.int_value
        elif exp.exp_type==ExpType.Exp_Bool:
            return exp.bool_value
        elif exp.exp_type==ExpType.Exp_Id:
            paths=exp.paths
            if paths==None:
                raise QueueRun.match_exception(scope,exp.Value()+"不是合法的ID类型")
            else:
                c_scope=scope
                value=None
                while paths!=None:
                    key=paths.First()
                    value=Node.kvs_find1st(c_scope,key);
                    paths=paths.Rest()
                    if paths!=None:
                        if(value==None or isinstance(value,Node)):
                            c_scope=value
                        else:
                            raise QueueRun.match_Exception(scope,"计算"+str(paths)+",其中"+str(value)+"不是kvs类型:\n"+str(exp),exp);
            return value    
        else:
            return None
        
class UserFunction(Function):
    """docstring for UserFunction"""
    def __init__(self, exp,parentScope):
        self.exp = exp
        self.parentScope = parentScope
        
    def exe(self,args):
        scope=Node.kvs_extend("args",args,self.parentScope)
        scope=Node.kvs_extend("this",self,scope)
        qr=QueueRun(scope)
        return qr.exe(self.exp)
    
    def Function_type(self):
        return FunctionType.Fun_user
    
    def __str__(self):
        return str(self.exp)