using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public abstract class Function
    {
        public enum FunctionType
        {
            Fun_User,
            Fun_BuildIn,
            Fun_Better,
            Fun_Cache
        }
        public abstract Object exec(Node<Object> args);
        public abstract FunctionType Function_type();
    }
}
