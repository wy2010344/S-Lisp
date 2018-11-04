using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public class UserFunction:Function
    {
        private Node<Object> parentScope;
        private Exp exp;
        public UserFunction(Exp exp, Node<Object> parentScope)
        {
            this.exp = exp;
            this.parentScope = parentScope;
        }
        public override object exec(Node<object> args)
        {
            Node<Object> scope = Node<Object>.kvs_extend("args", args, parentScope);
            scope = Node<Object>.kvs_extend("this", this, scope);
            QueueRun run = new QueueRun(scope);
            return run.exec(exp);
        }
        public override FunctionType Function_type()
        {
            return FunctionType.Fun_User;
        }

        public void toString(StringBuilder sb)
        {
            exp.toString(sb);
        }
        public override string ToString()
        {
            return exp.ToString();
        }
    }
}
