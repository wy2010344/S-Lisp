using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace gui.mve
{
    public class EListView:EC<ListView>
    {
        readonly s.Function buildRows;
        readonly s.Function buildColumns;
        public EListView(U u) : base(u)
        {
            buildRows = u.build_children_factory.exec(s.Node<Object>.list(
                "key","rows",
                "before",new ListViewBefore(),
                "after",new ListViewAfter(),
                "appendChild",new RowAppendChild(),
                "removeChild",new RowRemoveChild(),
                "insertChildBefore",new RowInsertBefore()
                )) as s.Function;
            buildColumns = u.build_children_factory.exec(s.Node<Object>.list(
                "key", "columns",
                "before", new ListViewBefore(),
                "after", new ListViewAfter(),
                "appendChild", new ColumnAppendChild(),
                "removeChild", new ColumnRemoveChild(),
                "insertChildBefore",new ColumnInsertBefore()
                )) as s.Function;
        }
        ColumnReplaceChild columnReplaceChild = new ColumnReplaceChild();
        RowReplaceChild rowReplaceChild = new RowReplaceChild();

        public override CommonReturn<ListView> run(s.Node<object> x, s.Node<object> o)
        {
            ListView lv = new ListView();
            s.Node<Object> obj = u.exec_buildChild(buildColumns, lv, columnReplaceChild, x, o);
            o = s.Node<Object>.kvs_extend("inits", getInits(obj),o);
            o = s.Node<Object>.kvs_extend("destroys", getDestroys(obj), o);
            o = s.Node<Object>.kvs_extend("k", getK(obj), o);
            obj = u.exec_buildChild(buildRows, lv, rowReplaceChild, x, o);
            return new CommonReturn<ListView>(lv, getK(obj), getInits(obj), getDestroys(obj));
        }


        public override Object attr_gs(ListView c, string key, object value)
        {
            if (key == "SelectedIndices")
            {
                if (value == null)
                {
                    s.Node<Object> cs = null;
                    for (int i = c.SelectedIndices.Count; i > 0; i--)
                    {
                        cs = s.Node<Object>.extend(c.SelectedIndices[i - 1], cs);
                    }
                    return cs;
                }
                else
                {
                    s.Node<Object> cs = value as s.Node<Object>;
                    c.SelectedIndices.Clear();
                    while (cs != null)
                    {
                        c.SelectedIndices.Add((int)cs.First());
                        cs = cs.Rest();
                    }
                }
            }
            else if (key == "CheckedIndices")
            {
                if (value == null)
                {
                    s.Node<Object> cs = null;
                    for (int i = c.CheckedIndices.Count; i > 0; i--)
                    {
                        cs = s.Node<Object>.extend(c.CheckedIndices[i - 1], cs);
                    }
                    return cs;
                }
                else
                {
                    s.Node<Object> cs = value as s.Node<Object>;
                    foreach (ListViewItem lvi in c.CheckedItems)
                    {
                        lvi.Checked = false;
                    }
                    for (s.Node<Object> t = cs; t != null; t = t.Rest())
                    {
                        int index = (int)t.First();
                        c.Items[index].Checked = true;
                    }
                }
            }
            else if (key == "View")
            {
                if (value == null)
                {
                    return c.View;
                }
                else
                {
                    String view = value as String;
                    if (view == "Details")
                    {
                        c.View = View.Details;
                    }
                    else if (view == "LargeIcon")
                    {
                        c.View = View.LargeIcon;
                    }
                    else if (view == "List")
                    {
                        c.View = View.List;
                    }
                    else if (view == "SmallIcon")
                    {
                        c.View = View.SmallIcon;
                    }
                    else if (view == "Tile")
                    {
                        c.View = View.Tile;
                    }
                }
            }
            else
            {
                return base.attr_gs(c, key, value);
            }
            return null;
        }
    }

    class ColumnAppendChild : EAppendChild<ListView, ColumnHeader>
    {
        public override void run(ListView pel, ColumnHeader el)
        {
            pel.Columns.Add(el);
        }
    }
    class ColumnRemoveChild : ERemoveChild<ListView, ColumnHeader>
    {
        public override void run(ListView pel, ColumnHeader el)
        {
            pel.Columns.Remove(el);
        }
    }
    class ColumnInsertBefore : EInsertChildBefore<ListView, ColumnHeader>
    {
        public override int getIndex(ListView pel, ColumnHeader old_el)
        {
            return pel.Columns.IndexOf(old_el);
        }
        public override void insert(ListView pel, ColumnHeader new_el, int index)
        {
            pel.Columns.Insert(index, new_el);
        }
    }
    class ColumnReplaceChild : EReplaceChild<ColumnHeader>
    {
        public override void replace(object e, ColumnHeader old_el, ColumnHeader new_el)
        {
            int index=old_el.ListView.Columns.IndexOf(old_el);
            if (index > -1)
            {
                old_el.ListView.Columns.Insert(index, new_el);
                old_el.ListView.Columns.Remove(old_el);
            }
        }
    }
    class RowAppendChild : EAppendChild<ListView, ListViewItem>
    {
        public override void run(ListView pel, ListViewItem el)
        {
            pel.Items.Add(el);
        }
    }
    class RowRemoveChild : ERemoveChild<ListView, ListViewItem>
    {
        public override void run(ListView pel, ListViewItem el)
        {
            pel.Items.Remove(el);
        }
    }
    class RowInsertBefore : EInsertChildBefore<ListView, ListViewItem>
    {
        public override int getIndex(ListView pel, ListViewItem old_el)
        {
            return pel.Items.IndexOf(old_el);
        }
        public override void insert(ListView pel, ListViewItem new_el, int index)
        {
            pel.Items.Insert(index, new_el);
        }
    }
    class RowReplaceChild : EReplaceChild<ListViewItem>
    {
        public override void replace(object e, ListViewItem old_el, ListViewItem new_el)
        {
            int index = old_el.ListView.Items.IndexOf(old_el);
            if (index > -1)
            {
                old_el.ListView.Items.Insert(index, new_el);
                old_el.ListView.Items.Remove(old_el);
            }
        }
    }
    class ListViewBefore : s.LibFunction
    {
        public override object exec(s.Node<object> args)
        {
            ListView lvi = args.First() as ListView;
            lvi.BeginUpdate();
            return null;
        }
    }
    class ListViewAfter : s.LibFunction
    {
        public override object exec(s.Node<object> args)
        {
            ListView lvi = args.First() as ListView;
            lvi.EndUpdate();
            return null;
        }
    }
}
