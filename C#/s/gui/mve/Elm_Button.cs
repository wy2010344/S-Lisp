using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace gui.mve
{
    public class Elm_Button : Elm_Control
    {
        public Elm_Button()
        {
            this.button = new Button();
            set_real_control(button);
        }
        private Button button;

        public override object text(s.Node<object> args)
        {
            if (args == null)
            {
                return button.Text;
            }
            else
            {
                button.Text = args.First() as String;
                return null;
            }
        }
        public override void action(string key, s.Function fun)
        {
            if (key == "click")
            {
                button.Click += new EventHandler(new SEventHandle(fun).run);
            }
        }
    }
}
