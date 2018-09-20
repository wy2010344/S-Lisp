using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public class S
    {
        private char lineSplit;
        private Node<Object> scope;
        public S(char lineSplit)
        {
            this.lineSplit = lineSplit;
            scope = library.Better.build(
                            library.System.library()
                           );
        }
        public S loadLib(String relative_path, String key, bool delay)
        {
            Object value = s.library.Load.run_e(s.Util.exe_path(relative_path) , scope, lineSplit);
            if (delay)
            {
                value = (value as Function).exec(null);
            }
            scope = s.Node<Object>.kvs_extend(key, value, scope);
            return this;
        }
        public S loadLib(String relative_path,String key)
        {
            return loadLib(relative_path, key, false);
        }
        public S loadLib(String relative_path)
        {
            s.Node<Object> kvs = s.library.Load.run_e(s.Util.exe_path(relative_path), scope, lineSplit) as s.Node<Object>;
            s.Node<Object> tmp = kvs;
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
        public S addDef(String key, Object value)
        {
            scope = Node<Object>.kvs_extend(key, value, scope);
            return this;
        }
        public Object run(String relative_path)
        {
            return s.library.Load.run_e(s.Util.exe_path(relative_path), scope, lineSplit);
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
                    Node<Token> tokens = Token.run(cache, lineSplit);
                    if (tokens != null)
                    {
                        Exp exp = Exp.Parse(tokens);
                        Object r = null;
                        try
                        {
                            r = qr.exec(exp);
                        }
                        catch (Exception e)
                        {
                            Util.logException(e);
                        }
                        Console.Write("=>");
                        if (r == null)
                        {
                            Console.WriteLine("[]");
                        }
                        else
                        {
                            StringBuilder sb=new StringBuilder();
                            Node<Object>.toString(sb,r,false);
                            Console.WriteLine(sb.ToString());
                        }
                        Console.WriteLine();
                    }
                }
            }
        }
    }
}
