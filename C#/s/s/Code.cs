using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public class Code
    {
        private int i;
        private String txt;
        private char lineSplit;
        private int row;
        private int col;
        private char c;
        private int maxLength;
        public Code(String txt, char lineSplit)
        {
            this.txt = txt;
            this.lineSplit = lineSplit;
            this.maxLength = txt.Length;
            i = -1;
            row = 0;
            col = 0;
            shift();
        }
        public void shift()
        {
            i++;
            if (i < maxLength)
            {
                c = txt[i];
                if (c == lineSplit)
                {
                    col = 0;
                    row++;
                }
                else
                {
                    col++;
                }
            }
            else
            {
                c = ' ';
            }
        }

        public bool noEnd() {
            return i < maxLength;
        }

        public char current()
        {
            return c;
        }
        public int index()
        {
            return i;
        }

        public String substr(int start, int end)
        {
            return txt.Substring(start, end - start);
        }
        public Location currentLoc()
        {
            return new Location(row, col, i);
        }
    }
}
