using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace gui
{
    public partial class MainForm : Form
    {
        private s.Function destroy;
        public MainForm()
        {
            InitializeComponent();
            SetStyle(ControlStyles.SupportsTransparentBackColor, true);
            s.S slib = new s.S('\n');
            slib.loadLib("./s/lib/index.lisp");
            slib.addDef("cache",new s.library.Cache());
            //slib.addDef("build-element", new BuildElement());
            slib.loadLib("./s/lib/mve/index.lisp","mve",s.Node<Object>.extend(DOM.build(),null));
            s.Function fun=slib.run("./s/index/index.lisp") as s.Function;
            s.Node<Object> o = fun.exec(null) as s.Node<Object>;

            s.Function getElement = s.Node<Object>.kvs_find1st(o, "getElement") as s.Function;
            s.Function init = s.Node<Object>.kvs_find1st(o, "init") as s.Function;
            s.Function destroy = s.Node<Object>.kvs_find1st(o, "destroy") as s.Function;
            Control els = getElement.exec(null) as Control;
            this.Controls.Add(els);
            els.Dock = DockStyle.Fill;
            init.exec(null);
            this.destroy = destroy;
        }

        private void MainForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            this.destroy.exec(null);
        }
    }

    class SEventHandle
    {
        public SEventHandle(s.Function fun)
        {
            this.fun = fun;
        }
        s.Function fun;

        public void run(object sender, EventArgs e)
        {
            try
            {
                this.fun.exec(null);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.ToString());
            }
        }
    }
    abstract class DOM : s.Function {
        public override string ToString()
        {
            return "DOM";
        }
        public override Function_Type Function_type()
        {
            return Function_Type.Fun_BuildIn;
        }
        public static s.Node<Object> build()
        {
            s.Node<Object> dom = null;
            dom = s.Node<Object>.kvs_extend("createElement", new DOMCreateElement(), dom);
            dom = s.Node<Object>.kvs_extend("attr", new DOMAttr(), dom);
            dom = s.Node<Object>.kvs_extend("action", new DOMAction(), dom);
            dom = s.Node<Object>.kvs_extend("appendChild", new DOMAppendChild(), dom);
            dom = s.Node<Object>.kvs_extend("replaceWith", new DOMReplaceWith(), dom);
            dom = s.Node<Object>.kvs_extend("removeChild", new DOMRemoveChild(), dom);
            dom = s.Node<Object>.kvs_extend("text", new DOMText(), dom);
            dom = s.Node<Object>.kvs_extend("value", new DOMValue(), dom);
            dom = s.Node<Object>.kvs_extend("alert", new DOMAlert(), dom);
            dom = s.Node<Object>.kvs_extend("confirm", new DOMConfirm(), dom);
            /*以下未定义*/
            dom = s.Node<Object>.kvs_extend("createTextNode", new DOMUnDefined(), dom);
            dom = s.Node<Object>.kvs_extend("style", new DOMUnDefined(), dom);
            dom = s.Node<Object>.kvs_extend("prop", new DOMUnDefined(), dom);
            dom = s.Node<Object>.kvs_extend("html", new DOMUnDefined(), dom);
            return dom;
        }
    }
    class DOMCreateElement : DOM
    {
        public override string ToString()
        {
            return base.ToString()+".createElement";
        }
        public override object exec(s.Node<object> args)
        {
            String type = args.First() as String;
            if (type == "button")
            {
                return new Button();
            }
            else if (type == "div")
            {
                return new Panel();
            }
            else if (type == "flow")
            {
                return new FlowLayoutPanel();
            }
            else if (type == "input")
            {
                TextBox t = new TextBox();
                t.ImeMode = ImeMode.HangulFull;
                return t;
            }
            else
            {
                return null;
            }
        }
    }
    class DOMAttr : DOM
    {
        public override string ToString()
        {
            return base.ToString() + ".attr";
        }
        public override object exec(s.Node<object> args)
        {
            Control c = args.First() as Control;
            args = args.Rest();
            String key = args.First() as String;
            args = args.Rest();
            if (args == null)
            {
                if (key == "dock")
                {
                    return c.Dock.ToString();
                }
                else if (key == "width")
                {
                    return c.Width;
                }
                else if (key == "height")
                {
                    return c.Height;
                }
                else if (key == "back-color")
                {
                    return System.Drawing.ColorTranslator.ToHtml(c.BackColor);
                }
            }
            else
            {
                Object value = args.First();
                if (key == "dock")
                {
                    /*
                     * Dock是反常的，是从最后一个组件排，通常第一个组件是Fill，填充剩下的位置。
                     * 而常用的方式比如顺序向下，最后一个Fill，则恰巧相反
                     * 因此第一个Fill在最上，下面的都是Bottom，这样符合预期
                     */
                    String v = value as String;
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
                else if (key == "width")
                {
                    c.Width = (int)value;
                }
                else if (key == "height")
                {
                    c.Height = (int)value;
                }
                else if (key == "back-color")
                {
                    String color = value as String;
                    if (color.StartsWith("#"))
                    {
                        c.BackColor = System.Drawing.ColorTranslator.FromHtml(color);
                    }
                    else
                        if (color.StartsWith("rgba"))
                        {
                            int start = color.IndexOf("(") + 1;
                            int end = color.IndexOf(")");
                            color = color.Substring(start, end - start);
                            String[] cs = color.Split(',');
                            if (cs.Length == 4)
                            {

                                c.BackColor = Color.FromArgb((int)(double.Parse(cs[3]) * 255 / 100), int.Parse(cs[0]), int.Parse(cs[1]), int.Parse(cs[2]));
                            }
                            else if (cs.Length == 3)
                            {
                                c.BackColor = Color.FromArgb(int.Parse(cs[0]), int.Parse(cs[1]), int.Parse(cs[2]));
                            }
                        }
                }
            }
            return null;
        }
    }
    class DOMAction : DOM {
        public override string ToString()
        {
            return base.ToString() + ".action";
        }
        public override object exec(s.Node<object> args)
        {
            Control c = args.First() as Control;
            args = args.Rest();
            String key = args.First() as String;
            args = args.Rest();
            s.Function act = args.First() as s.Function;
            if (key == "click")
            {
                c.Click += new EventHandler(new SEventHandle(act).run);
            }
            return null;
        }
    }
    class DOMAppendChild : DOM
    {
        public override string ToString()
        {
            return base.ToString()+".appendChild";
        }
        public override object exec(s.Node<object> args)
        {
            Control el = args.First() as Control;
            args = args.Rest();
            Control child = args.First() as Control;
            el.Controls.Add(child);
            return null;
        }
    }
    class DOMReplaceWith:DOM{
        public override string ToString()
        {
            return base.ToString()+".replaceWith";
        }
        public override object exec(s.Node<object> args)
        {
            Control old_e = args.First() as Control;
            args = args.Rest();
            Control new_e = args.First() as Control;
            int old_idx = old_e.Parent.Controls.IndexOf(old_e);
            old_e.Parent.Controls.Add(new_e);
            old_e.Parent.Controls.SetChildIndex(new_e, old_idx);
            old_e.Parent.Controls.Remove(old_e);
            return null;
        }
    }

    class DOMRemoveChild : DOM
    {
        public override string ToString()
        {
            return base.ToString()+".removeChild";
        }
        public override object exec(s.Node<object> args)
        {
            Control el = args.First() as Control;
            args = args.Rest();
            Control child = args.First() as Control;
            el.Controls.Remove(child);
            return null;
        }
    }

    class DOMText : DOM
    {
        public override string ToString()
        {
            return base.ToString()+".text";
        }
        public override object exec(s.Node<object> args)
        {
            Control c = args.First() as Control;
            args = args.Rest();
            if (args == null)
            {
                if (c is Button)
                {
                    return (c as Button).Text;
                }
            }
            else
            {
                String text = args.First() as String;
                if (c is Button)
                {
                    (c as Button).Text = text;
                }
            }
            return null;
        }
    }
    class DOMValue : DOM
    {
        public override string ToString()
        {
            return base.ToString()+".value";
        }
        public override object exec(s.Node<object> args)
        {
            Control c = args.First() as Control;
            args = args.Rest();
            if (args == null)
            {
                if (c is TextBox)
                {
                    return (c as TextBox).Text;
                }
            }
            else
            {
                String value = args.First() as String;
                if (c is TextBox)
                {
                    (c as TextBox).Text = value;
                }
            }
            return null;
        }
    }

    class DOMAlert : DOM
    {
        public override string ToString()
        {
            return base.ToString()+".alert";
        }
        public override object exec(s.Node<object> args)
        {
            String msg = args.First() as String;
            MessageBox.Show(msg);
            return null;
        }
    }
    class DOMConfirm : DOM
    {
        public override string ToString()
        {
            return base.ToString()+".confirm";
        }
        public override object exec(s.Node<object> args)
        {
            String msg = args.First() as String;
            DialogResult dr = MessageBox.Show(msg, "", MessageBoxButtons.OKCancel);
            if (dr == DialogResult.OK)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    class DOMUnDefined : DOM
    {
        public override string ToString()
        {
            return base.ToString()+".undefined";
        }
        public override object exec(s.Node<object> args)
        {
            return null;
        }
    }
}