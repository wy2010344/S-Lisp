using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public class Shell
    {
        public static void run(Node<Object> baseScope,char lineSplits)
        {
            String cache = "";
            bool come = true;
            QueueRun qr = new QueueRun(baseScope);
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
                            cache = cache + tmp + lineSplits;
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
                    Node<Token> tokens = Token.run(cache, lineSplits);
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
