using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace gui
{
    public partial class MainForm : Form
    {
        public MainForm(String[] args)
        {
            InitializeComponent();
            if (args.Length == 0)
            {
                new WinFormMVE(this);
            }
        }
    }

}