using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace gui.mve
{
    public class Elm_Control : Elm
    {
        public override object attr(string key, s.Node<object> args)
        {
            Control c = Real_Control() as Control;
            if (key == "Dock")
            {
                if (args == null)
                {
                    return c.Dock;
                }
                else
                {
                    String v = args.First() as String;
                    if (v == "Bottom")
                    {
                        c.Dock = DockStyle.Bottom;
                    }
                    else if (v == "Top")
                    {
                        c.Dock = DockStyle.Top;
                    }
                    else if (v == "Left")
                    {
                        c.Dock = DockStyle.Left;
                    }
                    else if (v == "Right")
                    {
                        c.Dock = DockStyle.Right;
                    }
                    else if (v == "Fill")
                    {
                        c.Dock = DockStyle.Fill;
                    }
                    else if (v == "None")
                    {
                        c.Dock = DockStyle.None;
                    }
                }
            }
            else
            {
                return base.attr(key, args);
            }
            return null;

        }
        public override void appendChild(Elm el)
        {
            (Real_Control() as Control).Controls.Add(el.Real_Control() as Control);
        }
        public override void removeChild(Elm el)
        {
            (Real_Control() as Control).Controls.Remove(el.Real_Control() as Control);
        }
        public override void replaceWith(Elm el)
        {
            Control old_el = Real_Control() as Control;
            Control new_el = el.Real_Control() as Control;
            int old_index = old_el.Parent.Controls.IndexOf(old_el);
            old_el.Parent.Controls.Add(new_el);
            old_el.Parent.Controls.SetChildIndex(new_el, old_index);
            old_el.Parent.Controls.Remove(new_el);
        }
    }
}
