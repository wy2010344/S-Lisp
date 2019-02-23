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

        public override CommonReturn<TextBox> run(s.Node<object> x, s.Node<object> o)
        {
            TextBox t = new TextBox();
            return new CommonReturn<TextBox>(t, getK(o), getInits(o), getDestroys(o));
        }

        public override object value(TextBox c)
        {
            return c.Text;
        }

        public override void value(TextBox c, string value)
        {
            c.Text = value;
        }
    }
}
