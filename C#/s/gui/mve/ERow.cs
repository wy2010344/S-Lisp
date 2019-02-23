using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace gui.mve
{
    public class ERow:E<ListViewItem>
    {
        readonly s.Function build_children;
        public ERow(U u) : base(u)
        {
            build_children = u.build_children_factory.exec(s.Node<Object>.list(
                "key","cells",
                "appendChild",new CellAppendChild(),
                "removeChild",new CellRemoveChild(),
                "insertChildBefore",new CellInserBefore()
                )) as s.Function;
        }
        CellReplaceChild cellReplaceChild = new CellReplaceChild();
        public override CommonReturn<ListViewItem> run(s.Node<object> x, s.Node<object> o)
        {
            ListViewItem lvi = new ListViewItem();
            s.Node<Object> obj = u.exec_buildChild(build_children, lvi, cellReplaceChild, x, o);
            return new CommonReturn<ListViewItem>(lvi, getK(obj), getInits(obj), getDestroys(obj));
        }

        public override object text(ListViewItem c)
        {
            return c.Text;
        }

        public override void text(ListViewItem c, string value)
        {
            c.Text = value;
        }
    }

    class CellAppendChild : EAppendChild<ListViewItem, ListViewItem.ListViewSubItem>
    {
        public override void run(ListViewItem pel, ListViewItem.ListViewSubItem el)
        {
            pel.SubItems.Add(el);
        }
    }
    class CellRemoveChild : ERemoveChild<ListViewItem, ListViewItem.ListViewSubItem>
    {
        public override void run(ListViewItem pel, ListViewItem.ListViewSubItem el)
        {
            pel.SubItems.Remove(el);
        }
    }
    class CellInserBefore : EInsertChildBefore<ListViewItem, ListViewItem.ListViewSubItem>
    {
        public override int getIndex(ListViewItem pel, ListViewItem.ListViewSubItem old_el)
        {
            return pel.SubItems.IndexOf(old_el);
        }
        public override void insert(ListViewItem pel, ListViewItem.ListViewSubItem new_el, int index)
        {
            pel.SubItems.Insert(index, new_el);
        }
    }
    class CellReplaceChild : EReplaceChild<ListViewItem.ListViewSubItem>
    {
        public override void replace(object e, ListViewItem.ListViewSubItem old_el, ListViewItem.ListViewSubItem new_el)
        {
            ListViewItem pel=s.Node<Object>.kvs_find1st(e as s.Node<Object>, "pel") as ListViewItem;
           int index= pel.SubItems.IndexOf(old_el);
           if (index > -1)
           {
               pel.SubItems.Insert(index, new_el);
               pel.SubItems.Remove(old_el);
           }
        }
    }
}
