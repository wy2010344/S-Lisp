using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class Read:Function
    {
        private char lineSplit;
        private Encoding encoding;
        public Read(char lineSplit,Encoding encoding)
        {
            this.lineSplit = lineSplit;
            this.encoding = encoding;
        }
        public override object exec(Node<object> args)
        {
            return Util.readTxt(args.First() as String, lineSplit, encoding);
        }
        public override Function_Type Function_type()
        {
            return Function_Type.Fun_BuildIn;
        }
        public override string ToString()
        {
            return "read";
        }
    }
}
