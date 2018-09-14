using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public class Location
    {
        private int row, col, i;
        public Location(int row, int col, int i)
        {
            this.row = row;
            this.col = col;
            this.i = i;
        }
        public int Row()
        {
            return row;
        }
        public int Col()
        {
            return col;
        }
        public int Index()
        {
            return i;
        }

        public override string ToString()
        {
            return "位置" + (row+1) + "行" + (col+1) + "列，第" + (i+1) + "个字符串";
        }
    }
}
