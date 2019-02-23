using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;


namespace gui.mve
{
    public class EFlow:EC<FlowLayoutPanel>
    {
        public EFlow(U u)
            : base(u)
        {
        }
        public override CommonReturn<FlowLayoutPanel> run(s.Node<object> x, s.Node<object> o)
        {
            FlowLayoutPanel p = new FlowLayoutPanel();
            s.Node<Object> obj = u.exec_buildChild_Control(p, x, o);
            return new CommonReturn<FlowLayoutPanel>(p, getK(obj), getInits(obj), getDestroys(obj));
        }

        public override Object attr_gs(FlowLayoutPanel c, string key, Object value)
        {
            if (key == "FlowDirection")
            {
                if (value == null)
                {
                    return c.FlowDirection;
                }
                else
                {
                    String d = value as String;
                    if (d == "LeftToRight")
                    {
                        c.FlowDirection = FlowDirection.LeftToRight;
                    }
                    else if (d == "RightToLeft")
                    {
                        c.FlowDirection = FlowDirection.RightToLeft;
                    }
                    else if (d == "TopDown")
                    {
                        c.FlowDirection = FlowDirection.TopDown;
                    }
                    else if (d == "BottomUp")
                    {
                        c.AutoScroll = true;
                        c.FlowDirection = FlowDirection.BottomUp;
                    }
                }
                return null;
            }
            else
            {
                return base.attr_gs(c, key, value) ;
            }
        }
    }
}
