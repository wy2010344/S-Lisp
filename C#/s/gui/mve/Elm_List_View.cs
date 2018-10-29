using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace gui.mve
{

    /// <summary>
    /// 
    /// </summary>
    public class Elm_List_View : Elm_Control
    {
        public Elm_List_View()
        {
            this.listview = new ListView();
            set_real_control(listview);
        }

        private ListView listview;
        public ListView ListView() { return listview; }

        public override object attr(string key, s.Node<object> args)
        {
            if (key == "View")
            {
                if (args == null)
                {
                    return listview.View;
                }
                else
                {
                    String view = args.First() as String;
                    if (view == "Details")
                    {
                        listview.View = View.Details;
                    }
                    else if (view == "LargeIcon")
                    {
                        listview.View = View.LargeIcon;
                    }
                    else if (view == "List")
                    {
                        listview.View = View.List;
                    }
                    else if (view == "SmallIcon")
                    {
                        listview.View = View.SmallIcon;
                    }
                    else if (view == "Tile")
                    {
                        listview.View = View.Tile;
                    }
                }
            }
            else
            {
                return base.attr(key, args);
            }
            return null;
        }

        public void appendColumn(Elm_List_View_Col cel)
        {
            ColumnHeader ch= cel.Real_Control() as ColumnHeader;
            listview.Columns.Add(ch);
        }
        public void removeColumn(Elm_List_View_Col cel)
        {
            ColumnHeader ch = cel.Real_Control() as ColumnHeader;
            listview.Columns.Remove(ch);
        }
        public void appendRow(Elm_List_View_Row rel)
        {
            ListViewItem lvi = rel.Real_Control() as ListViewItem;
            listview.Items.Add(lvi);
        }
        public void removeRow(Elm_List_View_Row rel)
        {
            ListViewItem lvi = rel.Real_Control() as ListViewItem;
            listview.Items.Remove(lvi);
        }
        public override void replaceWith(Elm el)
        {
            shareReplaceWith(listview, el.Real_Control() as Control);
        }
        public void begin_update()
        {
            listview.BeginUpdate();
        }
        public void end_update()
        {
            listview.EndUpdate();
        }

        public s.Node<Object> selectedIndexs(){
            s.Node<Object> o = null;
            foreach(int lvi in listview.SelectedIndices)
            {
               o=s.Node<Object>.extend(lvi, o);
            }
            return o;
        }
        public void selectedIndexs(s.Node<Object> o)
        {
            listview.SelectedIndices.Clear();
            for (s.Node<Object> t = o; t != null; t = t.Rest())
            {
                listview.SelectedIndices.Add((int)t.First());
            }
        }
        public s.Node<Object> checkedIndexs()
        {
            s.Node<Object> o = null;
            foreach (int lvi in listview.CheckedIndices)
            {
                o = s.Node<Object>.extend(lvi, o);
            }
            return o;
        }
        public void checkedIndexs(s.Node<Object> o)
        {
            foreach (ListViewItem lvi in listview.CheckedItems)
            {
                lvi.Checked = false;
            }
            for (s.Node<Object> t = o; t != null; t = t.Rest())
            {
                int index = (int)t.First();
                listview.Items[index].Checked = true;
            }
        }
    }

    public class DOMListViewBeginUpdate : DOM
    {
        public override object exec(s.Node<object> args)
        {
            Elm_List_View ev = args.First() as Elm_List_View;
            ev.begin_update();
            return null;
        }
    }
    public class DOMListViewEndUpdate : DOM
    {
        public override object exec(s.Node<object> args)
        {
            Elm_List_View ev = args.First() as Elm_List_View;
            ev.end_update();
            return null;
        }
    }

    public class DOMListViewAppendColumn : DOM {
        public override object exec(s.Node<object> args)
        {
            Elm_List_View ev = args.First() as Elm_List_View;
            args = args.Rest();
            Elm_List_View_Col col = args.First() as Elm_List_View_Col;
            ev.appendColumn(col);
            return null;
        }
    }
    public class DOMListViewRemoveColumn : DOM {
        public override object exec(s.Node<object> args)
        {
            Elm_List_View ev = args.First() as Elm_List_View;
            args = args.Rest();
            Elm_List_View_Col col = args.First() as Elm_List_View_Col;
            ev.removeColumn(col);
            return null;
        }
    }

    public class DOMListViewAppendRow : DOM
    {
        public override object exec(s.Node<object> args)
        {
            Elm_List_View ev = args.First() as Elm_List_View;
            args = args.Rest();
            Elm_List_View_Row row = args.First() as Elm_List_View_Row;
            ev.appendRow(row);
            return null;
        }
    }
    public class DOMListViewRemoveRow : DOM
    {
        public override object exec(s.Node<object> args)
        {
            Elm_List_View ev = args.First() as Elm_List_View;
            args = args.Rest();
            Elm_List_View_Row row = args.First() as Elm_List_View_Row;
            ev.removeRow(row);
            return null;
        }
    }
    public class DOMListViewSelectedIndexs : DOM
    {
        public override object exec(s.Node<object> args)
        {
            Elm_List_View ev = args.First() as Elm_List_View;
            args = args.Rest();
            if (args == null)
            {
                return ev.selectedIndexs();
            }
            else
            {
                ev.selectedIndexs(args.First() as s.Node<Object>);
                return null;
            }
        }
    }
    public class DOMListViewCheckedIndexs : DOM
    {
        public override object exec(s.Node<object> args)
        {
            Elm_List_View ev = args.First() as Elm_List_View;
            args = args.Rest();
            if (args == null)
            {
                return ev.checkedIndexs();
            }
            else
            {
                ev.checkedIndexs(args.First() as s.Node<Object>);
                return null;
            }
        }
    }
    /// <summary>
    /// 
    /// </summary>
    public class Elm_List_View_Col : Elm
    {
        public Elm_List_View_Col()
        {
            this.ch = new ColumnHeader();
            set_real_control(ch);
        }
        private ColumnHeader ch;

        public override object text(s.Node<object> args)
        {
            if (args == null)
            {
                return ch.Text;
            }
            else
            {
                ch.Text = args.First() as String;
                return null;
            }
        }
    }
    /// <summary>
    /// 
    /// </summary>
    public class Elm_List_View_Row : Elm
    {
        public Elm_List_View_Row()
        {
            this.list_view_item = new ListViewItem();
            this.list_view_item.Tag = this;/*为了获得反向查询*/
            set_real_control(list_view_item);
        }
        private ListViewItem list_view_item;

        public override void appendChild(Elm el)
        {
            list_view_item.SubItems.Add(el.Real_Control() as ListViewItem.ListViewSubItem);
        }
        public override void removeChild(Elm el)
        {
            list_view_item.SubItems.Remove(el.Real_Control() as ListViewItem.ListViewSubItem);
        }

        public override void replaceWith(Elm el)
        {
            ListView parent=list_view_item.ListView;
            int index=parent.Items.IndexOf(list_view_item);
            parent.Items.Insert(index, el.Real_Control() as ListViewItem);
            parent.Items.Remove(list_view_item);
        }

        public override object text(s.Node<object> args)
        {
            if (args == null)
            {
                return list_view_item.Text;
            }
            else
            {
                list_view_item.Text = args.First() as String;
                return null;
            }
        }
    }
    /// <summary>
    /// 
    /// </summary>
    public class Elm_List_View_Cell : Elm
    {
        public Elm_List_View_Cell()
        {
            this.lvsi = new ListViewItem.ListViewSubItem();
            set_real_control(lvsi);
        }
        private ListViewItem.ListViewSubItem lvsi;

        public override object text(s.Node<object> args)
        {
            if (args == null)
            {
                return lvsi.Text;
            }
            else
            {
                lvsi.Text = args.First() as String;
                return null;
            }
        }
    }
}
