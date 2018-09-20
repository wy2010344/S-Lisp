using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public abstract class Function
    {
        public enum Function_Type
        {
            Fun_User,
            Fun_BuildIn,
            Fun_Better,
            Fun_Cache
        }
        public abstract Object exec(Node<Object> args);
        public abstract Function_Type Function_type();
    }
}
