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
            sb.Append("[");
            toString(sb);
            sb.Append("]");
            return sb.ToString();
        }
        public void toString(StringBuilder sb)
        {
            toString(sb, first,true);
            for (Node<T> tmp = rest; tmp != null; tmp = tmp.Rest())
            {
                sb.Append(" ");
                toString(sb, tmp.First(),true);
            }
        }
        public static void toString(StringBuilder sb, Object v,bool trans)
        {
            if (v == null)
            {
                sb.Append("[]");
            }
            else if (v is String)
            {
                sb.Append(Util.stringToEscape((String)v, '"', '"', null));
            }
            else if (v is Function)
            {
                Function f = v as Function;
                if (f.Function_type() == Function.Function_Type.Fun_BuildIn)
                {
                    if (trans)
                    {
                        sb.Append("'");
                    }
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
                else
                {
                    sb.Append(v.ToString());
                }
            }
            else if (v is bool)
            {
                if (trans)
                {
                    sb.Append("'");
                }
                if ((bool)v)
                {
                    sb.Append("true");
                }
                else
                {
                    sb.Append("false");
                }
            }
            else
            {
                sb.Append(v.ToString());
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

        public static Node<Object> kvs_extend(String key, Object value, Node<Object> scope)
        {
            return new Node<object>(key, new Node<object>(value, scope));
        }

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
