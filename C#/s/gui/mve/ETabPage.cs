using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace gui.mve
{
    class ETabPage:EC<TabPage>
    {
        public ETabPage(U u)
            : base(u)
        {
        }

        public override object build(s.Node<object> x, s.Node<object> o)
        {
            TabPage tp = new TabPage();
            s.Node<Object> obj = u.exec_buildChild_Control(tp, x, o);
            return build(tp, getK(obj), getInits(obj), getDestroys(obj));
        }

        public override object text(TabPage c, s.Node<object> rest)
        {
            if (rest == null)
            {
                return c.Text;
            }
            else
            {
                c.Text = rest.First() as String;
            }
            return null;
        }
    }
}
