using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace gui
{
    class WinFormMVE
    {
        private MainForm form;

        private mve.MVE target;
        public WinFormMVE(MainForm form)
        {
            this.form = form;

            Encoding encoding = new UTF8Encoding(false);
            char line_split = '\n';
            s.S slib = new s.S(line_split, encoding);
            slib.addDef("read", new s.library.Read(line_split, encoding));
            slib.addDef("write", new s.library.Write(encoding));
            slib.addDef("cache", new s.library.Cache());

            target = new gui.mve.MVE(slib,form, s.Util.exe_path("s/index/index.lisp"));
            form.Controls.Add(target.element);
            target.element.Dock=DockStyle.Fill;
            target.init.exec(null);

            form.ResizeEnd+=new EventHandler(form_ResizeEnd);
            form_ResizeEnd(null, null);
            form.FormClosing += new System.Windows.Forms.FormClosingEventHandler(form_FormClosing);
        }

        void form_ResizeEnd(object sender, EventArgs e)
        {
            if (target.element is ScrollableControl)
            {
                ScrollableControl sc = target.element as ScrollableControl;
                int ws = sc.AutoScrollMargin.Width;
                int w=sc.DisplayRectangle.Size.Width;
            }
            int w1 = target.element.Width;
            /*
            target.width.exec(s.Node<Object>.extend(target.element.ClientSize.Width, null));
            target.height.exec(s.Node<Object>.extend(target.element.ClientSize.Height, null));
            */
        }

        void form_FormClosing(object sender, System.Windows.Forms.FormClosingEventArgs e)
        {
            target.destroy.exec(null);
        }
    }
}
