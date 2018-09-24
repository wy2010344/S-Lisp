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
                b.addDef("write", new Write());
                /*
                (b.run(@"D:\usr\web\app\S-Lisp\C#\s\s\library\index.s-shell") as Function).exec(null);
                 */
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
                        (b.run(first_arg) as Function).exec(null);
                        Console.WriteLine("Ö´ÐÐ½áÊø");
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
