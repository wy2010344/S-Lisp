using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class ArrayList:LibFunction
    {
        public override string ToString()
        {
            return "ArrayList";
        }
        public override object exec(Node<object> args)
        {
            return new ArrayListValue(args);
        }
    }

    /// <summary>
    /// 副作用的列表
    /// 假设使用场合完全不会重复
    /// 可能有filter之类。
    /// 这样一来内置链表使用比较少。
    /// </summary>
    class ArrayListValue : Function
    {
        private readonly List<Object> list;

        public override string ToString()
        {
            return "[]";
        }
        public ArrayListValue(Node<Object> args)
        {
            list = new List<object>();
            while (args != null)
            {
                list.Add(args.First());
                args = args.Rest();
            }
        }
        public ArrayListValue(List<Object> list)
        {
            this.list = list;
        }

        public override Function.FunctionType Function_type()
        {
            return FunctionType.Fun_Cache;
        }
        public override object exec(Node<object> args)
        {
            String method = args.First() as String;
            args = args.Rest();
            if(method=="length")
            {
                return list.Count;
            }else if(method=="get")
            {
                int index=(int)args.First();
                return list[index];
            }
            else if (method == "push")
            {
                list.Add(args.First());
                return null;
            }
            else if (method == "pop")
            {
                int index=list.Count-1;
                Object o=list[index];
                list.RemoveAt(index);
                return o;
            }
            else if (method == "unshift")
            {
                list.Insert(0,args.First());
                return null;
            }
            else if (method == "shift")
            {
                Object o=list[0];
                list.RemoveAt(0);
                return o;
            }
            else if (method == "insert")
            {
                //index,row
                int index=(int)args.First();
                args=args.Rest();
                Object o=args.First();
                list.Insert(index,o);
                return null;
            }
            else if (method == "remove")
            {
                //index
                int index=(int)args.First();
                Object o=list[index];
                list.RemoveAt(index);
                return o;
            }
            else if (method == "indexOf")
            {
                //row 只找第一个
                Object o = args.First();
                return list.IndexOf(o);
            }
            else if (method == "filter")
            {
                Function fun = args.First() as Function;
                List<Object> result = new List<object>();
                for (int i = 0; i < list.Count; i++)
                {
                    if ((bool)fun.exec(Node<Object>.list(list[i], i)))
                    {
                        result.Add(list[i]);
                    }
                }
                return new ArrayListValue(result);
            }
            else if (method == "map")
            {
                Function fun = args.First() as Function;
                List<Object> result = new List<object>();
                for (int i = 0; i < list.Count; i++)
                {
                    result.Add(fun.exec(Node<Object>.list(list[i], i)));
                }
                return new ArrayListValue(result);
            }
            else if (method == "reduce")
            {
                Function fun = args.First() as Function;
                args = args.Rest();
                Object init = args == null ? null : args.First();
                for (int i = 0; i < list.Count; i++)
                {
                    init = fun.exec(Node<Object>.list(init, list[i], i));
                }
                return init;
            }
            else if (method == "forEach")
            {
                Function fun = args.First() as Function;
                for (int i = 0; i < list.Count; i++)
                {
                    fun.exec(Node<Object>.list(list[i], i));
                }
                return null;
            }
            else if (method == "sort")
            {
                List<Object> result = new List<object>();
                for (int i = 0; i < list.Count; i++)
                {
                    result.Add(list[i]);
                }
                result.Sort(new CompareV(args.First() as Function));
                return new ArrayListValue(result);
            }
            else if (method == "sort-self")
            {
                list.Sort(new CompareV(args.First() as Function));
                return null;
            }
            else if (method == "list")
            {
                Node<Object> result = null;
                for (int i = list.Count; i > 0; i--)
                {
                    result = Node<Object>.extend(list[i - 1], result);
                }
                return result;
            }
            else
            {
                throw new Exception("未找到相应方法");
            }
        }
    }
    public class CompareV : IComparer<Object>
    {
        public CompareV(Function fun)
        {
            this.fun = fun;
        }
        private readonly Function fun;
        #region IComparer<object> 成员

        int IComparer<object>.Compare(object x, object y)
        {
            return (int)fun.exec(Node<Object>.list(x, y));
        }

        #endregion
    }
}
