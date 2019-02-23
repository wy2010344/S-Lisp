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
        public override CommonReturn<Panel> run(s.Node<object> x, s.Node<object> o)
        {
            Panel p = new Panel();
            s.Node<Object> obj = u.exec_buildChild_Control(p, x, o);
            return new CommonReturn<Panel>(p, getK(obj), getInits(obj), getDestroys(obj));
        }
    }
}
