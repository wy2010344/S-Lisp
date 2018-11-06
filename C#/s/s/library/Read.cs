using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class Read : LibFunction
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
        public override string ToString()
        {
            return "read";
        }
    }
}
