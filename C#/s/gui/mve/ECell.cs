using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace gui.mve
{
    public class ECell:E<ListViewItem.ListViewSubItem>
    {
        public ECell(U u)
            : base(u)
        {
        }

        public override CommonReturn<ListViewItem.ListViewSubItem> run(s.Node<object> x, s.Node<object> o)
        {
            ListViewItem.ListViewSubItem lvsi = new ListViewItem.ListViewSubItem();

            return new CommonReturn<ListViewItem.ListViewSubItem>(lvsi, getK(o), getInits(o), getDestroys(o));
        }

        public override Object text(ListViewItem.ListViewSubItem c)
        {
            return c.Text;
        }

        
        public override void text(ListViewItem.ListViewSubItem c, string value)
        {
            c.Text = value;
        }
    }
}
