using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace gui.mve
{
    public class Elm_Input : Elm
    {
        public Elm_Input()
        {
            this.input = new TextBox();
            input.ImeMode = ImeMode.HangulFull;
            set_real_control(input);
        }
        private TextBox input;

        public override object value(s.Node<object> args)
        {
            if (args == null)
            {
                return input.Text;
            }
            else
            {
                input.Text = args.First() as String;
                return null;
            }
        }
    }
}
