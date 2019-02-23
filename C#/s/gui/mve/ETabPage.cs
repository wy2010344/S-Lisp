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

        public override CommonReturn<TabPage> run(s.Node<object> x, s.Node<object> o)
        {
            TabPage tp = new TabPage();
            s.Node<Object> obj = u.exec_buildChild_Control(tp, x, o);
            return new CommonReturn<TabPage>(tp, getK(obj), getInits(obj), getDestroys(obj));
        }

        public override object text(TabPage c)
        {
            return c.Text;
        }

        public override void text(TabPage c, string value)
        {
            c.Text = value;
        }
    }
}
