using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace gui.mve
{
    class EButton:EC<Button>
    {
        public EButton(U u) : base(u) {
        }
        public override CommonReturn<Button> run(s.Node<object> x, s.Node<object> o)
        {
            Button button = new Button();
            s.Node<Object> obj=u.exec_buildChild_Control(button, x, o);
            return new CommonReturn<Button>(button,getK(obj),getInits(obj),getDestroys(obj));
        }
        public override void action_gs(Button c, string key, s.Function fun)
        {
            if (key == "click")
            {
                c.Click += new EventHandler(new SEventHandle(fun).run);
            }
        }

        public override Object text(Button c)
        {
            return c.Text;
        }
        public override void text(Button c, string value)
        {
            c.Text = value;
        }
    }
}
