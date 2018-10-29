using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public class S
    {
        private char lineSplit;
        private Encoding encoding;
        private Node<Object> scope;
        public S(char lineSplit,Encoding encoding)
        {
            this.encoding = encoding;
            this.lineSplit = lineSplit;
            scope = library.System.library();
            scope = Node<Object>.kvs_extend("lib-path", LibPath.instance(), scope);
            loadLib(LibPath.instance().calculate("index.lisp"));
        }
        private Object loadValue(String relative_path,bool delay,s.Node<Object> delay_args)
        {
            Object value = s.library.Load.run_e(s.Util.exe_path(relative_path), scope, lineSplit,encoding);
            if (delay)
            {
                value = (value as Function).exec(delay_args);
            }
            return value;
        }
        /*将值挂载到指定节点上*/
        private S loadLibKey(String key,Object value)
        {
            scope = s.Node<Object>.kvs_extend(key, value, scope);
            return this;
        }
        public S loadLib(String relative_path, String key, s.Node<Object> delay_args)
        {
            return loadLibKey(key,loadValue(relative_path, true,delay_args));
        }
        public S loadLib(String relative_path,String key)
        {
            return loadLibKey(key,loadValue(relative_path, false, null));
        }

        /*将值作为kvs挂载*/
        private S loadLibKVS(Object kvs)
        {
            s.Node<Object> tmp = kvs as s.Node<Object>;
            while (tmp != null)
            {
                String key = tmp.First() as String;
                tmp = tmp.Rest();
                Object value = tmp.First();
                tmp = tmp.Rest();
                scope = s.Node<Object>.kvs_extend(key, value, scope);
            }
            return this;
        }
        public S loadLib(String relative_path, s.Node<Object> delay_args)
        {
            return loadLibKVS(loadValue(relative_path, true,delay_args));
        }
        public S loadLib(String relative_path)
        {
            return loadLibKVS(loadValue(relative_path, false, null));
        }
        public S addDef(String key, Object value)
        {
            scope = Node<Object>.kvs_extend(key, value, scope);
            return this;
        }
        public Object run(String path)
        {
            return s.library.Load.run_e(path, scope, lineSplit, encoding);
        }

        public void shell()
        {
            String cache = "";
            bool come = true;
            QueueRun qr = new QueueRun(scope);
            while (come)
            {
                String tmp = "";
                Console.Write("<=");
                tmp=Console.ReadLine();
                if (tmp == "``")
                {
                    bool will = true;
                    while (will)
                    {
                        tmp = Console.ReadLine();
                        if (tmp == "``")
                        {
                            will = false;
                        }
                        else
                        {
                            cache = cache + tmp + lineSplit;
                        }
                    }
                }
                else
                {
                    cache = tmp;
                }
                if (cache == "exit")
                {
                    come = false;
                }
                else
                {
                    try
                    {
                        Node<Token> tokens = Token.run(cache, lineSplit);
                        cache = "";
                        if (tokens != null)
                        {
                            Exp exp = Exp.Parse(tokens);
                            Object r = null;
                            r = qr.exec(exp);
                            Console.Write("=>");
                            Console.WriteLine(s.library.System.toString(r,true));
                            Console.WriteLine();
                        }
                    }
                    catch (Exception e)
                    {
                        Util.logException(e);
                    }
                }
            }
        }
    }
    public class LibPath : Function
    {
        private LibPath()
        {
            try
            {
                lib_path = Environment.GetEnvironmentVariable("S_LISP");
                lib_path = lib_path.Replace('\\', '/');
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
            if (lib_path==null || lib_path == "")
            {
                lib_path = "D:/S-Lisp/";
            }
            else if (!(lib_path[lib_path.Length - 1] == '/'))
            {
                lib_path = lib_path + "/";
            }
        }
        private static LibPath ini=new LibPath();
        public static LibPath instance() { return ini; }
        public String calculate(string path)
        {
            if (!(path[0] == '.'))
            {
                path = "./" + path;
            }
            return Util.absolute_from_relative(lib_path, path);
        }
        private string lib_path="";
        public override object exec(Node<object> args)
        {
            String path=args.First() as String;
            return calculate(path);
        }
        public override string ToString()
        {
            return "lib-path";
        }

        public override Function.Function_Type Function_type()
        {
            return Function_Type.Fun_BuildIn;
        }
    }
}
