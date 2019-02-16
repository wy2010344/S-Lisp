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

        public override object build(s.Node<object> x, s.Node<object> o)
        {
            ListViewItem.ListViewSubItem lvsi = new ListViewItem.ListViewSubItem();

            return build(lvsi, getK(o), getInits(o), getDestroys(o));
        }

        public override object text(ListViewItem.ListViewSubItem c, s.Node<object> rest)
        {
            if (rest == null)
            {
                return c.Text;
            }
            else
            {
                c.Text = rest.First() as String;
            }
            return null;
        }
    }
}
