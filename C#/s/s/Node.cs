using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public class Node<T>
    {
        private T first;
        private Node<T> rest;
        private int length;
        private Node(T first, Node<T> rest)
        {
            this.first = first;
            this.rest = rest;
            if (rest == null)
            {
                length = 1;
            }
            else
            {
                length = rest.Length() + 1;
            }
        }
        public T First() { return first; }
        public Node<T> Rest() { return rest; }
        public int Length() { return length; }


        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            toString(sb);
            return sb.ToString();
        }
        protected void toString(StringBuilder sb)
        {
            sb.Append("[");
            toString(sb, first);
            for (Node<T> tmp = rest; tmp != null; tmp = tmp.Rest())
            {
                sb.Append(" ");
                toString(sb, tmp.First());
            }
            sb.Append("]");
        }
        /// <summary>
        /// 专供调用
        /// </summary>
        /// <param name="sb"></param>
        /// <param name="v"></param>
        private static void toString(StringBuilder sb, Object v)
        {
            if (v == null)
            {
                sb.Append("[]");
            }
            else if (v is Node<Object>)
            {
                (v as Node<Object>).toString(sb);
            }
            else if (v is String)
            {
                sb.Append(Util.stringToEscape(v as String, '"', '"', null));
            }
            else if (v is Function)
            {
                Function f = v as Function;
                if (f.Function_type() == Function.Function_Type.Fun_BuildIn)
                {
                    sb.Append("'");
                    sb.Append(v.ToString());
                }
                else if (f.Function_type() == Function.Function_Type.Fun_Better)
                {
                    sb.Append(v.ToString());
                }
                else if (f.Function_type() == Function.Function_Type.Fun_User)
                {
                    (f as UserFunction).toString(sb);
                }
                else if (f.Function_type() == Function.Function_Type.Fun_Cache)
                {
                    sb.Append("[]");
                }else
                {
                    sb.Append(v.ToString());
                }
            }
            else if (v is bool)
            {
                if ((bool)v)
                {
                    sb.Append("true");
                }
                else
                {
                    sb.Append("false");
                }
            }
            else if (v is int)
            {
                sb.Append(v.ToString());
            }
            else
            {
                String vx = v.ToString();
                if (vx == null)
                {
                    sb.Append("[]");
                }
                else
                {
                    sb.Append("'").Append(v.ToString());
                }
            }
        }

        public static Node<T> reverse(Node<T> list)
        {
            Node<T> r = null;
            while (list != null)
            {
                r = new Node<T>(list.First(), r);
                list = list.Rest();
            }
            return r;
        }

        /**************************一些库函数********************************/

        public static Node<T> extend(T value, Node<T> scope)
        {
            return new Node<T>(value, scope);
        }
        public static Node<T> list(params T[] ps)
        {
            return extends(ps, null);
        }
        public static Node<T> extends(T[] ps, Node<T> r)
        {
            for (int i = ps.Length-1; i >-1; i--)
            {
                r = new Node<T>(ps[i], r);
            }
            return r;
        }
        public static Node<Object> kvs_extend(String key, Object value, Node<Object> scope)
        {
            return new Node<Object>(key, new Node<Object>(value, scope));
        }
        public static Object kvs_find1st(Node<Object> kvs, String k)
        {
            if (kvs == null)
            {
                return null;
            }
            else
            {
                String key = (String)kvs.First();
                kvs = kvs.Rest();
                if (key == k)
                {
                    return kvs.First();
                }
                else
                {
                    return kvs_find1st(kvs.Rest(), k);
                }
            }
        }
    }
}
