
# coding=utf-8

from Util import Util
from Function import Function,FunctionType
from Node import Node
class System: 
    @staticmethod
    def toString(v,trans_str):
        if v==None:
            return "[]"
        if isinstance(v, bool):
            "true" if v else "false"
        elif trans_str:
            if isinstance(v,basestring):
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
        m=Node.kvs_extend("if",IfFun(),m);
        m=Node.kvs_extend("eq",EqFun(),m);
        m=Node.kvs_extend("apply",ApplyFun(),m);
        m=Node.kvs_extend("log",LogFun(),m);
        m=Node.kvs_extend("toString",ToStringFun(),m);
        m=Node.kvs_extend("stringify",StringifyFun(),m);
        m=Node.kvs_extend("type",TypeFun(),m);
        m=Node.kvs_extend("+",AddFun(),m);
        m=Node.kvs_extend("-",SubFun(),m);
        m=Node.kvs_extend("*",MultiFun(),m);
        m=Node.kvs_extend("/",DivFun(),m);
        m=Node.kvs_extend(">",MBiggerFun(),m);
        m=Node.kvs_extend("<",MSmallerFun(),m);
        m=Node.kvs_extend("=",MEqFun(),m);
        m=Node.kvs_extend("and",AndFun(),m);
        m=Node.kvs_extend("or",OrFun(),m);
        m=Node.kvs_extend("not",NotFun(),m);
        m=Node.kvs_extend("str-length",Str_lengthFun(),m);
        m=Node.kvs_extend("str-charAt",Str_charAtFun(),m);
        m=Node.kvs_extend("str-substr",Str_substrFun(),m);
        m=Node.kvs_extend("str-join",Str_joinFun(),m);
        m=Node.kvs_extend("str-split",Str_splitFun(),m);
        m=Node.kvs_extend("str-upper",Str_upperFun(),m);
        m=Node.kvs_extend("str-lower",Str_lowerFun(),m);
        m=Node.kvs_extend("str-trim",Str_trimFun(),m);
        m=Node.kvs_extend("str-indexOf",Str_indexOfFun(),m);
        m=Node.kvs_extend("quote",QuoteFun(),m);
        m=Node.kvs_extend("list",ListFun(),m);
        m=Node.kvs_extend("kvs-find1st",Kvs_find1stFun(),m);
        m=Node.kvs_extend("kvs-extend",Kvs_extendFun(),m);
        m=Node.kvs_extend("type?",IstypeFun(),m);
        m=Node.kvs_extend("call",CallFun(),m);
        m=Node.kvs_extend("!=",MNotEqFun(),m);
        m=Node.kvs_extend("empty-fun",Empty_funFun(),m);
        m=Node.kvs_extend("default",DefaultFun(),m);
        m=Node.kvs_extend("len",LenFun(),m);
        m=Node.kvs_extend("indexOf",IndexOfFun(),m);
        m=Node.kvs_extend("if-run",If_runFun(),m);
        m=Node.kvs_extend("loop",LoopFun(),m);
        m=Node.kvs_extend("reverse",ReverseFun(),m);
        m=Node.kvs_extend("reduce",ReduceFun(),m);
        m=Node.kvs_extend("reduce-right",Reduce_rightFun(),m);
        m=Node.kvs_extend("kvs-reduce",Kvs_reduceFun(),m);
        m=Node.kvs_extend("kvs-reduce-right",Kvs_reduce_rightFun(),m);
        m=Node.kvs_extend("kvs-path",Kvs_pathFun(),m);
        m=Node.kvs_extend("kvs-path-run",Kvs_path_runFun(),m);
        m=Node.kvs_extend("offset",OffsetFun(),m);
        m=Node.kvs_extend("slice-to",Slice_toFun(),m);
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
            
    
    
    
                        

class ApplyFun(Function):
    def __str__(self):
        return "apply"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        f=args.First()
        n_args=args.Rest().First()
        return f.exe(n_args)
            
    
    
    
                        

class LogFun(Function):
    def __str__(self):
        return "log"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        sb=[]
        tmp=args
        while tmp!=None:
            sb.append(System.toString(tmp.First(),True))
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
            
    
    
    
                        

class TypeFun(Function):
    def __str__(self):
        return "type"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
            return TypeFun.base_run(args.First())
            
    
    
    @staticmethod
    def base_run(o):
        if (o==None or isinstance(o,Node)):
            return "list"
        elif isinstance(o,Function):
            return "function"
        elif isinstance(o,bool):
            return "bool"
        elif isinstance(o,basestring):
            return "string"
        elif isinstance(o,int):
            return "int"
        else:
            return ""
            
    
                        

class AddFun(Function):
    def __str__(self):
        return "+"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        all=0
        t=args
        while t!=None:
            all=all+t.First()
            t=t.Rest()
        return all
            
    
    
    
                        

class SubFun(Function):
    def __str__(self):
        return "-"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        all=args.First()
        args=args.Rest()
        while args!=None:
            all=all-args.First()
            args=args.Rest()
        return all
            
    
    
    
                        

class MultiFun(Function):
    def __str__(self):
        return "*"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        all=0
        t=args
        while t!=None:
            all=all*t.First()
            t=t.Rest()
        return all
            
    
    
    
                        

class DivFun(Function):
    def __str__(self):
        return "/"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        all=args.First()
        args=args.Rest()
        while args!=None:
            all=all/args.First()
            args=args.Rest()
        return all
            
    
    
    
                        

class MBiggerFun(Function):
    def __str__(self):
        return ">"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        ret=True 
        last=args.First()
        args=args.Rest()
        while (args!=None and ret):
            ret=last>args.First()
            args=args.Rest()
        return ret
            
    
    
    
                        

class MSmallerFun(Function):
    def __str__(self):
        return "<"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        ret=True 
        last=args.First()
        args=args.Rest()
        while (args!=None and ret):
            ret=last<args.First()
            args=args.Rest()
        return ret
            
    
    
    
                        

class MEqFun(Function):
    def __str__(self):
        return "="
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return MEqFun.base_run(args)
            
    
    
    @staticmethod
    def base_run(args):
        ret=True 
        last=args.First()
        args=args.Rest()
        while (args!=None and ret):
            ret=(last==args.First())
            args=args.Rest()
        return ret
            
    
                        

class AndFun(Function):
    def __str__(self):
        return "and"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        init=True
        t=args
        while (t!=None and init):
            init=t.First()
            t=t.Rest()
        return init
            
    
    
    
                        

class OrFun(Function):
    def __str__(self):
        return "or"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        init=False
        t=args
        while(t!=None and (not init)):
            init=t.First()
            t=t.Rest()
        return t
            
    
    
    
                        

class NotFun(Function):
    def __str__(self):
        return "not"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return (not args.First())
            
    
    
    
                        

class Str_lengthFun(Function):
    def __str__(self):
        return "str-length"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return len(args.First())
            
    
    
    
                        

class Str_charAtFun(Function):
    def __str__(self):
        return "str-charAt"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return args.First()[args.Rest().First()]
            
    
    
    
                        

class Str_substrFun(Function):
    def __str__(self):
        return "str-substr"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        a=args.First()
        args=args.Rest()
        begin=args.First()
        args=args.Rest()
        if args==None:
            return a[begin:len(a)]
        else:
            return a[begin:(begin+args.First())]
            
    
    
    
                        

class Str_joinFun(Function):
    def __str__(self):
        return "str-join"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        array=args.First()
        split="";
        args=args.Rest()
        if args!=None:
            split=args.First()
        sb=[]
        while array!=None:
            sb.append(array.First())
            array=array.Rest()
        return split.join(sb)
            
    
    
    
                        

class Str_splitFun(Function):
    def __str__(self):
        return "str-split"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        a=args.First()
        split=""
        args=args.Rest()
        if args!=None:
            split=args.First()
        if split=="":
            sb=[]
            i=0
            len_a=len(a)
            while i<len_a:
                sb.append(a[i])
                i=i+1
            return sb
        else:
            return a.split(split)
            
    
    
    
                        

class Str_upperFun(Function):
    def __str__(self):
        return "str-upper"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return args.First().upper()
            
    
    
    
                        

class Str_lowerFun(Function):
    def __str__(self):
        return "str-lower"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return args.First().lower()
            
    
    
    
                        

class Str_trimFun(Function):
    def __str__(self):
        return "str-trim"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        return args.First().strip()
            
    
    
    
                        

class Str_indexOfFun(Function):
    def __str__(self):
        return "str-indexOf"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        stre=args.First()
        args=args.Rest()
        v=args.First()
        return stre.find(v)
            
    
    
    
                        

class QuoteFun(Function):
    def __str__(self):
        return "{(first args ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        return args.First()
            
    
    
    
                        

class ListFun(Function):
    def __str__(self):
        return "{args }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        return args
            
    
    
    
                        

class Kvs_find1stFun(Function):
    def __str__(self):
        return "{(let (key kvs ) args find1st this ) (let (k v ...kvs ) args ) (if-run (str-eq k key ) {v } {(find1st key kvs ) } ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        kvs=args.First()
        args=args.Rest()
        key=args.First()
        return Node.kvs_find1st(kvs,key)
            
    
    
    
                        

class Kvs_extendFun(Function):
    def __str__(self):
        return "{(let (k v kvs ) args ) (extend k (extend v kvs ) ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        key=args.First()
        args=args.Rest()
        value=args.First()
        args=args.Rest()
        kvs=args.First()
        return Node.kvs_extend(key,value,kvs)
            
    
    
    
                        

class IstypeFun(Function):
    def __str__(self):
        return "{(let (x n ) args ) (str-eq (type x ) n ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        x=args.First()
        args=args.Rest()
        n=args.First()
        return (TypeFun.base_run(x)==n)
            
    
    
    
                        

class CallFun(Function):
    def __str__(self):
        return "call"
    def Function_type(self):
        return FunctionType.Fun_buildIn
    def exe(self,args):
        
        run=args.First()
        args=args.Rest()
        return run.exe(args)
            
    
    
    
                        

class MNotEqFun(Function):
    def __str__(self):
        return "{(not (apply = args ) ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        return (not MEqFun.base_run(args))
            
    
    
    
                        

class Empty_funFun(Function):
    def __str__(self):
        return "{}"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        return None
            
    
    
    
                        

class DefaultFun(Function):
    def __str__(self):
        return "{(let (a d ) args ) (if (exist? a ) a d ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        v=args.First()
        if v!=None:
            return v
        else:
            args=args.Rest()
            return args.First()
            
    
    
    
                        

class LenFun(Function):
    def __str__(self):
        return "{(let (cs ) args ) (if-run (exist? cs ) {(length cs ) } {0 } ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        list=args.First()
        if list!=None:
            return list.Length()
        else:
            return 0
            
    
    
    
                        

class IndexOfFun(Function):
    def __str__(self):
        return "{(let (vs k is_eq ) args is_eq (default eq ) ) (loop {(let ((v ...vs ) index ) args ) (if-run (is_eq v k ) {(list false index ) } {(if-run (exist? vs ) {(list true (list vs (+ index 1 ) ) ) } ) } ) } (list vs 0 ) ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        vs=args.First()
        args=args.Rest()
        k=args.First()
        args=args.Rest()
        eq=None
        if args!=None:
            eq=args.First()
        else:
            eq=EqFun()
        index=-1
        flag=0
        while (vs!=None and index==-1):
            if eq.exe(Node.list(vs.First(),k)):
                index=flag
            else:
                vs=vs.Rest()
                flag=flag+1
        if index==-1:
            return None
        else:
            return index

            
    
    
    
                        

class If_runFun(Function):
    def __str__(self):
        return "{(let (a b c ) args ) (let x (default (if a b c ) ) ) (x ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        o=IfFun.base_run(args)
        if o==None:
            return None
        else:
            return o.exe(None)
            
    
    
    
                        

class LoopFun(Function):
    def __str__(self):
        return "{(let (f init ) args loop this ) (let (will init ) (f init ) ) (if-run will {(loop f init ) } {init } ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        f=args.First()
        args=args.Rest()
        init=None
        if args!=None:
            init=args.First()
        will=True 
        while will:
            o=f.exe(Node.list(init))
            will=o.First()
            o=o.Rest()
            init=o.First()
        return init
            
    
    
    
                        

class ReverseFun(Function):
    def __str__(self):
        return "{(let (xs ) args ) (reduce xs {(let (init x ) args ) (extend x init ) } [] ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        return ReverseFun.base_run(args.First())
            
    
    
    @staticmethod
    def base_run(list):
        return Node.reverse(list)
            
    
                        

class ReduceFun(Function):
    def __str__(self):
        return "{(let (xs run init ) args reduce this ) (if-run (exist? xs ) {(let (x ...xs ) xs ) (let init (run init x ) ) (reduce xs run init ) } {init } ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        list=args.First()
        args=args.Rest()
        return ReduceFun.base_run(list,args)
            
    
    
    @staticmethod
    def base_run(list,args):
        f=args.First()
        args=args.Rest()
        init=args.First()
        while list!=None:
            x=list.First()
            list=list.Rest()
            nargs=Node.list(init,x)
            init=f.exe(nargs)
        return init
            
    
                        

class Reduce_rightFun(Function):
    def __str__(self):
        return "{(let (xs run init ) args reduce-right this ) (if-run (exist? xs ) {(let (x ...xs ) xs ) (run (reduce-right xs run init ) x ) } {init } ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        list=args.First()
        list=ReverseFun.base_run(list)
        args=args.Rest()
        return ReduceFun.base_run(list,args)
            
    
    
    
                        

class Kvs_reduceFun(Function):
    def __str__(self):
        return "{(let (kvs run init ) args kvs-reduce this ) (if-run (exist? kvs ) {(let (k v ...kvs ) kvs ) (let init (run init v k ) ) (kvs-reduce kvs run init ) } {init } ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        kvs=args.First()
        args=args.Rest()
        return Kvs_reduceFun.base_run(kvs,args)
            
    
    
    @staticmethod
    def base_run(kvs,args):
        f=args.First()
        args=args.Rest()
        init=args.First()
        while kvs!=None:
            key=kvs.First()
            kvs=kvs.Rest()
            value=kvs.First()
            kvs=kvs.Rest()
            nargs=Node.list(init,value,key)
            init=f.exe(nargs)
        return init
            
    
                        

class Kvs_reduce_rightFun(Function):
    def __str__(self):
        return "{(let (kvs run init ) args kvs-reduce-right this ) (if-run (exist? kvs ) {(let (k v ...kvs ) kvs ) (run (kvs-reduce-right kvs run init ) v k ) } {init } ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        kvs=args.First()
        kvs=Kvs_reverseFun.base_run(kvs)
        args=args.Rest()
        return Kvs_reduceFun.base_run(kvs,args)
            
    
    
    
                        

class Kvs_pathFun(Function):
    def __str__(self):
        return "{(let (e paths ) args kvs-path this ) (if-run (exist? paths ) {(let (path ...paths ) paths ) (kvs-path (kvs-find1st e path ) paths ) } {e } ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        kvs=args.First()
        args=args.Rest()
        paths=args.First()
        return Kvs_pathFun.base_run(kvs,args)
            
    
    
    @staticmethod
    def base_run(kvs,paths):
        while paths!=None:
            path=paths.First()
            value=Node.kvs_find1st(kvs,path)
            paths=paths.Rest()
            if paths!=None:
                kvs=value
        return value
            
    
                        

class Kvs_path_runFun(Function):
    def __str__(self):
        return "{(let (e paths ...ps ) args ) (apply (kvs-path e paths ) ps ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        kvs=args.First()
        kvs=kvs.Rest()
        paths=args.First()
        args=args.Rest()
        return Kvs_pathFun.base_run(kvs,paths).exe(args)
            
    
    
    
                        

class OffsetFun(Function):
    def __str__(self):
        return "{(let (list i ) args offset this ) (if-run (= i 0 ) {list } {(offset (rest list ) (- i 1 ) ) } ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        list=args.First()
        args=args.Rest()
        i=args.First()
        return OffsetFun.base_run(list,i)
            
    
    
    @staticmethod
    def base_run(list,i):
        while i!=0:
            list=list.Rest()
            i=i-1
        return list
            
    
                        

class Slice_toFun(Function):
    def __str__(self):
        return "{(let (xs to ) args slice-to this ) (if-run (= to 0 ) {[] } {(let (x ...xs ) xs ) (extend x (slice-to xs (- to 1 ) ) ) } ) }"
    def Function_type(self):
        return FunctionType.Fun_user
    def exe(self,args):
        
        list=args.First()
        args=args.Rest()
        i=args.First()
        r=None
        while i!=0:
            r=Node.extend(list.First(),r)
            list=list.Rest()
            i=i-1
        return ReverseFun.base_run(r)
            
    
    
    
                        
