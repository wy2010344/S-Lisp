# coding=utf-8

class UserFunction(Function):
    """docstring for UserFunction"""
    def __init__(self, exp,parentScope):
        super(UserFunction, self).__init__()
        self.exp = exp
        self.parentScope = parentScope
        
    def exe(self,args):
        scope=Node.kvs_extend("args",args,parentScope)
        scope=Node.kvs_extend("this",self,scope)
        qr=QueueRun(scope)
        return qr.exe(exp)
    
    def Function_type(self):
        return FunctionType.Fun_user
    
    def __str__(self):
        return str(exp)