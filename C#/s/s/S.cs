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
            loadLibKVS(LibPath.instance().calculate("index.lisp"));
        }
        private Object loadValue(String path, bool delay, s.Node<Object> delay_args)
        {
            Object value = s.library.Load.run_e(path, scope, lineSplit,encoding);
            if (delay)
            {
                value = (value as Function).exec(delay_args);
            }
            return value;
        }
        /*将值挂载到指定节点上*/
        private S __loadLibKV__(String key,Object value)
        {
            scope = s.Node<Object>.kvs_extend(key, value, scope);
            return this;
        }
        /// <summary>
        /// 延迟kv
        /// </summary>
        /// <param name="path"></param>
        /// <param name="key"></param>
        /// <param name="delay_args"></param>
        /// <returns></returns>
        public S loadLibKV_delay(String path, String key, s.Node<Object> delay_args)
        {
            return __loadLibKV__(key, loadValue(path, true, delay_args));
        }
        /// <summary>
        /// 不延迟kv
        /// </summary>
        /// <param name="path"></param>
        /// <param name="key"></param>
        /// <returns></returns>
        public S loadLibKV(String path, String key)
        {
            return __loadLibKV__(key, loadValue(path, false, null));
        }
        /*将值作为kvs挂载*/
        private S __loadLibKVS__(Object kvs)
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
        /// <summary>
        /// 延迟kvs
        /// </summary>
        /// <param name="path"></param>
        /// <param name="delay_args"></param>
        /// <returns></returns>
        public S loadLibKVS_delay(String path, s.Node<Object> delay_args)
        {
            return __loadLibKVS__(loadValue(path, true, delay_args));
        }
        /// <summary>
        /// 不延迟kvs
        /// </summary>
        /// <param name="path"></param>
        /// <returns></returns>
        public S loadLibKVS(String path)
        {
            return __loadLibKVS__(loadValue(path, false, null));
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
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
            if (lib_path == null || lib_path == "")
            {
                lib_path = "D:/S-Lisp/";
            }
            else
            {
                lib_path = lib_path.Replace('\\', '/');
            }
            
            if (!(lib_path[lib_path.Length - 1] == '/'))
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

        public override Function.FunctionType Function_type()
        {
            return FunctionType.Fun_BuildIn;
        }
    }
}
