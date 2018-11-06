using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class Write : LibFunction
    {
        public Write(Encoding encoding)
        {
            this.encoding = encoding;
        }
        private Encoding encoding;
        public override string ToString()
        {
            return "write";
        }
        public override object exec(Node<object> args)
        {
            String path = args.First() as String;
            args = args.Rest();
            String content = args.First() as String;
            Util.writeTxt(path, content,encoding);
            return null;
        }
    }
}
