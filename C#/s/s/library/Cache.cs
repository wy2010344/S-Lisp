using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class Cache : LibFunction
    {
        public override string ToString()
        {
            return "cache";
        }
        public override object exec(Node<object> args)
        {
            CacheValue cv = new CacheValue();
            cv.exec(args);
            return cv;
        }
    }

    class CacheValue : Function
    {
        public override string ToString()
        {
            return "[]";
        }
        public override FunctionType Function_type()
        {
            return FunctionType.Fun_Cache;
        }
        private Object value;
        public override object exec(Node<object> args)
        {
            if (args == null)
            {
                return value;
            }
            else
            {
                value = args.First();
                return null;
            }
        }
    }
}
