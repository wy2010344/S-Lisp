
using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class System
    {
    

        class FirstFun:Function{
            private static FirstFun _ini_=new FirstFun();
            public static FirstFun instance(){return _ini_;}
            public override string ToString(){return "first";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                return (args.First() as Node<Object>).First();
            
            }
            
        }
			            

        class RestFun:Function{
            private static RestFun _ini_=new RestFun();
            public static RestFun instance(){return _ini_;}
            public override string ToString(){return "rest";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                return (args.First() as Node<Object>).Rest();
            
            }
            
        }
			            

        class ExtendFun:Function{
            private static ExtendFun _ini_=new ExtendFun();
            public static ExtendFun instance(){return _ini_;}
            public override string ToString(){return "extend";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                return Node<Object>.extend(args.First(),(args.Rest().First() as Node<Object>));
            
            }
            
        }
			            

        class LengthFun:Function{
            private static LengthFun _ini_=new LengthFun();
            public static LengthFun instance(){return _ini_;}
            public override string ToString(){return "length";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                return (args.First() as Node<Object>).Length();
            
            }
            
        }
			            

        class AddFun:Function{
            private static AddFun _ini_=new AddFun();
            public static AddFun instance(){return _ini_;}
            public override string ToString(){return "+";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                int all=0;
                for(Node<Object> t=args;t!=null;t=t.Rest())
                {
                    int it=(int)t.First();
                    all=all+it;
                }
                return all;
            
            }
            
        }
			            

        class SubFun:Function{
            private static SubFun _ini_=new SubFun();
            public static SubFun instance(){return _ini_;}
            public override string ToString(){return "-";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                int all=(int)args.First();
                args=args.Rest();
                for(Node<Object> t=args;t!=null;t=t.Rest())
                {
                    int it=(int)args.First();
                    all=all-it;
                }
                return all;
            
            }
            
        }
			            

        class MBiggerFun:Function{
            private static MBiggerFun _ini_=new MBiggerFun();
            public static MBiggerFun instance(){return _ini_;}
            public override string ToString(){return ">";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                bool ret=true;
                int last=(int)args.First();
                args=args.Rest();
                while(args!=null && ret)
                {
                    int current=(int)args.First();
                    ret=(last>current);
                    last=current;
                    args=args.Rest();
                }
                return ret;
            
            }
            
        }
			            

        class MSmallerFun:Function{
            private static MSmallerFun _ini_=new MSmallerFun();
            public static MSmallerFun instance(){return _ini_;}
            public override string ToString(){return "<";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                bool ret=true;
                int last=(int)args.First();
                args=args.Rest();
                while(args!=null && ret)
                {
                    int current=(int)args.First();
                    ret=(last<current);
                    last=current;
                    args=args.Rest();
                }
                return ret;
            
            }
            
        }
			            

        class MEqFun:Function{
            private static MEqFun _ini_=new MEqFun();
            public static MEqFun instance(){return _ini_;}
            public override string ToString(){return "=";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                return base_run(args);
            
            }
            
            public static bool base_run(Node<Object> args){
                bool ret=true;
                int last=(int)args.First();
                args=args.Rest();
                while(args!=null && ret)
                {
                    int current=(int)args.First();
                    ret=(last==current);
                    last=current;
                    args=args.Rest();
                }
                return ret;
            }
            
        }
			            

        class AndFun:Function{
            private static AndFun _ini_=new AndFun();
            public static AndFun instance(){return _ini_;}
            public override string ToString(){return "and";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                bool ret=true;
                while(args!=null && ret)
                {
                    ret=(bool)args.First();
                    args=args.Rest();
                }
                return ret;
            
            }
            
        }
			            

        class OrFun:Function{
            private static OrFun _ini_=new OrFun();
            public static OrFun instance(){return _ini_;}
            public override string ToString(){return "or";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                bool ret=false;
                while(args!=null && (!ret))
                {
                    ret=(bool)args.First();
                    args=args.Rest();
                }
                return ret;
            
            }
            
        }
			            

        class NotFun:Function{
            private static NotFun _ini_=new NotFun();
            public static NotFun instance(){return _ini_;}
            public override string ToString(){return "not";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                return !(bool)args.First();
            
            }
            
        }
			            

        class IsemptyFun:Function{
            private static IsemptyFun _ini_=new IsemptyFun();
            public static IsemptyFun instance(){return _ini_;}
            public override string ToString(){return "empty?";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                return args.First()==null;
            
            }
            
        }
			            

        class IsexistFun:Function{
            private static IsexistFun _ini_=new IsexistFun();
            public static IsexistFun instance(){return _ini_;}
            public override string ToString(){return "exist?";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                return args.First()!=null;
            
            }
            
        }
			            

        class LogFun:Function{
            private static LogFun _ini_=new LogFun();
            public static LogFun instance(){return _ini_;}
            public override string ToString(){return "log";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                StringBuilder sb = new StringBuilder();
                args.toString(sb);
                Console.WriteLine(sb.ToString());
                return null;
            
            }
            
        }
			            

        class IfFun:Function{
            private static IfFun _ini_=new IfFun();
            public static IfFun instance(){return _ini_;}
            public override string ToString(){return "if";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                return base_run(args);
            
            }
            
            public static Object base_run(Node<Object> args){
                bool c=(bool)args.First();
                args=args.Rest();
                if(c){
                    return args.First();
                }else{
                    args=args.Rest();
                    if(args!=null)
                    {
                        return args.First();
                    }else{
                        return null;
                    }
                }
            }

            
        }
			            

        class EqFun:Function{
            private static EqFun _ini_=new EqFun();
            public static EqFun instance(){return _ini_;}
            public override string ToString(){return "eq";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                Object a=args.First();
                args=args.Rest();
                Object b=args.First();
                return a==b;
            
            }
            
        }
			            

        class ApplyFun:Function{
            private static ApplyFun _ini_=new ApplyFun();
            public static ApplyFun instance(){return _ini_;}
            public override string ToString(){return "apply";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                Function f=args.First() as Function;
                args=args.Rest();
                return f.exec(args.First() as Node<Object>);
            
            }
            
        }
			            

        class StringifyFun:Function{
            private static StringifyFun _ini_=new StringifyFun();
            public static StringifyFun instance(){return _ini_;}
            public override string ToString(){return "stringify";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                StringBuilder sb=new StringBuilder();
                Node<Object>.toString(sb, args.First(), false);
                return sb.ToString();
            
            }
            
        }
			            

        class TypeFun:Function{
            private static TypeFun _ini_=new TypeFun();
            public static TypeFun instance(){return _ini_;}
            public override string ToString(){return "type";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                Object b=args.First();
                return base_run(b);
            
            }
            
                public static String base_run(Object b){
                    if(b==null){
                        return "list";
                    }else{
                        if(b is Node<Object>)
                        {
                            return "list";
                        }else if(b is Function)
                        {
                            return "function";
                        }else if(b is int)
                        {
                            return "int";
                        }else if(b is String)
                        {
                            return "string";
                        }else if(b is bool)
                        {
                            return "bool";
                        }else{
                            if(b is Token)
                            {
                                return "token";
                            }else if(b is Exp)
                            {
                                return "exp";
                            }else if(b is Location)
                            {
                                return "location";
                            }else{
                                return "user";
                            }
                        }
                    }
                }
            
        }
			            

        class Str_eqFun:Function{
            private static Str_eqFun _ini_=new Str_eqFun();
            public static Str_eqFun instance(){return _ini_;}
            public override string ToString(){return "str-eq";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                String a=args.First() as String;
                args=args.Rest();
                String b=args.First() as String;
                return a==b;
            
            }
            
        }
			            

        class Str_lengthFun:Function{
            private static Str_lengthFun _ini_=new Str_lengthFun();
            public static Str_lengthFun instance(){return _ini_;}
            public override string ToString(){return "str-length";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                String a=args.First() as String;
                return a.Length;
            
            }
            
        }
			            

        class Str_charAtFun:Function{
            private static Str_charAtFun _ini_=new Str_charAtFun();
            public static Str_charAtFun instance(){return _ini_;}
            public override string ToString(){return "str-charAt";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                String a=args.First() as String;
                args=args.Rest();
                int b=(int)args.First();
                return ""+a[b];
            
            }
            
        }
			            

        class Str_substrFun:Function{
            private static Str_substrFun _ini_=new Str_substrFun();
            public static Str_substrFun instance(){return _ini_;}
            public override string ToString(){return "str-substr";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                String a=args.First() as String;
                args=args.Rest();
                int begin=(int)args.First();
                args=args.Rest();
                if(args==null)
                {
                    return a.Substring(begin);
                }else
                {
                    return a.Substring(begin,(int)args.First());
                }
            
            }
            
        }
			            

        class Str_joinFun:Function{
            private static Str_joinFun _ini_=new Str_joinFun();
            public static Str_joinFun instance(){return _ini_;}
            public override string ToString(){return "str-join";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                Node<Object> vs=args.First() as Node<Object>;
                args=args.Rest();
                String split="";
                if(args!=null)
                {
                    split=args.First() as String;
                }
                StringBuilder sb=new StringBuilder();
                for(Node<Object> tmp=vs;tmp!=null;tmp=tmp.Rest())
                {
                    sb.Append(tmp.First() as String);
                    if(tmp.Rest()!=null)
                    {
                        sb.Append(split);
                    }
                }
                return sb.ToString();
            
            }
            
        }
			            

        class Str_splitFun:Function{
            private static Str_splitFun _ini_=new Str_splitFun();
            public static Str_splitFun instance(){return _ini_;}
            public override string ToString(){return "str-split";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                String str = args.First() as String;
                args = args.Rest();
                String split = "";
                if (args != null)
                {
                    split = args.First() as String;
                }
                Node<Object> r = null;
                if (split == "")
                {
                    for (int i = str.Length-1; i>-1; i--)
                    {
                        r = Node<Object>.extend(str[i] + "", r);
                    }
                }
                else
                {
                    int last_i = 0;
                    while (last_i >-1)
                    {
                        int new_i = str.IndexOf(split, last_i);
                        if (new_i > -1)
                        {
                            r = Node<Object>.extend(str.Substring(last_i, new_i - last_i), r);
                            last_i = new_i+split.Length;
                        }
                        else
                        {
                            //最后
                            r = Node<Object>.extend(str.Substring(last_i), r);
                            last_i = new_i;
                        }
                    }
                    r = Node<Object>.reverse(r);
                }
                return r;
            
            }
            
        }
			            

        class Str_upperFun:Function{
            private static Str_upperFun _ini_=new Str_upperFun();
            public static Str_upperFun instance(){return _ini_;}
            public override string ToString(){return "str-upper";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                return (args.First() as String).ToUpper();
            
            }
            
        }
			            

        class Str_lowerFun:Function{
            private static Str_lowerFun _ini_=new Str_lowerFun();
            public static Str_lowerFun instance(){return _ini_;}
            public override string ToString(){return "str-lower";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
            public override object exec(Node<object> args){
                
                return (args.First() as String).ToLower();
            
            }
            
        }
			            

        class QuoteFun:Function{
            private static QuoteFun _ini_=new QuoteFun();
            public static QuoteFun instance(){return _ini_;}
            public override string ToString(){return "{(first args ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                return args.First();
            
            }
            
        }
			            

        class ListFun:Function{
            private static ListFun _ini_=new ListFun();
            public static ListFun instance(){return _ini_;}
            public override string ToString(){return "{args }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                return args;
            
            }
            
        }
			            

        class IstypeFun:Function{
            private static IstypeFun _ini_=new IstypeFun();
            public static IstypeFun instance(){return _ini_;}
            public override string ToString(){return "{(let (x n ) args ) (str-eq (type x ) n ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Object x=args.First();
                args=args.Rest();
                String n=args.First() as String;
                return (TypeFun.base_run(x)==n);
            
            }
            
        }
			            

        class MNotEqFun:Function{
            private static MNotEqFun _ini_=new MNotEqFun();
            public static MNotEqFun instance(){return _ini_;}
            public override string ToString(){return "{(not (apply = args ) ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                return !MEqFun.base_run(args);
            
            }
            
        }
			            

        class ReverseFun:Function{
            private static ReverseFun _ini_=new ReverseFun();
            public static ReverseFun instance(){return _ini_;}
            public override string ToString(){return "{(let (xs ) args ) (reduce xs {(let (init x ) args ) (extend x init ) } [] ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                return base_run(args.First() as Node<Object>);
            
            }
            
            public static Node<Object> base_run(Node<Object> list){
                Node<Object> r=null;
                Node<Object> tmp=list;
                while(tmp!=null){
                    Object v=tmp.First();
                    r=Node<Object>.extend(v,r);
                    tmp=tmp.Rest();
                }
                return r;
            }
            
        }
			            

        class Kvs_reverseFun:Function{
            private static Kvs_reverseFun _ini_=new Kvs_reverseFun();
            public static Kvs_reverseFun instance(){return _ini_;}
            public override string ToString(){return "{(let (kvs ) args ) (kvs-reduce kvs {(let (init v k ) args ) (kvs-extend k v init ) } [] ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                return base_run(args.First() as Node<Object>);
            
            }
            
            public static Node<Object> base_run(Node<Object> kvs){
                Node<Object> r=null;
                Node<Object> tmp=kvs;
                while(tmp!=null){
                    String key=tmp.First() as String;
                    tmp=tmp.Rest();
                    Object value=tmp.First();
                    tmp=tmp.Rest();
                    r=Node<Object>.kvs_extend(key,value,r);
                }
                return r;
            }
            
        }
			            

        class Empty_funFun:Function{
            private static Empty_funFun _ini_=new Empty_funFun();
            public static Empty_funFun instance(){return _ini_;}
            public override string ToString(){return "{}";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                return null;
            
            }
            
        }
			            

        class DefaultFun:Function{
            private static DefaultFun _ini_=new DefaultFun();
            public static DefaultFun instance(){return _ini_;}
            public override string ToString(){return "{(let (a d ) args ) (if (exist? a ) a d ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Object v=args.First();
                if(v!=null){
                    return v;
                }else{
                    args=args.Rest();
                    return args.First();
                }
            
            }
            
        }
			            

        class If_runFun:Function{
            private static If_runFun _ini_=new If_runFun();
            public static If_runFun instance(){return _ini_;}
            public override string ToString(){return "{(let (a b c ) args ) (let x (default (if a b c ) ) ) (x ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Object o=IfFun.base_run(args);
                if(o==null){
                    return null;
                }else{
                    return (o as Function).exec(null);
                }
            
            }
            
        }
			            

        class ReduceFun:Function{
            private static ReduceFun _ini_=new ReduceFun();
            public static ReduceFun instance(){return _ini_;}
            public override string ToString(){return "{(let (xs run init ) args reduce this ) (if-run (exist? xs ) {(let (x ...xs ) xs ) (let init (run init x ) ) (reduce xs run init ) } {init } ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Node<Object> list = args.First() as Node<Object>;
                args = args.Rest();
                return base_run(list,args);
            
            }
            
            public static Object base_run(Node<Object> list,Node<Object> args){

                Function f = args.First() as Function;
                args = args.Rest();
                Object init = args.First();
                while (list != null)
                {
                    Object x = list.First();
                    list = list.Rest();
                    Node<Object> nargs = Node<Object>.list(init, x);
                    init = f.exec(nargs);
                }
                return init;
            }
            
        }
			            

        class Reduce_rightFun:Function{
            private static Reduce_rightFun _ini_=new Reduce_rightFun();
            public static Reduce_rightFun instance(){return _ini_;}
            public override string ToString(){return "{(let (xs run init ) args reduce-right this ) (if-run (exist? xs ) {(let (x ...xs ) xs ) (run (reduce-right xs run init ) x ) } {init } ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Node<Object> list = args.First() as Node<Object>;
                list=ReverseFun.base_run(list);
                args = args.Rest();
                return ReduceFun.base_run(list,args);
            
            }
            
        }
			            

        class Kvs_reduceFun:Function{
            private static Kvs_reduceFun _ini_=new Kvs_reduceFun();
            public static Kvs_reduceFun instance(){return _ini_;}
            public override string ToString(){return "{(let (kvs run init ) args kvs-reduce this ) (if-run (exist? kvs ) {(let (k v ...kvs ) kvs ) (let init (run init v k ) ) (kvs-reduce kvs run init ) } {init } ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Node<Object> kvs = args.First() as Node<Object>;
                args = args.Rest();
                return base_run(kvs,args);
            
            }
            
            public static Object base_run(Node<Object> kvs,Node<Object> args){
                Function f = args.First() as Function;
                args = args.Rest();
                Object init = args.First();
                while (kvs != null)
                {
                    Object key = kvs.First();
                    kvs = kvs.Rest();
                    Object value = kvs.First();
                    kvs = kvs.Rest();
                    Node<Object> nargs = Node<Object>.list(init,value,key);
                    init = f.exec(nargs);
                }
                return init;
            }
            
        }
			            

        class Kvs_reduce_rightFun:Function{
            private static Kvs_reduce_rightFun _ini_=new Kvs_reduce_rightFun();
            public static Kvs_reduce_rightFun instance(){return _ini_;}
            public override string ToString(){return "{(let (kvs run init ) args kvs-reduce-right this ) (if-run (exist? kvs ) {(let (k v ...kvs ) kvs ) (run (kvs-reduce-right kvs run init ) v k ) } {init } ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Node<Object> kvs = args.First() as Node<Object>;
                kvs=Kvs_reverseFun.base_run(kvs);
                args = args.Rest();
                return Kvs_reduceFun.base_run(kvs,args);
            
            }
            
        }
			            

        class Kvs_find1stFun:Function{
            private static Kvs_find1stFun _ini_=new Kvs_find1stFun();
            public static Kvs_find1stFun instance(){return _ini_;}
            public override string ToString(){return "{(let (key kvs ) args find1st this ) (let (k v ...kvs ) args ) (if-run (str-eq k key ) {v } {find1st key kvs } ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Node<Object> kvs=args.First() as Node<Object>;
                args=args.Rest();
                String key=args.First() as String;
                return Node<Object>.kvs_find1st(kvs,key);
            
            }
            
        }
			            

        class Kvs_extendFun:Function{
            private static Kvs_extendFun _ini_=new Kvs_extendFun();
            public static Kvs_extendFun instance(){return _ini_;}
            public override string ToString(){return "{(let (k v kvs ) args ) (extend k (extend v kvs ) ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                String key=args.First() as String;
                args=args.Rest();
                Object value=args.First();
                args=args.Rest();
                Node<Object> kvs=args.First() as Node<Object>;
                return Node<Object>.kvs_extend(key,value,kvs);
            
            }
            
        }
			            

        class Kvs_pathFun:Function{
            private static Kvs_pathFun _ini_=new Kvs_pathFun();
            public static Kvs_pathFun instance(){return _ini_;}
            public override string ToString(){return "{(let (e paths ) args kvs-path this ) (if-run (exist? paths ) {(let (path ...paths ) paths ) (kvs-path (kvs-find1st e path ) paths ) } {e } ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Node<Object> o=args.First() as Node<Object>;
                args=args.Rest();
                Node<Object> paths=args.First() as Node<Object>;
                return kvs_path(o,paths);
            
            }
            
                public static Object kvs_path(Node<Object> o,Node<Object> paths)
                {
                    Object value=null;
                    while(paths!=null)
                    {
                        String path=paths.First() as String;
                        value=Node<Object>.kvs_find1st(o,path);
                        paths=paths.Rest();
                        if(paths!=null)
                        {
                            o=value as Node<Object>;
                        }
                    }
                    return value;
                }
            
        }
			            

        class Kvs_path_runFun:Function{
            private static Kvs_path_runFun _ini_=new Kvs_path_runFun();
            public static Kvs_path_runFun instance(){return _ini_;}
            public override string ToString(){return "{(let (e paths ...ps ) args ) (apply (kvs-path e paths ) ps ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Node<Object> o=args.First() as Node<Object>;
                args=args.Rest();
                Node<Object> paths=args.First() as Node<Object>;
                args=args.Rest();
                Function f=Kvs_pathFun.kvs_path(o,paths) as Function;
                return f.exec(args);
            
            }
            
        }
			            

        class OffsetFun:Function{
            private static OffsetFun _ini_=new OffsetFun();
            public static OffsetFun instance(){return _ini_;}
            public override string ToString(){return "{(let (list i ) args offset this ) (if-run (= i 0 ) {list } {(offset (rest list ) (- i 1 ) ) } ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Node<Object> list=args.First() as Node<Object>;
                args=args.Rest();
                int i=(int)args.First();
                return base_run(list,i);
            
            }
            
            public static Node<Object> base_run(Node<Object> list,int i){
                while(i!=0){
                    list=list.Rest();
                    i--;
                }
                return list;
            }
            
        }
			            

        class Slice_toFun:Function{
            private static Slice_toFun _ini_=new Slice_toFun();
            public static Slice_toFun instance(){return _ini_;}
            public override string ToString(){return "{(let (xs to ) args slice-to this ) (if-run (= to 0 ) {[] } {(let (x ...xs ) xs ) (extend x (slice-to xs (- to 1 ) ) ) } ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Node<Object> list=args.First() as Node<Object>;
                args=args.Rest();
                int i=(int)args.First();

                Node<Object> r=null;
                while(i!=0){
                    r=Node<Object>.extend(list.First(),r);
                    list=list.Rest();
                    i--;
                }
                return ReverseFun.base_run(r);
            
            }
            
        }
			            

        class LenFun:Function{
            private static LenFun _ini_=new LenFun();
            public static LenFun instance(){return _ini_;}
            public override string ToString(){return "{(let (cs ) args ) (if-run (exist? cs ) {(length cs ) } {0 } ) }";}
            public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
            public override object exec(Node<object> args){
                
                Node<Object> list=args.First() as Node<Object>;
                if(list!=null){
                    return list.Length();
                }else{
                    return 0;
                }
            
            }
            
        }
			            
        public static Node<Object> library(){
            Node<Object> m = null;
            m = Node<Object>.kvs_extend("true",true, m);
            m = Node<Object>.kvs_extend("false", false, m);
            
        m=Node<Object>.kvs_extend("first",FirstFun.instance(),m);
        m=Node<Object>.kvs_extend("rest",RestFun.instance(),m);
        m=Node<Object>.kvs_extend("extend",ExtendFun.instance(),m);
        m=Node<Object>.kvs_extend("length",LengthFun.instance(),m);
        m=Node<Object>.kvs_extend("+",AddFun.instance(),m);
        m=Node<Object>.kvs_extend("-",SubFun.instance(),m);
        m=Node<Object>.kvs_extend(">",MBiggerFun.instance(),m);
        m=Node<Object>.kvs_extend("<",MSmallerFun.instance(),m);
        m=Node<Object>.kvs_extend("=",MEqFun.instance(),m);
        m=Node<Object>.kvs_extend("and",AndFun.instance(),m);
        m=Node<Object>.kvs_extend("or",OrFun.instance(),m);
        m=Node<Object>.kvs_extend("not",NotFun.instance(),m);
        m=Node<Object>.kvs_extend("empty?",IsemptyFun.instance(),m);
        m=Node<Object>.kvs_extend("exist?",IsexistFun.instance(),m);
        m=Node<Object>.kvs_extend("log",LogFun.instance(),m);
        m=Node<Object>.kvs_extend("if",IfFun.instance(),m);
        m=Node<Object>.kvs_extend("eq",EqFun.instance(),m);
        m=Node<Object>.kvs_extend("apply",ApplyFun.instance(),m);
        m=Node<Object>.kvs_extend("stringify",StringifyFun.instance(),m);
        m=Node<Object>.kvs_extend("type",TypeFun.instance(),m);
        m=Node<Object>.kvs_extend("str-eq",Str_eqFun.instance(),m);
        m=Node<Object>.kvs_extend("str-length",Str_lengthFun.instance(),m);
        m=Node<Object>.kvs_extend("str-charAt",Str_charAtFun.instance(),m);
        m=Node<Object>.kvs_extend("str-substr",Str_substrFun.instance(),m);
        m=Node<Object>.kvs_extend("str-join",Str_joinFun.instance(),m);
        m=Node<Object>.kvs_extend("str-split",Str_splitFun.instance(),m);
        m=Node<Object>.kvs_extend("str-upper",Str_upperFun.instance(),m);
        m=Node<Object>.kvs_extend("str-lower",Str_lowerFun.instance(),m);
        m=Node<Object>.kvs_extend("quote",QuoteFun.instance(),m);
        m=Node<Object>.kvs_extend("list",ListFun.instance(),m);
        m=Node<Object>.kvs_extend("type?",IstypeFun.instance(),m);
        m=Node<Object>.kvs_extend("!=",MNotEqFun.instance(),m);
        m=Node<Object>.kvs_extend("reverse",ReverseFun.instance(),m);
        m=Node<Object>.kvs_extend("kvs-reverse",Kvs_reverseFun.instance(),m);
        m=Node<Object>.kvs_extend("empty-fun",Empty_funFun.instance(),m);
        m=Node<Object>.kvs_extend("default",DefaultFun.instance(),m);
        m=Node<Object>.kvs_extend("if-run",If_runFun.instance(),m);
        m=Node<Object>.kvs_extend("reduce",ReduceFun.instance(),m);
        m=Node<Object>.kvs_extend("reduce-right",Reduce_rightFun.instance(),m);
        m=Node<Object>.kvs_extend("kvs-reduce",Kvs_reduceFun.instance(),m);
        m=Node<Object>.kvs_extend("kvs-reduce-right",Kvs_reduce_rightFun.instance(),m);
        m=Node<Object>.kvs_extend("kvs-find1st",Kvs_find1stFun.instance(),m);
        m=Node<Object>.kvs_extend("kvs-extend",Kvs_extendFun.instance(),m);
        m=Node<Object>.kvs_extend("kvs-path",Kvs_pathFun.instance(),m);
        m=Node<Object>.kvs_extend("kvs-path-run",Kvs_path_runFun.instance(),m);
        m=Node<Object>.kvs_extend("offset",OffsetFun.instance(),m);
        m=Node<Object>.kvs_extend("slice-to",Slice_toFun.instance(),m);
        m=Node<Object>.kvs_extend("len",LenFun.instance(),m);
            return m;
        }
    }
}