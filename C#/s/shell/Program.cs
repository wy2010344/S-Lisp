using System;
using System.Collections.Generic;
using System.Text;
using s;

namespace shell
{
    class Program
    {
        static void Main(string[] args)
        {
            s.Shell.run(s.library.System.library(),'\n');
        }
    }
}
