using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public class Exp
    {
        public enum Exp_Type
        {
            Exp_Large,
            Exp_Medium,
            Exp_Small,
            Exp_String,
            Exp_Int,
            Exp_Id,/*id包含kvs-path*/
            Exp_Let,
            Exp_LetId,
            Exp_LetSmall,
            Exp_LetRest
        }
        private Exp(
            Exp_Type type, 
            String value, 
            Location loc,
            Token.Token_Type original_type,
            Node<Exp> children, 
            Node<Exp> r_children,
            Node<String> kvs_paths
        )
        {
            this.type = type;
            this.value = value;
            this.loc = loc;
            this.original_type = original_type;
            this.children = children;
            this.r_children = r_children;
            this.kvs_paths = kvs_paths;
        }


        /*基本组*/
        public Exp(
            Exp_Type type,
            String value,
            Location loc,
            Token.Token_Type original_type
            )
            : this(type, value, loc, original_type, null, null, null) { }
        private Exp_Type type;
        private String value;
        private Location loc;
        public Exp_Type Exp_type() { return type; }
        public Location Loc() { return loc; }
        public String Value() { return value; }

        Token.Token_Type original_type;
        public Token.Token_Type Original_type() { return original_type; }

        /*kvs-path组*/
        public Exp(
            Exp_Type type,
            String value,
            Location loc,
            Token.Token_Type original_type,
            Node<String> kvs_paths)
            : this(type, value, loc, original_type, null, null, kvs_paths) { }

        private Node<String> kvs_paths;
        public Node<String> KVS_paths() { return kvs_paths; }

        /*children组*/
        Exp(
            Exp_Type type,
            String value,
            Location loc,
            Token.Token_Type original_type,
            Node<Exp> children,
            Node<Exp> r_children)
            : this(type, value, loc, original_type, children, r_children, null) { }
        private Node<Exp> children;
        private Node<Exp> r_children;
        public Node<Exp> Children() { return children; }
        public Node<Exp> R_children() { return r_children; }


        public void toString(StringBuilder sb)
        {
            if (this.original_type == Token.Token_Type.Token_Id)
            {
                if (this.Exp_type() == Exp_Type.Exp_LetRest)
                {
                    sb.Append("...").Append(value);
                }
                else
                {
                    sb.Append(this.value);
                }
            }
            else if (this.original_type == Token.Token_Type.Token_Int)
            {
                sb.Append(this.value);
            }
            else if (this.original_type == Token.Token_Type.Token_Prevent)
            {
                sb.Append("'").Append(this.value);
            }
            else if (this.original_type == Token.Token_Type.Token_String)
            {
                sb.Append(Util.stringToEscape(this.value, '"', '"', null));
            }
            else 
            {
                sb.Append(value[0]);
                for (Node<Exp> tmp = children; tmp != null; tmp = tmp.Rest())
                {
                    tmp.First().toString(sb);
                    sb.Append(" ");
                }
                sb.Append(value[1]);
            }
        }
        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            toString(sb);
            return sb.ToString();
        }

        /**
         *此处的id会用于定义，剩余匹配，所以不能报错
         */
        static Node<String> isKVS_path(String id, Location loc)
        {
            if (id[0] == '.' || id[id.Length - 1] == '.')
            {
                //throw new LocationException(loc,"不是合法的id类型，不能以.开始或结束");
                return null;
            }
            int i = 0;
            int last_i = 0;
            Node<String> r = null;
            bool has_error = false;
            while (i < id.Length)
            {
                char c = id[i];
                if (c == '.')
                {
                    String node = id.Substring(last_i, i - last_i);
                    last_i = i + 1;
                    if (node == "")
                    {
                        has_error = true;
                    }
                    else
                    {
                        r = Node<String>.extend(node, r);
                    }
                }
                i++;
            }
            /*最后一个，默认匹配*/
            r = Node<String>.extend(id.Substring(last_i), r);
            if (has_error)
            {
                throw new LocationException(loc, id + "不是合法的id类型，不允许连续的.号");
            }
            else
            {
                return Node<String>.reverse(r);
            }
        }

        /// <summary>
        /// 重设Let中的ID表达式。
        /// </summary>
        /// <param name="k"></param>
        /// <returns></returns>
        static Exp resetLetID(Exp k)
        {
            if (k.Value().IndexOf('.') < 0)
            {
                return new Exp(Exp_Type.Exp_LetId, k.Value(), k.Loc(), k.Original_type());
            }
            else
            {
                throw new LocationException(k.Loc(), "let表达式中，" + k.ToString() + "不是合法的key-id类型");
            }
        }
        /// <summary>
        /// let表达式的，括号匹配的部分，不过用其反转的子来计算
        /// </summary>
        /// <param name="k"></param>
        /// <returns></returns>
        static Exp resetLetSmall(Exp small)
        {
            Node<Exp> vs = small.R_children();
            Node<Exp> children=null;
            if (vs != null)
            {
                Exp k = vs.First();
                vs = vs.Rest();
                if (k.Exp_type() == Exp_Type.Exp_Id)
                {
                    String v = k.Value();
                    if (v.StartsWith("..."))
                    {
                        v = v.Substring(3);
                        if (v.IndexOf('.') < 0)
                        {
                            children = Node<Exp>.extend(
                                new Exp(
                                    Exp_Type.Exp_LetRest,
                                    v,
                                    k.Loc(),
                                    k.Original_type()
                                ),
                                children
                             );
                        }
                        else
                        {
                            throw new LocationException(k.Loc(), "let表达式中，" + k.ToString() + "不是合法的剩余匹配ID");
                        }
                    }
                    else
                    {
                        /*
                         * 最后一个只是普通的ID
                         */
                        children = Node<Exp>.extend(resetLetID(k),children);
                    }
                }
            }
            while (vs != null)
            {
                Exp k = vs.First();
                vs = vs.Rest();
                if (k.Exp_type() == Exp_Type.Exp_Small)
                {
                    children = Node<Exp>.extend(resetLetSmall(k), children);
                }
                else if (k.Exp_type() == Exp_Type.Exp_Id)
                {
                    children = Node<Exp>.extend(resetLetID(k),children);
                }
                else
                {
                    throw new LocationException(k.Loc(), "Let表达式中，不是合法的key类型" + k.ToString());
                }
            }
            return new Exp(
                Exp_Type.Exp_LetSmall,
                small.Value(),
                small.Loc(),
                small.Original_type(),
                children,
                null
            );
        }

        /// <summary>
        /// let表达式kvkv的部分，不过是反转的。
        /// </summary>
        /// <param name="vks"></param>
        /// <returns></returns>
        static Node<Exp> resetLetKV(Node<Exp> vks)
        {
            Node<Exp> children = null;
            while (vks != null)
            {
                Exp v = vks.First();
                children = Node<Exp>.extend(v,children);
                vks = vks.Rest();
                if (vks != null)
                {
                    Exp k = vks.First();
                    vks = vks.Rest();

                    if (k.Exp_type() == Exp_Type.Exp_Id)
                    {
                        children=Node<Exp>.extend(resetLetID(k),children);
                    }
                    else if (k.Exp_type() == Exp_Type.Exp_Small)
                    {
                        if (k.Children() == null)
                        {
                            Console.WriteLine("Let表达式中无意义的空()，请检查" + k.Loc().ToString() + ":" + v.ToString());
                        }
                        children=Node<Exp>.extend(resetLetSmall(k),children);
                    }
                    else
                    {
                        throw new LocationException(k.Loc(), "let表达式中，不合法的key类型:" + k.ToString());
                    }
                }
                else
                {
                    throw new LocationException(v.Loc(), "let表达式中期待与value:" + v.ToString() + "匹配，却结束了let表达式");
                }
            }
            return children;
        }
        /// <summary>
        /// 检查函数内的无用函数
        /// </summary>
        /// <param name="children"></param>
        static void check_Large(Node<Exp> vs)
        {
            while (vs != null)
            {
                Exp v = vs.First();
                vs = vs.Rest();
                if (vs != null)
                {
                    Exp_Type t = v.Exp_type();
                    if(!(t==Exp_Type.Exp_Let || t==Exp_Type.Exp_Small || t==Exp_Type.Exp_Medium))
                    {
                        Console.WriteLine("warn:函数中定义无意义的表达式，请检查"+v.Loc().ToString()+":"+v.ToString());
                    }
                }
            }
        }
        public static Exp Parse(Node<Token> tokens)
        {
            Location root_loc = new Location(0, 0, 0);
            Exp exp = new Exp(Exp_Type.Exp_Large, "{}", root_loc, Token.Token_Type.Token_BracketRight, null, null);
            Node<Exp> caches = Node<Exp>.extend(exp, null);
            Node<Token> xs = tokens;
            Node<Exp> children = null;
            while (xs != null)
            {
                Token x = xs.First();
                xs = xs.Rest();
                if (x.Token_type() == Token.Token_Type.Token_BracketRight)
                {
                    Exp.Exp_Type tp;
                    String v = "";
                    if (x.Value() == ")")
                    {
                        tp = Exp_Type.Exp_Small;
                        v = "()";
                    }
                    else if (x.Value() == "]")
                    {
                        tp = Exp_Type.Exp_Medium;
                        v = "[]";
                    }
                    else
                    {
                        tp = Exp_Type.Exp_Large;
                        v = "{}";
                    }
                    Exp cache = new Exp(tp, v, x.Loc(), x.Token_type(), children, null);
                    caches = Node<Exp>.extend(cache, caches);
                    children = null;
                }
                else if (x.Token_type() == Token.Token_Type.Token_BracketLeft)
                {
                    Exp cache = caches.First();
                    Node<Exp> r_children = null;
                    Exp_Type tp = cache.Exp_type();

                    if (tp == Exp_Type.Exp_Large)
                    {
                        check_Large(children);
                    }
                    else
                    {
                        r_children = Node<Exp>.reverse(children);
                    }
                    Node<Exp> caches_parent = caches.Rest();
                    if (caches_parent != null)
                    {
                        Exp p_exp = caches_parent.First();
                        if (p_exp.Exp_type() == Exp_Type.Exp_Large)
                        {
                            //父表达式是函数
                            if (tp == Exp_Type.Exp_Small)
                            {
                                if (children == null)
                                {
                                    throw new LocationException(cache.Loc(), "不允许空的()");
                                }
                                else
                                {
                                    Exp first = children.First();
                                    if (first.Exp_type() == Exp_Type.Exp_Id && first.Value() == "let")
                                    {
                                        tp = Exp_Type.Exp_Let;
                                        if (children.Length() == 1)
                                        {
                                            throw new LocationException(first.Loc(), "不允许空的let表达式");
                                        }
                                        else
                                        {
                                            children = Node<Exp>.extend(children.First(), resetLetKV(Node<Exp>.reverse(children.Rest())));
                                        }
                                    }
                                    else
                                    {
                                        if (!(first.Exp_type() == Exp_Type.Exp_Id || first.Exp_type() == Exp_Type.Exp_Large || first.Exp_type() == Exp_Type.Exp_Small))
                                        {
                                            throw new LocationException(first.Loc(), "函数调用第一个应该是id或{}或()，而不是" + first.ToString());
                                        }
                                    }
                                }
                            }
                        }
                    }

                    children =Node<Exp>.extend(
                        new Exp(
                            tp,
                            cache.Value(),
                            cache.Loc(),
                            x.Token_type(),
                            children,
                            r_children
                        ),
                        cache.Children()
                    );
                    caches = caches_parent;
                }
                else
                {
                    Exp_Type tp=0;
                    bool deal = true;
                    if (x.Token_type() == Token.Token_Type.Token_String)
                    {
                        tp = Exp_Type.Exp_String;
                    }
                    else if (x.Token_type() == Token.Token_Type.Token_Int)
                    {
                        tp = Exp_Type.Exp_Int;
                    }
                    else
                    {
                        Exp parent = caches.First();
                        if (parent.Exp_type() == Exp_Type.Exp_Medium)
                        {
                            if (x.Token_type() == Token.Token_Type.Token_Prevent)
                            {
                                tp = Exp_Type.Exp_Id;
                            }
                            else if (x.Token_type() == Token.Token_Type.Token_Id)
                            {
                                tp = Exp_Type.Exp_String;
                            }
                            else
                            {
                                deal = false;
                            }
                        }
                        else
                        {
                            if (x.Token_type() == Token.Token_Type.Token_Prevent)
                            {
                                tp = Exp_Type.Exp_String;
                            }
                            else if (x.Token_type() == Token.Token_Type.Token_Id)
                            {
                                tp = Exp_Type.Exp_Id;
                            }
                            else
                            {
                                deal = false;
                            }
                        }
                    }

                    if (deal)
                    {
                        Node<String> kvs_path=null;
                        if (tp == Exp_Type.Exp_Id)
                        {
                            kvs_path = isKVS_path(x.Value(), x.Loc());
                        }
                        children =Node<Exp>.extend(
                            new Exp(tp, x.Value(), x.Loc(), x.Token_type(),kvs_path),
                            children
                        );
                    }
                }
            }
            check_Large(children);
            return new Exp(Exp_Type.Exp_Large, "{}", root_loc, Token.Token_Type.Token_BracketLeft, children, null);
        }
    }
}
