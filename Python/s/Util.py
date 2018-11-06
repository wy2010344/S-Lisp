# coding=utf-8
import os,sys

class Util:
    
    @staticmethod
    def stringToEscape(s,start,end,kvs_map=None):
        sb=[]
        i=0
        sb.append(start)
        s_len=len(s)
        while i<s_len:
            c=s[i]
            if c=='\\':
                sb.append("\\\\")
            elif c==end:
                sb.append("\\")
                sb.append(end)
            else:
                if kvs_map!=None:
                    x=kvs_map.get(c,None)
                    if x==None:
                        sb.append(c)
                    else:
                        sb.append("\\")
                        sb.append(x)
                else:
                    sb.append(c)
            i=i+1
        sb.append(end)
        return "".join(sb)
    
    @staticmethod
    def absolute_from_relative(base_path,path):
        if(path[0]=='.'):
            nodes=base_path.split('/')
            names=path.split('/')
            sb=nodes
            sb.pop()
            for name in names:
                if name=='.':
                    pass
                elif name=="..":
                    sb.pop()
                elif name=="":
                    pass
                else:
                    sb.append(name)
            return "/".join(sb)
        else:
            return path
        
    '''
    终端的当前路径
    '''
    @staticmethod
    def exe_path(relative_path):
        return Util.absolute_from_relative(sys.path[0].replace('\\','/'), relative_path)
    
    @staticmethod
    def readTxt(path,linesplit):
        fo=open(path,"r")
        sb=fo.readlines()
        fo.close()
        return linesplit.join(sb)
    
    @staticmethod
    def writeTxt(path,content):
        fo=open(path,"w")
        fo.write(content)
        fo.close()