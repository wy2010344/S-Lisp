

class Util:
    
    @staticmethod
    def stringToEsacpe(s,start,end,kvs_map=None):
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
        sb.append(end)
        return "".join(sb)