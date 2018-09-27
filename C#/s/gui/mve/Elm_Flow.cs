using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace gui.mve
{
    public class Elm_Flow :Elm_Div
    {
        public Elm_Flow()
        {
            this.flow = new FlowLayoutPanel();
            set_real_control(flow);
        }
        private FlowLayoutPanel flow;
        public override object attr(string key, s.Node<object> args)
        {
            if (key == "FlowDirection")
            {
                if (args == null)
                {
                    return flow.FlowDirection;
                }
                else
                {
                    String d = args.First() as String;
                    if (d == "LeftToRight")
                    {
                        flow.FlowDirection = FlowDirection.LeftToRight;
                    }
                    else if (d == "RightToLeft")
                    {
                        flow.FlowDirection = FlowDirection.RightToLeft;
                    }
                    else if (d == "TopDown")
                    {
                        flow.FlowDirection = FlowDirection.TopDown;
                    }
                    else if (d == "BottomUp")
                    {
                        flow.AutoScroll = true;
                        flow.FlowDirection = FlowDirection.BottomUp;
                    }
                }
            }
            else
            {
                return base.attr(key,args);
            }
            return null;
        }
    }
}
