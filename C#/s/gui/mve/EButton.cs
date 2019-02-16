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
        public override object build(s.Node<object> x, s.Node<object> o)
        {
            Button button = new Button();
            s.Node<Object> obj=u.exec_buildChild_Control(button, x, o);
            return build(button,getK(obj),getInits(obj),getDestroys(obj));
        }
        public override object action(Button c, string key, s.Node<object> rest)
        {
            if (key == "click")
            {
                s.Function click = rest.First() as s.Function;
                c.Click += new EventHandler(new SEventHandle(click).run);
            }
            return null;
        }

        public override object text(Button c,s.Node<object> rest)
        {
            if (rest == null)
            {
                return c.Text;
            }
            else
            {
                c.Text = rest.First() as String;
                return null;
            }
        }
    }
}
