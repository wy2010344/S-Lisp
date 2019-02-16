using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace gui.mve
{
    public class EColumn:E<ColumnHeader>
    {
        public EColumn(U u)
            : base(u)
        {
        }
        public override object build(s.Node<object> x, s.Node<object> o)
        {
            ColumnHeader header = new ColumnHeader();
            return build(header, getK(o), getInits(o), getDestroys(o));
        }

        public override object text(ColumnHeader c, s.Node<object> rest)
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
