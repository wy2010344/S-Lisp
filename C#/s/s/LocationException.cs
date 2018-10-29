using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public class LocationException:Exception
    {
        class Stack {
            public String path;
            public Location left;
            public Location right;
            public String exp;
        }
        private Location loc;
        public LocationException(Location loc, String msg)
            : base(msg)
        {
            this.loc = loc;
        }
        public Location Loc()
        {
            return loc;
        }
        Node<Stack> stacks = null;
        public void addStack(String path,Location left,Location right,String exp)
        {
            Stack stack = new Stack();
            stack.path = path;
            stack.left = left;
            stack.right = right;
            stack.exp = exp;
            stacks = Node<Stack>.extend(stack, stacks);
        }
        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            for (Node<Stack> tmp = stacks; tmp != null; tmp = tmp.Rest())
            {
                Stack stack = tmp.First();
                sb.Append(stack.path)
                  .Append("\t")
                  .Append(stack.left.ToString()).Append("-").Append(stack.right.ToString())
                  .Append("\t")
                  .Append(stack.exp)
                  .Append("\r\n");
            }
            sb.Append(loc.ToString()).Append("\r\n");
            sb.Append(base.ToString()).Append("\r\n");
            return sb.ToString();
        }
    }
}
