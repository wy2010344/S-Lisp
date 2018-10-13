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
        private Elm_List_View_Columns columns;
        private Elm_List_View_Rows rows;
        public override void appendChild(Elm el)
        {
            if (el is Elm_List_View_Columns)
            {
                this.columns = el as Elm_List_View_Columns;
                this.columns.setParent(this);
            }
            else if (el is Elm_List_View_Rows)
            {
                this.rows = el as Elm_List_View_Rows;
                this.rows.setParent(this);
            }
        }
        public override void removeChild(Elm el)
        {
            if (el == columns)
            {
                columns = null;
            }
            else if (el == rows)
            {
                rows = null;
            }
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
    }
    /// <summary>
    /// 
    /// </summary>
    public class Elm_List_View_Columns : Elm
    {
        public Elm_List_View_Columns()
        {
            this.cols = new List<Elm_List_View_Col>();
            set_real_control(cols);
        }
        private List<Elm_List_View_Col> cols;
        public void setParent(Elm_List_View elv)
        {
            if (this.parent != null)
            {
                foreach (Elm_List_View_Col col in cols)
                {
                    parent.ListView().Columns.Remove(col.Real_Control() as ColumnHeader);
                }
            }
            this.parent = elv;
            if (this.parent != null)
            {
                foreach (Elm_List_View_Col col in cols)
                {
                    parent.ListView().Columns.Add(col.Real_Control() as ColumnHeader);
                }
            }
        }
        private Elm_List_View parent;
        public override void appendChild(Elm el)
        {
            cols.Add(el as Elm_List_View_Col);
            if (parent != null)
            {
                parent.ListView().Columns.Add(el.Real_Control() as ColumnHeader);
            }
        }
        public override void removeChild(Elm el)
        {
            cols.Remove(el as Elm_List_View_Col);
            if (parent != null)
            {
                parent.ListView().Columns.Remove(el.Real_Control() as ColumnHeader);
            }
        }
        public override void replaceWith(Elm el)
        {
            parent.removeChild(this);
            parent.appendChild(el);
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
    public class Elm_List_View_Rows : Elm
    {
        public Elm_List_View_Rows()
        {
            this.rows = new List<Elm_List_View_Row>();
            set_real_control(rows);
        }
        public List<Elm_List_View_Row> rows;

        public void setParent(Elm_List_View elv)
        {
            if (this.parent != null)
            {
                foreach (Elm_List_View_Row row in rows)
                {
                    parent.ListView().Items.Remove(row.Real_Control() as ListViewItem);
                }
            }
            this.parent = elv;
            if (this.parent != null)
            {
                foreach(Elm_List_View_Row row in rows)
                {
                    parent.ListView().Items.Add(row.Real_Control() as ListViewItem);
                }
            }
        }

        public override void appendChild(Elm el)
        {
            rows.Add(el as Elm_List_View_Row);
            if (parent != null)
            {
                parent.ListView().Items.Add(el.Real_Control() as ListViewItem);
            }
        }

        public override void removeChild(Elm el)
        {
            rows.Remove(el as Elm_List_View_Row);
            if (parent != null)
            {
                parent.ListView().Items.Remove(el.Real_Control() as ListViewItem);
            }
        }

        public override void replaceWith(Elm el)
        {
            parent.removeChild(this);
            parent.appendChild(el);
        }
        private Elm_List_View parent;
    }

    /// <summary>
    /// 
    /// </summary>
    public class Elm_List_View_Row : Elm
    {
        public Elm_List_View_Row()
        {
            this.list_view_item = new ListViewItem();
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
