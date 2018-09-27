using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace gui.mve
{
    public class MVE
    {
        public MVE(s.S slib,String path)
        {
            slib.loadLib(s.LibPath.instance().calculate("mve/winform/index.lisp"), "mve", s.Node<Object>.extend(DOM.build(),null));

            s.Node<Object> o = (slib.run(path) as s.Function).exec(null) as s.Node<Object>;
            element=((s.Node<Object>.kvs_find1st(o, "getElement") as s.Function).exec(null) as Elm).Real_Control() as Control;
            init = s.Node<Object>.kvs_find1st(o, "init") as s.Function;
            destroy = s.Node<Object>.kvs_find1st(o, "destroy") as s.Function;
            width = s.Node<Object>.kvs_find1st(o, "width") as s.Function;
            height = s.Node<Object>.kvs_find1st(o, "height") as s.Function;
        }
        public Control element;
        public s.Function destroy;
        public s.Function init;
        public s.Function height;
        public s.Function width;
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
            dom = s.Node<Object>.kvs_extend("event", new DOMEvent(), dom);
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
            mve.Elm el = args.First() as mve.Elm;
            args = args.Rest();
            String key = args.First() as String;
            args = args.Rest();
            return el.attr(key, args);
        }
    }
    class DOMEvent : DOM
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
            return base.ToString() + ".appendChild";
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
            return base.ToString() + ".removeChild";
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
            return base.ToString() + ".replaceWith";
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
