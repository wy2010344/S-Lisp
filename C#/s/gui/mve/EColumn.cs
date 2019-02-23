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
        public override CommonReturn<ColumnHeader> run(s.Node<object> x, s.Node<object> o)
        {
            ColumnHeader header = new ColumnHeader();
            return new CommonReturn<ColumnHeader>(header, getK(o), getInits(o), getDestroys(o));
        }

        public override object text(ColumnHeader c)
        {
            return c.Text;
        }
        public override void text(ColumnHeader c, string value)
        {
            c.Text = value;
        }
    }
}
