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
            Exp_Bool,
            Exp_Id,/*id包含kvs-path*/
            Exp_Let,
            Exp_LetId,
            Exp_LetSmall,
            Exp_LetRest
        }
        private bool isBracket;
        public bool IsBracket()
        {
            return isBracket;
        }
        private Exp_Type type;
        public Exp_Type Exp_type()
        {
            return type;
        }
        /*括号部分*/
        private Node<Exp> children;
        private Node<Exp> r_children;
        public Node<Exp> Children() { return children; }
        public Node<Exp> R_children() { return r_children; }
        private Token left;
        private Token right;
        public Token Left() { return left; }
        public Token Right() { return right; }
        private Exp(
            Exp_Type type, 
            Token left,
            Token right,
            Node<Exp> children, 
            Node<Exp> r_children
        )
        {
            this.isBracket = true;
            this.type = type;
            this.left = left;
            this.right = right;
            this.children = children;
            this.r_children = r_children;
        }

        /*基本组*/
        private Token token;
        public Token Token() { return token; }
        public String Value() { return token.Value(); }
        private int int_value;
        public int Int_Value(){ return int_value;}
        private bool bool_value;
        public bool Bool_Value() { return bool_value; }
        private Node<String> kvs_paths;
        public Node<String> KVS_paths() { return kvs_paths; }
        public Exp(Exp_Type type, Token token)
        {
            this.isBracket = false;
            this.type = type;
            this.token = token;
            if (type == Exp_Type.Exp_Int)
            {
                int_value = int.Parse(token.Value());
            }
            if (type == Exp_Type.Exp_Bool)
            {
                bool_value=(token.Value() == "true");
            }
            if (type == Exp_Type.Exp_Id)
            {
                String id = token.Value();
                if (id[0] == '.' || id[id.Length - 1] == '.')
                {
                    //throw new LocationException(loc,"不是合法的id类型，不能以.开始或结束");
                    kvs_paths = null;
                }
                else
                {
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
                        throw this.exception(id + "不是合法的id类型，不允许连续的.号");/*id类型中间有连续的..*/
                    }
                    else
                    {
                        kvs_paths = Node<String>.reverse(r);
                    }
                }
            }
        }
        public void toString(StringBuilder sb)
        {
            if (isBracket)
            {
                sb.Append(left.Value());
                for (Node<Exp> t = children; t != null; t = t.Rest())
                {
                    sb.Append(t.First().ToString()).Append(" ");
                }
                sb.Append(right.Value());
            }
            else
            {
                sb.Append(token.ToString());
            }
        }
        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            toString(sb);
            return sb.ToString();
        }
        public LocationException exception(String msg)
        {
            if (isBracket)
            {
                return new LocationException(left.Loc(), msg);
            }
            else
            {
                return new LocationException(token.Loc(), msg);
            }
        }
        public void warn(String msg)
        {
            if (isBracket)
            {
                Console.Write(left.Loc().ToString());
            }
            else
            {
                Console.Write(token.Loc().ToString());
            }
            Console.WriteLine(msg);
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
                return new Exp(Exp_Type.Exp_LetId, k.Token());
            }
            else
            {
                throw k.exception("let表达式中，" + k.ToString() + "不是合法的key-id类型");/*let-id中间不允许有任何的.*/
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
                                    new Token(v,k.Token().Old_Value(),k.Token().Token_type(),k.Token().Loc())
                                ),
                                children
                             );
                        }
                        else
                        {
                            throw k.exception("let表达式中，" + k.ToString() + "不是合法的剩余匹配ID");/*剩余匹配的id，只允许三个开头的点*/
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
                    throw k.exception("Let表达式中，不是合法的key类型" + k.ToString());/*Let表达式中，key部分出现其它类型（如数字、布尔等）不应该允许出现*/
                }
            }
            return new Exp(
                Exp_Type.Exp_LetSmall,
                small.Left(),
                small.Right(),
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
                            k.warn("Let表达式中无意义的空()，请检查:" + v.ToString());/*let表达式中，无意义的空*/
                        }
                        children=Node<Exp>.extend(resetLetSmall(k),children);
                    }
                    else
                    {
                        throw k.exception("let表达式中，不合法的key类型:" + k.ToString());/*let表达式中，不是合法的key类型，不是id或()*/
                    }
                }
                else
                {
                    throw v.exception("let表达式中期待与value:" + v.ToString() + "匹配，却结束了let表达式");/*let表达式不完全*/
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
                        v.warn("warn:函数中定义无意义的表达式，请检查:"+v.ToString());
                    }
                }
            }
        }
        public static Exp Parse(Node<Token> tokens)
        {
            Location root_loc = new Location(0, 0, 0);
            Token root_left = new Token("{", "{", s.Token.Token_Type.Token_BracketLeft, root_loc);
            Token root_right = new Token("}", "}", s.Token.Token_Type.Token_BracketRight, root_loc);
            Exp exp = new Exp(Exp_Type.Exp_Large, root_left, root_right, null, null);
            Node<Exp> caches = Node<Exp>.extend(exp, null);
            Node<Token> xs = tokens;
            Node<Exp> children = null;
            while (xs != null)
            {
                Token x = xs.First();
                xs = xs.Rest();
                if (x.Token_type() == s.Token.Token_Type.Token_BracketRight)
                {
                    Exp.Exp_Type tp;
                    if (x.Value() == ")")
                    {
                        tp = Exp_Type.Exp_Small;
                    }
                    else if (x.Value() == "]")
                    {
                        tp = Exp_Type.Exp_Medium;
                    }
                    else
                    {
                        tp = Exp_Type.Exp_Large;
                    }
                    Exp cache = new Exp(tp, null, x, children, null);
                    caches = Node<Exp>.extend(cache, caches);
                    children = null;
                }
                else if (x.Token_type() == s.Token.Token_Type.Token_BracketLeft)
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
                                    throw new LocationException(x.Loc(),"不允许空的()");/*左括号*/
                                }
                                else
                                {
                                    Exp first = children.First();
                                    if (first.Exp_type() == Exp_Type.Exp_Id && first.Value() == "let")
                                    {
                                        tp = Exp_Type.Exp_Let;
                                        if (children.Length() == 1)
                                        {
                                            throw first.exception("不允许空的let表达式");/*let标识的位置*/
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
                                            throw first.exception("函数调用第一个应该是id或{}或()，而不是" + first.ToString());/*first已经计算出来了*/
                                        }
                                    }
                                }
                            }
                        }
                    }

                    children =Node<Exp>.extend(
                        new Exp(
                            tp,
                            x,
                            cache.Right(),
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
                    if (x.Token_type() == s.Token.Token_Type.Token_String)
                    {
                        tp = Exp_Type.Exp_String;
                    }
                    else if (x.Token_type() == s.Token.Token_Type.Token_Int)
                    {
                        tp = Exp_Type.Exp_Int;
                    }
                    else if (x.Token_type() == s.Token.Token_Type.Token_Bool)
                    {
                        tp = Exp_Type.Exp_Bool;
                    }
                    else
                    {
                        Exp parent = caches.First();
                        if (parent.Exp_type() == Exp_Type.Exp_Medium)
                        {
                            if (x.Token_type() == s.Token.Token_Type.Token_Prevent)
                            {
                                if (x.Value() == "true" || x.Value() == "false" || Token.isInt(x.Value()))
                                {
                                    throw new LocationException(x.Loc(), "中括号内转义寻找作用域上的" + x.Value() + "定义");/*token相关*/
                                }
                                tp = Exp_Type.Exp_Id;
                            }
                            else if (x.Token_type() == s.Token.Token_Type.Token_Id)
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
                            if (x.Token_type() == s.Token.Token_Type.Token_Prevent)
                            {
                                tp = Exp_Type.Exp_String;
                            }
                            else if (x.Token_type() == s.Token.Token_Type.Token_Id)
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
                        children =Node<Exp>.extend(
                            new Exp(tp, x),
                            children
                        );
                    }
                }
            }
            check_Large(children);
            return new Exp(Exp_Type.Exp_Large, root_left,root_right,children, null);
        }
    }
}
