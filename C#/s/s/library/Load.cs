using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class Load:Function
    {
        public Load(Node<Object> baseScope,String base_path,char lineSpilt):base()
        {
            this.baseScope = baseScope;
            this.base_path = base_path;
            this.lineSpilt = lineSpilt;
        }
        private Node<Object> baseScope;
        private String base_path;
        private char lineSpilt;
        public override string ToString()
        {
            return "load";
        }
        public override Function.Function_Type Function_type()
        {
            return Function_Type.Fun_BuildIn;
        }
        public override object exec(Node<object> args)
        {
            String r_path = args.First() as String;
            String path = calAbsolutePath(base_path, r_path);
            return run_e(path, baseScope,lineSpilt);
        }

        static Node<String> StringSplit(String str, char sp)
        {
            Node<String> r = null;

            int last = 0;
            for (int i = 0; i < str.Length; i++)
            {
                if (str[i] == sp)
                {
                    r = Node<string>.extend(str.Substring(last, i - last), r);
                    last = i + 1;
                }
            }
            return Node<string>.extend(str.Substring(last,str.Length-last),r);
        }
        public static String calAbsolutePath(String base_path, String r_path)
        {
            Node<String> b = StringSplit(base_path, '/').Rest();
            Node<String> r = Node<String>.reverse(StringSplit(r_path, '/'));
            for (Node<String> t = r; t != null; t = t.Rest())
            {
                String s = t.First();
                if (s == ".")
                {
                }else if(s==""){
                }
                else if (s == "..")
                {
                    b = b.Rest();
                }
                else
                {
                    b = Node<String>.extend(s, b);
                }
            }
            b = Node<String>.reverse(b);
            StringBuilder sb = new StringBuilder();
            for (Node<String> t = b; t != null; t = t.Rest())
            {
                sb.Append(t.First());
                if (t.Rest() != null)
                {
                    sb.Append("/");
                }
            }
            return sb.ToString();
        }
        static bool onLoad = false;
        static Node<Object> core;
        public static object run_e(String path, Node<Object> baseScope, char lineSpilt)
        {
            if (onLoad)
            {
                throw new Exception("禁止在加载期间加载");
            }
            else
            {
                Node<Object> x = Node<Object>.kvs_find1st(core, path) as Node<Object>;
                if (x != null)
                {
                    return x.First();
                }
                else
                {
                    onLoad = true;
                    String sb = Util.readTxt(path, lineSpilt, Encoding.UTF8);
                    Node<Object> scope = Node<Object>.kvs_extend("load", new Load(baseScope, path, lineSpilt), baseScope);
                    scope = Node<Object>.kvs_extend("pathOf", new PathOf(path), scope);
                    Node<Token> tokens = Token.run(sb, lineSpilt);
                    Exp exp = Exp.Parse(tokens);
                    UserFunction f = new UserFunction(exp, scope);
                    Object b = f.exec(null);
                    core = Node<Object>.kvs_extend(path, Node<Object>.extend(b, null), core);
                    onLoad = false;
                    return b;
                }
            }
        }
    }
    class PathOf : Function
    {
        public PathOf(String basePath)
        {
            this.basePath = basePath;
        }
        private String basePath;
        public override string ToString()
        {
            return "pathOf";
        }
        public override object exec(Node<object> args)
        {
            if (args == null)
            {
                return basePath;
            }
            else
            {
                String r_path = args.First() as String;
                return Load.calAbsolutePath(basePath, r_path);
            }
        }
        public override Function_Type Function_type()
        {
            return Function_Type.Fun_BuildIn;
        }
    }
}
