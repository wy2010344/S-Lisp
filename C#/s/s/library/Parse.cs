using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class Parse:Function
    {
        public Parse(Node<Object> defaultScope,char lineSplit)
            : base()
        {
            this.defaultScope = defaultScope;
            this.lineSplit = lineSplit;
        }
        private char lineSplit;
        private Node<Object> defaultScope;
        public override string ToString()
        {
            return "parse";
        }
        public override Function.Function_Type Function_type()
        {
            return Function_Type.Fun_BuildIn;
        }
        public override object exec(Node<object> args)
        {
            String str = args.First() as String;
            args = args.Rest();
            Node<Object> scope = defaultScope;
            if (args != null)
            {
                scope = args.First() as Node<Object>;
            }
            Node<Token> tokens = Token.run(str, lineSplit);
            Exp exp = Exp.Parse(tokens);
            UserFunction f = new UserFunction(exp, scope);
            return f.exec(null);
        }
    }
}
