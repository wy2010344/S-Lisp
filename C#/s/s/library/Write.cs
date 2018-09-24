using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class Write:Function
    {
        public override Function.Function_Type Function_type()
        {
            return Function_Type.Fun_BuildIn;
        }
        public override string ToString()
        {
            return "write";
        }
        public override object exec(Node<object> args)
        {
            String path = args.First() as String;
            args = args.Rest();
            String content = args.First() as String;
            Util.writeTxt(path, content);
            return null;
        }
    }
}
