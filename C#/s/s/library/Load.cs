using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class Load:Function
    {
        public Load(Node<Object> baseScope,String base_path,char lineSpilt,Encoding encoding):base()
        {
            this.baseScope = baseScope;
            this.base_path = base_path;
            this.lineSpilt = lineSpilt;
            this.encoding = encoding;
        }
        private Encoding encoding;
        private Node<Object> baseScope;
        private String base_path;
        private char lineSpilt;
        public override string ToString()
        {
            return "load";
        }
        public override Function.FunctionType Function_type()
        {
            return FunctionType.Fun_BuildIn;
        }
        public override object exec(Node<object> args)
        {
            String r_path = args.First() as String;
            String path = Util.absolute_from_relative(base_path, r_path);
            return run_e(path, baseScope,lineSpilt,encoding);
        }
        static bool onLoad = false;
        static Node<Object> core;
        public static object run_e(String path, Node<Object> baseScope, char lineSpilt,Encoding encoding)
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
                    String sb = Util.readTxt(path, lineSpilt, encoding);
                    Node<Object> scope = Node<Object>.kvs_extend("load", new Load(baseScope, path, lineSpilt, encoding), baseScope);
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
                return Util.absolute_from_relative(basePath, r_path);
            }
        }
        public override FunctionType Function_type()
        {
            return FunctionType.Fun_BuildIn;
        }
    }
}
