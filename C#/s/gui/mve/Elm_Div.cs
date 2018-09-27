using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace gui.mve
{
    public class Elm_Div : Elm_Control
    {
        public Elm_Div()
        {
            this.div = new Panel();
            set_real_control(div);
        }
        private Panel div;
    }
}
