using System;
using System.Collections.Generic;
using System.Text;
using s;
using s.library;
namespace shell
{
    class Program
    {
        static void Main(String[] args)
        {
            try
            {
                char line_split = '\n';
                Encoding encoding = new UTF8Encoding(false);
                S b = new S(line_split, encoding);
                b.addDef("read", new Read(line_split, encoding));
                b.addDef("write", new Write(encoding));
                //(b.run(@"C:\Users\miki\Desktop\f.s-shell") as Function).exec(null);
                if (args.Length == 0)
                {
                    b.shell();
                }
                else if (args.Length == 1)
                {
                    String first_arg = args[0];
                    if (first_arg.EndsWith("s-shell"))
                    {
                        Console.WriteLine(first_arg);
                        Object o=b.run(first_arg);
                        if (o != null && o is Function)
                        {
                            /*
                             * 脚本如果返回Function，则执行
                             */
                            (o as Function).exec(null);
                        }
                        else
                        {
                            Console.WriteLine("脚本返回不是一个函数");
                        }
                        Console.WriteLine("执行结束");
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
            Console.ReadKey();
        }
    }
}
