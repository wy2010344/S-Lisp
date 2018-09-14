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
            Exp_Id
        }
        private Exp_Type type;
        private String value;
        private Location loc;
        public Exp(
            Exp_Type type, 
            String value,
            Location loc,
            Token.Token_Type original_type
        )
        {
            this.type = type;
            this.value = value;
            this.loc = loc;
            this.original_type = original_type;
            this.children = null;
            this.r_children = null;
        }
        public Exp(
            Exp_Type type, 
            String value, 
            Location loc,
            Token.Token_Type original_type,
            Node<Exp> children, 
            Node<Exp> r_children
        )
        {
            this.type = type;
            this.value = value;
            this.loc = loc;
            this.original_type = original_type;
            this.children = children;
            this.r_children = r_children;
        }
        public Exp_Type Exp_type() { return type; }
        public Location Loc() { return loc; }
        public String Value() { return value; }
        Token.Token_Type original_type;
        public Token.Token_Type Original_type() { return original_type; }

        private Node<Exp> children;
        private Node<Exp> r_children;
        public Node<Exp> Children() { return children; }
        public Node<Exp> R_children() { return r_children; }


        public void toString(StringBuilder sb)
        {

            if (this.original_type == Token.Token_Type.Token_Id)
            {
                sb.Append(this.value);
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
                    if (cache.Exp_type() != Exp_Type.Exp_Large)
                    {
                        r_children = Node<Exp>.reverse(children);
                    }

                    children =Node<Exp>.extend(
                        new Exp(
                            cache.Exp_type(),
                            cache.Value(),
                            cache.Loc(),
                            x.Token_type(),
                            children,
                            r_children
                        ),
                        cache.Children()
                    );
                    caches = caches.Rest();
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
                        children =Node<Exp>.extend(
                            new Exp(tp, x.Value(), x.Loc(), x.Token_type()),
                            children
                        );
                    }
                }
            } 
            return new Exp(Exp_Type.Exp_Large, "{}", root_loc, Token.Token_Type.Token_BracketLeft, children, null);
        }
    }
}
