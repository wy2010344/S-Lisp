
using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class Better
    {
    

            class Kvs_extendFunc:Function{
                private static Kvs_extendFunc _ini_=new Kvs_extendFunc();
                public static Kvs_extendFunc instance(){return _ini_;}
                public override string ToString(){return "kvs-extend";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                String key=args.First() as String;
                args=args.Rest();
                Object value=args.First();
                args=args.Rest();
                Node<Object> kvs=args.First() as Node<Object>;
                return Node<Object>.kvs_extend(key,value,kvs);
            
                }
            }
            

            class Kvs_find1stFunc:Function{
                private static Kvs_find1stFunc _ini_=new Kvs_find1stFunc();
                public static Kvs_find1stFunc instance(){return _ini_;}
                public override string ToString(){return "kvs-find1st";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_BuildIn;}
                public override object exec(Node<object> args){
                    
                Node<Object> kvs=args.First() as Node<Object>;
                args=args.Rest();
                String key=args.First() as String;
                return Node<Object>.kvs_find1st(kvs,key);
            
                }
            }
            

            class ListFunc:Function{
                private static ListFunc _ini_=new ListFunc();
                public static ListFunc instance(){return _ini_;}
                public override string ToString(){return "{ args }";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
                public override object exec(Node<object> args){
                    
	            return args;
	        
                }
            }
            

            class QuoteFunc:Function{
                private static QuoteFunc _ini_=new QuoteFunc();
                public static QuoteFunc instance(){return _ini_;}
                public override string ToString(){return "{ ( first args ) }";}
                public override Function_Type Function_type(){return Function.Function_Type.Fun_Better;}
                public override object exec(Node<object> args){
                    
	            return args.First();
	        
                }
            }
            
        public static Node<Object> build(Node<Object> m){
            
            m=Node<Object>.kvs_extend("kvs-extend",Kvs_extendFunc.instance(),m);
            m=Node<Object>.kvs_extend("kvs-find1st",Kvs_find1stFunc.instance(),m);
            m=Node<Object>.kvs_extend("list",ListFunc.instance(),m);
            m=Node<Object>.kvs_extend("quote",QuoteFunc.instance(),m);
            return m;
        }
    }
}