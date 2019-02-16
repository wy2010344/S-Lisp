using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace gui.mve
{
    public class EInput:E<TextBox>
    {
        public EInput(U u)
            : base(u)
        {
        }

        public override object build(s.Node<object> x, s.Node<object> o)
        {
            TextBox t = new TextBox();
            return build(t, getK(o), getInits(o), getDestroys(o));
        }
        public override object value(TextBox c, s.Node<object> rest)
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
