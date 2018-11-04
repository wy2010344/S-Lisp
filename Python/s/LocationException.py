# coding=utf-8

class LocationException(BaseException):
    def __init__(self,loc,msg):
        BaseException.__init__(self)
        self.loc=loc
        self.msg=msg
        self.stacks=[]
        
    def addStack(self,path,left,right,exp):
        self.stacks.push({
            'path':path,
            'left':left,
            'right':right,
            'exp':exp
        })
    def __str__(self):
        sb=[]
        for stack in self.stacks:
            sb.append(stack.path)
            sb.append("\t")
            sb.append(str(stack.left))
            sb.append("-")
            sb.append(str(stack.right))
            sb.append("\t")
            sb.append(stack.exp)
            sb.append("\r\n")
        sb.append(str(self.loc))
        sb.append("\r\n")
        sb.append(self.msg)
        sb.append("\r\n")
        return "".join(sb)
        