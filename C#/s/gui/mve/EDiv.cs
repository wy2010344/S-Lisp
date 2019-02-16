using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
namespace gui.mve
{
    public class EDiv:EC<Panel>
    {
        public EDiv(U u)
            : base(u)
        {
        }
        public override object build(s.Node<object> x, s.Node<object> o)
        {
            Panel p = new Panel();
            s.Node<Object> obj = u.exec_buildChild_Control(p, x, o);
            return build(p, getK(obj), getInits(obj), getDestroys(obj));
        }
        public override object action(Panel c, string key, s.Node<object> rest)
        {
            throw new Exception("The method or operation is not implemented.");
        }
    }
}
