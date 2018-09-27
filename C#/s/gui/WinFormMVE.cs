using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace gui
{
    class WinFormMVE
    {
        private s.Function destroy;
        private s.Function reWidth;
        private s.Function reHeight;
        private MainForm form;
        private Control center_control;
        public WinFormMVE(MainForm form)
        {
            Encoding encoding = new UTF8Encoding(false);
            char line_split = '\n';
            s.S slib = new s.S(line_split, encoding);
            slib.addDef("read", new s.library.Read(line_split, encoding));
            slib.addDef("write", new s.library.Write());
            slib.addDef("cache", new s.library.Cache());
            //slib.addDef("build-element", new BuildElement());
            slib.loadLib(s.LibPath.instance().calculate("mve/index.lisp"), "mve", s.Node<Object>.extend(DOM.build(), null));
            s.Function fun = slib.run("./s/index/index.lisp") as s.Function;
            s.Node<Object> o = fun.exec(null) as s.Node<Object>;

            s.Function getElement = s.Node<Object>.kvs_find1st(o, "getElement") as s.Function;
            s.Function init = s.Node<Object>.kvs_find1st(o, "init") as s.Function;
            s.Function destroy = s.Node<Object>.kvs_find1st(o, "destroy") as s.Function;
            center_control = (getElement.exec(null) as mve.Elm).Real_Control() as Control;


            form.Controls.Add(center_control);
            center_control.Dock = DockStyle.Fill;
            init.exec(null);
            this.form = form;
            this.reHeight = s.Node<Object>.kvs_find1st(o, "height") as s.Function;
            this.reWidth = s.Node<Object>.kvs_find1st(o, "width") as s.Function;
            form.ResizeEnd+=new EventHandler(form_ResizeEnd);
            form_ResizeEnd(null, null);
            form.FormClosing += new System.Windows.Forms.FormClosingEventHandler(form_FormClosing);
            this.destroy = destroy;
        }

        void form_ResizeEnd(object sender, EventArgs e)
        {
            if (center_control is ScrollableControl)
            {
                ScrollableControl sc=center_control as ScrollableControl;
                int ws = sc.AutoScrollMargin.Width;
                int w=sc.DisplayRectangle.Size.Width;
            }
            int w1 = center_control.Width;
            this.reWidth.exec(s.Node<Object>.extend(center_control.ClientSize.Width, null));
            this.reHeight.exec(s.Node<Object>.extend(center_control.ClientSize.Height, null));
        }

        void form_FormClosing(object sender, System.Windows.Forms.FormClosingEventArgs e)
        {
            this.destroy.exec(null);
        }
    }

    abstract class DOM : s.Function
    {
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
            dom = s.Node<Object>.kvs_extend("locsize", new DOMUnDefined(), dom);
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
            return base.ToString() + ".createElement";
        }
        public override object exec(s.Node<object> args)
        {
            String type = args.First() as String;
            if (type == "button")
            {
                return new mve.Elm_Button();
            }
            else if (type == "div")
            {
                return new mve.Elm_Div();
            }
            else if (type == "flow")
            {
                return new mve.Elm_Flow();
            }
            else if (type == "list-view")
            {
                return new mve.Elm_List_View();
            }
            else if (type == "columns")
            {
                return new mve.Elm_List_View_Columns();          
            }
            else if (type == "col")
            {
                return new mve.Elm_List_View_Col();
            }
            else if (type == "rows")
            {
                return new mve.Elm_List_View_Rows();
            }
            else if (type == "row")
            {
                return new mve.Elm_List_View_Row();
            }
            else if (type == "cell")
            {
                return new mve.Elm_List_View_Cell();
            }
            else if (type == "input")
            {
                return new mve.Elm_Input();
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
            mve.Elm el=args.First() as mve.Elm;
            args=args.Rest();
            String key = args.First() as String;
            args = args.Rest();
            return el.attr(key, args);
        }
    }
    class DOMAction : DOM
    {
        public override string ToString()
        {
            return base.ToString() + ".action";
        }
        public override object exec(s.Node<object> args)
        {
            mve.Elm el = args.First() as mve.Elm;
            args = args.Rest();
            String key = args.First() as String;
            args = args.Rest();
            s.Function act = args.First() as s.Function;
            el.action(key, act);
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
            mve.Elm pel = args.First() as mve.Elm;
            args = args.Rest();
            mve.Elm el = args.First() as mve.Elm;
            pel.appendChild(el);
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
            mve.Elm pel = args.First() as mve.Elm;
            args = args.Rest();
            mve.Elm el = args.First() as mve.Elm;
            pel.removeChild(el);
            return null;
        }
    }
    class DOMReplaceWith : DOM
    {
        public override string ToString()
        {
            return base.ToString()+".replaceWith";
        }
        public override object exec(s.Node<object> args)
        {
            mve.Elm el = args.First() as mve.Elm;
            args = args.Rest();
            mve.Elm new_el = args.First() as mve.Elm;
            el.replaceWith(new_el);
            return null;
        }
    }

    class DOMText : DOM
    {
        public override string ToString()
        {
            return base.ToString() + ".text";
        }
        public override object exec(s.Node<object> args)
        {
            try
            {
                mve.Elm el = args.First() as mve.Elm;
                args = args.Rest();
                return el.text(args);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return null;
            }
        }
    }
    class DOMValue : DOM
    {
        public override string ToString()
        {
            return base.ToString() + ".value";
        }
        public override object exec(s.Node<object> args)
        {
            try
            {
                mve.Elm el = args.First() as mve.Elm;
                args = args.Rest();
                return el.value(args);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return null;
            }
        }
    }

    class DOMAlert : DOM
    {
        public override string ToString()
        {
            return base.ToString() + ".alert";
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
            return base.ToString() + ".confirm";
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
            return base.ToString() + ".undefined";
        }
        public override object exec(s.Node<object> args)
        {
            return null;
        }
    }
}
