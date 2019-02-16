using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Drawing;


namespace gui.mve
{
    public class MVE
    {
        public MVE(s.S slib,Control pel,String path)
        {
            slib.loadLibKV_delay(s.LibPath.instance().calculate("mve/winform/index.lisp"), "mve", s.Node<Object>.extend(new Factory(), null));

            s.Function fun = (slib.run(path) as s.Function).exec(null) as s.Function;
            s.Node<Object> o=fun.exec(s.Node<Object>.list(
                "pel",pel,
                "replaceChild",new ControlReplaceChild()
                )
            ) as s.Node<Object>;
            element = (s.Node<Object>.kvs_find1st(o, "getElement") as s.Function).exec(null) as Control;
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

    class Factory:s.LibFunction
    {
        public override object exec(s.Node<object> args)
        {
            s.Function build_children_factory = args.First() as s.Function;
            Build build = new Build(build_children_factory);
            return s.Node<Object>.list(
                "build",build,
                "attr",new SetAttr(build,"attr"),
                "event", new SetAttr(build, "event"),
                "text", new SetAttr(build, "text"),
                "value", new SetAttr(build, "value"),
                "alert",new DOMAlert(),
                "confirm",new DOMConfirm()
            );
        }
        public override FunctionType Function_type()
        {
            return FunctionType.Fun_BuildIn;
        }
        public override string ToString()
        {
            return "Factory";
        }
    }
    class DOMAlert : s.LibFunction
    {
        public override object exec(s.Node<object> args)
        {
            String msg = args.First() as String;
            MessageBox.Show(msg);
            return null;
        }
    }
    class DOMConfirm : s.LibFunction
    {
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
    class Build:s.LibFunction
    {
        public Build(s.Function build_children_factory)
        {
            U u = new U(build_children_factory);
            this.button = new EButton(u);
            this.panel = new EDiv(u);
            this.flow = new EFlow(u);
            this.input = new EInput(u);
            this.listview = new EListView(u);
            this.list_column = new EColumn(u);
            this.list_row = new ERow(u);
            this.list_cell = new ECell(u);
            this.tab = new ETab(u);
            this.tabPage = new ETabPage(u);
        }
        public EButton button;
        public EDiv panel;
        public EFlow flow;
        public EInput input;
        public EListView listview;
        public EColumn list_column;
        public ERow list_row;
        public ECell list_cell;
        public ETab tab;
        public ETabPage tabPage;
        public override object exec(s.Node<object> args)
        {
            String type = args.First() as String;
            args = args.Rest();
            s.Node<Object> x = args.First() as s.Node<Object>;
            args = args.Rest();
            s.Node<Object> o = args.First() as s.Node<Object>;
            if (type == "button")
            {
                return button.build(x, o);
            }
            else if (type == "div")
            {
                return panel.build(x, o);
            }
            else if (type == "flow")
            {
                return flow.build(x, o);
            }
            else if (type == "input")
            {
                return input.build(x, o);
            }
            else if (type == "list")
            {
                return listview.build(x, o);
            }
            else if (type == "list-column")
            {
                return list_column.build(x, o);
            }
            else if (type == "list-row")
            {
                return list_row.build(x, o);
            }
            else if (type == "list-cell")
            {
                return list_cell.build(x, o);
            }
            else if (type == "tab")
            {
                return tab.build(x, o);
            }
            else if (type == "tab-page")
            {
                return tabPage.build(x, o);
            }
            else
            {
                return null;
            }
        }
    }
    class SetAttr : s.LibFunction
    {
        public SetAttr(Build build,String type)
        {
            this.build = build;
            this.type = type;
        }
        Build build;
        String type;
        public override object exec(s.Node<object> args)
        {
            Object c = args.First();
            args = args.Rest();
            if (c is Button)
            {
                return run<Button>(build.button, c as Button, type, args);
            }
            else if (c is FlowLayoutPanel)
            {
                //必须在Panel前面，因为FLowLayoutPanel继承自Panel
                return run<FlowLayoutPanel>(build.flow, c as FlowLayoutPanel, type, args);
            }
            else if (c is TabControl)
            {
                return run<TabControl>(build.tab, c as TabControl, type, args);
            }
            else if (c is TabPage)
            {
                //从Panel继承，得放前面
                return run<TabPage>(build.tabPage, c as TabPage, type, args);
            }
            else if (c is Panel)
            {
                return run<Panel>(build.panel,c as Panel, type, args);
            }
            else if (c is TextBox)
            {
                return run<TextBox>(build.input, c as TextBox, type, args);
            }
            else if (c is ListView)
            {
                return run<ListView>(build.listview,c as ListView,type,args);
            }
            else if (c is ColumnHeader)
            {
                return run<ColumnHeader>(build.list_column, c as ColumnHeader, type, args);
            }
            else if (c is ListViewItem)
            {
                return run<ListViewItem>(build.list_row, c as ListViewItem, type, args);
            }
            else if (c is ListViewItem.ListViewSubItem)
            {
                return run<ListViewItem.ListViewSubItem>(build.list_cell, c as ListViewItem.ListViewSubItem, type, args);
            }
            else
            {
                return null;
            }
        }

        Object run<T>(E<T> e,T c, String type, s.Node<Object> args)
        {
            if (type == "attr")
            {
                String key = args.First() as String;
                args = args.Rest();
                return e.attr(c, key, args);
            }
            else if (type == "event")
            {
                String key = args.First() as String;
                args = args.Rest();
                return e.action(c, key, args);
            }
            else if (type == "text")
            {
                return e.text(c, args);
            }
            else if (type == "value")
            {
                return e.value(c, args);
            }
            else
            {
                return null;
            }
        }
    }

    public abstract class E<T>
    {
        protected U u;
        public E(U u)
        {
            this.u = u;
        }

        public s.Node<Object> build(Object element,Object k,Object inits,Object destroys)
        {
            return s.Node<Object>.list("element",element,"k",k,"inits",inits,"destroys",destroys);
        }
        public abstract Object build(s.Node<Object> x, s.Node<Object> o);
        public static s.Node<Object> getK(s.Node<Object> o)
        {
            return s.Node<Object>.kvs_find1st(o,"k") as s.Node<Object>;
        }
        public static s.Node<Object> getInits(s.Node<Object> o)
        {
            return s.Node<Object>.kvs_find1st(o, "inits") as s.Node<Object>;
        }

        public static s.Node<Object> getDestroys(s.Node<Object> o)
        {
            return s.Node<Object>.kvs_find1st(o, "destroys") as s.Node<Object>;
        }
        public virtual Object attr(T c, String key, s.Node<Object> rest)
        {
            Type Ts = c.GetType();
            System.Reflection.PropertyInfo info = Ts.GetProperty(key);
            if (info != null)
            {
                if (rest == null)
                {
                    Object o = info.GetValue(c, null);
                    if (o is Color)
                    {
                        return System.Drawing.ColorTranslator.ToHtml((Color)o);
                    }
                }
                else
                {
                    Object o = rest.First();
                    if (info.PropertyType == typeof(Color))
                    {
                        String color = rest.First() as String;
                        if (color.StartsWith("#"))
                        {
                            o = System.Drawing.ColorTranslator.FromHtml(color);
                        }
                        else if (color.StartsWith("rgba"))
                        {
                            int start = color.IndexOf("(") + 1;
                            int end = color.IndexOf(")");
                            color = color.Substring(start, end - start);
                            String[] cs = color.Split(',');
                            if (cs.Length == 4)
                            {

                                o = Color.FromArgb((int)(double.Parse(cs[3]) * 255 / 100), int.Parse(cs[0]), int.Parse(cs[1]), int.Parse(cs[2]));
                            }
                            else if (cs.Length == 3)
                            {
                                o = Color.FromArgb(int.Parse(cs[0]), int.Parse(cs[1]), int.Parse(cs[2]));
                            }
                        }
                    }
                    else
                    {
                        o = Convert.ChangeType(o, info.PropertyType);
                    }
                    info.SetValue(c, o, null);
                }
            }
            else
            {
                Console.WriteLine("未找到该属性");
            }
            return null;
        }
        public virtual Object action(T c, String key, s.Node<Object> rest)
        {
            throw new Exception("The method or operation is not implemented.");
        }
        public virtual Object text(T c,s.Node<Object> rest)
        {
            throw new Exception("The method or operation is not implemented.");
        }
        public virtual Object value(T c, s.Node<Object> rest)
        {
            throw new Exception("The method or operation is not implemented.");
        }
    }


    public abstract class EAppendChild<P, T> : s.LibFunction
        where P : class
        where T : class
    {
        public override object exec(s.Node<object> args)
        {
            P pel = args.First() as P;
            args = args.Rest();
            T el = args.First() as T;
            run(pel, el);
            return null;
        }
        public abstract void run(P pel, T el);
    }
    public abstract class ERemoveChild<P, T> : s.LibFunction
        where P : class
        where T : class
    {
        public override object exec(s.Node<object> args)
        {
            P pel = args.First() as P;
            args = args.Rest();
            T el = args.First() as T;
            run(pel, el);
            return null;
        }
        public abstract void run(P pel, T el);
    }
    public abstract class EInsertChildBefore<P, T> : s.LibFunction
        where P : class
        where T : class
    {
        public override object exec(s.Node<object> args)
        {
            P pel = args.First() as P;
            args = args.Rest();
            T new_el = args.First() as T;
            args = args.Rest();
            T old_el = args.First() as T;
            int index = getIndex(pel, old_el);
            if (index > -1)
            {
                insert(pel, new_el, index);
            }
            return null;
        }
        public abstract int getIndex(P pel, T old_el);
        public abstract void insert(P pel, T new_el, int index);

    }
    public abstract class EReplaceChild<T> : s.LibFunction
        where T : class
    {
        public override object exec(s.Node<object> args)
        {
            Object e = args.First();
            args = args.Rest();
            T old_el = args.First() as T;
            args = args.Rest();
            T new_el = args.First() as T;
            replace(e, old_el, new_el);
            return null;
        }
        public abstract void replace(Object e, T old_el, T new_el);
    }
    class ControlAppendChild : EAppendChild<Control,Control>
    {
        public override void run(Control pel, Control el)
        {
            pel.Controls.Add(el);
        }
    }
    class ControlRemoveChild : ERemoveChild<Control, Control>
    {
        public override void run(Control pel, Control el)
        {
            pel.Controls.Remove(el);
        }
    }
    class ControlInsertChildBefore : EInsertChildBefore<Control, Control>
    {
        public override int getIndex(Control pel, Control old_el)
        {
            return pel.Controls.IndexOf(old_el);
        }

        public override void insert(Control pel, Control new_el, int index)
        {
            pel.Controls.Add(new_el);
            pel.Controls.SetChildIndex(new_el, index);
        }
    }
    class ControlReplaceChild : EReplaceChild<Control>
    {
        public override void replace(object e, Control old_el, Control new_el)
        {
            int index = old_el.Parent.Controls.IndexOf(old_el);
            if (index > -1)
            {
                old_el.Parent.Controls.Add(new_el);
                old_el.Parent.Controls.SetChildIndex(new_el, index);
                old_el.Parent.Controls.Remove(old_el);
            }
        }
    }
    public class U
    {
        public readonly s.Function build_children_factory;
        private readonly s.Function buildChildren;
        public U(s.Function build_children_factory)
        {
            this.build_children_factory = build_children_factory;
            s.Node<Object> args = s.Node<Object>.list(
                "key", "children",
                "appendChild", new ControlAppendChild(),
                "removeChild", new ControlRemoveChild(),
                "insertChildBefore", new ControlInsertChildBefore()
            );
            buildChildren = build_children_factory.exec(args) as s.Function;

        }
        ControlAppendChild controlAppendChild = new ControlAppendChild();
        public s.Node<Object> exec_buildChild_Control(Control parent, s.Node<Object> x, s.Node<Object> o)
        {
            return exec_buildChild(buildChildren, parent, controlAppendChild, x, o);
        }
        public s.Node<Object> exec_buildChild(s.Function buildChildren, Object parent, s.Function replaceChild, s.Node<Object> x, s.Node<Object> o)
        {
            s.Node<Object> args = s.Node<Object>.list(
                s.Node<Object>.list("pel", parent, "replaceChild", replaceChild),
                x,
                o
            );
            return buildChildren.exec(args) as s.Node<Object>;
        }
    }
    public abstract class EC<T> : E<T> where T:Control
    {
        public EC(U u)
            : base(u)
        {
        }
        public override object attr(T c, string key, s.Node<object> rest)
        {
            if (key == "Dock")
            {
                if (rest == null)
                {
                    return c.Dock;
                }
                else
                {
                    String v = rest.First() as String;
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
                return null;
            }
            else
            {
                return base.attr(c, key, rest);
            }
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
}
