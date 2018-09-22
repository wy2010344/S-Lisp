
using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class System
    {
    

            class Str_joinFunc:Function{
                private static Str_joinFunc _ini_=new Str_joinFunc();
                public static Str_joinFunc instance(){return _ini_;}
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
            

            class Str_substrFunc:Function{
                private static Str_substrFunc _ini_=new Str_substrFunc();
                public static Str_substrFunc instance(){return _ini_;}
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
            

            class Str_charAtFunc:Function{
                private static Str_charAtFunc _ini_=new Str_charAtFunc();
                public static Str_charAtFunc instance(){return _ini_;}
                public override string ToString(){return "str-charAt";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                String a=args.First() as String;
                args=args.Rest();
                int b=(int)args.First();
                return ""+a[b];
            
                }
            }
            

            class Str_lengthFunc:Function{
                private static Str_lengthFunc _ini_=new Str_lengthFunc();
                public static Str_lengthFunc instance(){return _ini_;}
                public override string ToString(){return "str-length";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                String a=args.First() as String;
                return a.Length;
            
                }
            }
            

            class Str_eqFunc:Function{
                private static Str_eqFunc _ini_=new Str_eqFunc();
                public static Str_eqFunc instance(){return _ini_;}
                public override string ToString(){return "str-eq";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                String a=args.First() as String;
                args=args.Rest();
                String b=args.First() as String;
                return a==b;
            
                }
            }
            

            class TypeFunc:Function{
                private static TypeFunc _ini_=new TypeFunc();
                public static TypeFunc instance(){return _ini_;}
                public override string ToString(){return "type";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                Object b=args.First();
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
            

            class StringifyFunc:Function{
                private static StringifyFunc _ini_=new StringifyFunc();
                public static StringifyFunc instance(){return _ini_;}
                public override string ToString(){return "stringify";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                StringBuilder sb=new StringBuilder();
                Node<Object>.toString(sb, args.First(), false);
                return sb.ToString();
            
                }
            }
            

            class ApplyFunc:Function{
                private static ApplyFunc _ini_=new ApplyFunc();
                public static ApplyFunc instance(){return _ini_;}
                public override string ToString(){return "apply";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                Function f=args.First() as Function;
                args=args.Rest();
                return f.exec(args.First() as Node<Object>);
            
                }
            }
            

            class EqFunc:Function{
                private static EqFunc _ini_=new EqFunc();
                public static EqFunc instance(){return _ini_;}
                public override string ToString(){return "eq";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                Object a=args.First();
                args=args.Rest();
                Object b=args.First();
                return a==b;
            
                }
            }
            

            class IfFunc:Function{
                private static IfFunc _ini_=new IfFunc();
                public static IfFunc instance(){return _ini_;}
                public override string ToString(){return "if";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
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
            

            class LogFunc:Function{
                private static LogFunc _ini_=new LogFunc();
                public static LogFunc instance(){return _ini_;}
                public override string ToString(){return "log";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                StringBuilder sb = new StringBuilder();
                args.toString(sb);
                Console.WriteLine(sb.ToString());
                return null;
            
                }
            }
            

            class IsexistFunc:Function{
                private static IsexistFunc _ini_=new IsexistFunc();
                public static IsexistFunc instance(){return _ini_;}
                public override string ToString(){return "exist?";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                return args.First()!=null;
            
                }
            }
            

            class IsemptyFunc:Function{
                private static IsemptyFunc _ini_=new IsemptyFunc();
                public static IsemptyFunc instance(){return _ini_;}
                public override string ToString(){return "empty?";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                return args.First()==null;
            
                }
            }
            

            class NotFunc:Function{
                private static NotFunc _ini_=new NotFunc();
                public static NotFunc instance(){return _ini_;}
                public override string ToString(){return "not";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                return !(bool)args.First();
            
                }
            }
            

            class OrFunc:Function{
                private static OrFunc _ini_=new OrFunc();
                public static OrFunc instance(){return _ini_;}
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
            

            class AndFunc:Function{
                private static AndFunc _ini_=new AndFunc();
                public static AndFunc instance(){return _ini_;}
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
            

            class MEqFunc:Function{
                private static MEqFunc _ini_=new MEqFunc();
                public static MEqFunc instance(){return _ini_;}
                public override string ToString(){return "=";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
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
            

            class MSmaiierFun:Function{
                private static MSmaiierFun _ini_=new MSmaiierFun();
                public static MSmaiierFun instance(){return _ini_;}
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
            

            class MBiggerFunc:Function{
                private static MBiggerFunc _ini_=new MBiggerFunc();
                public static MBiggerFunc instance(){return _ini_;}
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
            

            class LengthFunc:Function{
                private static LengthFunc _ini_=new LengthFunc();
                public static LengthFunc instance(){return _ini_;}
                public override string ToString(){return "length";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                return (args.First() as Node<Object>).Length();
            
                }
            }
            

            class ExtendFunc:Function{
                private static ExtendFunc _ini_=new ExtendFunc();
                public static ExtendFunc instance(){return _ini_;}
                public override string ToString(){return "extend";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                return Node<Object>.extend(args.First(),(args.Rest().First() as Node<Object>));
            
                }
            }
            

            class RestFunc:Function{
                private static RestFunc _ini_=new RestFunc();
                public static RestFunc instance(){return _ini_;}
                public override string ToString(){return "rest";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                return (args.First() as Node<Object>).Rest();
            
                }
            }
            

            class FirstFunc:Function{
                private static FirstFunc _ini_=new FirstFunc();
                public static FirstFunc instance(){return _ini_;}
                public override string ToString(){return "first";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                return (args.First() as Node<Object>).First();
            
                }
            }
            
        public static Node<Object> library(){
            Node<Object> m = null;
            m = Node<Object>.kvs_extend("true",true, m);
            m = Node<Object>.kvs_extend("false", false, m);
            
            m=Node<Object>.kvs_extend("str-join",Str_joinFunc.instance(),m);
            m=Node<Object>.kvs_extend("str-substr",Str_substrFunc.instance(),m);
            m=Node<Object>.kvs_extend("str-charAt",Str_charAtFunc.instance(),m);
            m=Node<Object>.kvs_extend("str-length",Str_lengthFunc.instance(),m);
            m=Node<Object>.kvs_extend("str-eq",Str_eqFunc.instance(),m);
            m=Node<Object>.kvs_extend("type",TypeFunc.instance(),m);
            m=Node<Object>.kvs_extend("stringify",StringifyFunc.instance(),m);
            m=Node<Object>.kvs_extend("apply",ApplyFunc.instance(),m);
            m=Node<Object>.kvs_extend("eq",EqFunc.instance(),m);
            m=Node<Object>.kvs_extend("if",IfFunc.instance(),m);
            m=Node<Object>.kvs_extend("log",LogFunc.instance(),m);
            m=Node<Object>.kvs_extend("exist?",IsexistFunc.instance(),m);
            m=Node<Object>.kvs_extend("empty?",IsemptyFunc.instance(),m);
            m=Node<Object>.kvs_extend("not",NotFunc.instance(),m);
            m=Node<Object>.kvs_extend("or",OrFunc.instance(),m);
            m=Node<Object>.kvs_extend("and",AndFunc.instance(),m);
            m=Node<Object>.kvs_extend("=",MEqFunc.instance(),m);
            m=Node<Object>.kvs_extend("<",MSmaiierFun.instance(),m);
            m=Node<Object>.kvs_extend(">",MBiggerFunc.instance(),m);
            m=Node<Object>.kvs_extend("-",SubFun.instance(),m);
            m=Node<Object>.kvs_extend("+",AddFun.instance(),m);
            m=Node<Object>.kvs_extend("length",LengthFunc.instance(),m);
            m=Node<Object>.kvs_extend("extend",ExtendFunc.instance(),m);
            m=Node<Object>.kvs_extend("rest",RestFunc.instance(),m);
            m=Node<Object>.kvs_extend("first",FirstFunc.instance(),m);
            return m;
        }
    }
}