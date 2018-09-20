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
        private s.Function destroy;
        public MainForm()
        {
            InitializeComponent();
            s.S slib = new s.S('\n');
            slib.loadLib("./s/lib/index.lisp");
            slib.addDef("cache",new s.library.Cache());
            slib.addDef("build-element", new BuildElement());
            slib.loadLib("./s/lib/mve/index.lisp", "mve",true);
            s.Function fun=slib.run("./s/index/index.lisp") as s.Function;
            s.Node<Object> o = fun.exec(null) as s.Node<Object>;

            Control els = (s.Node<Object>.kvs_find1st(o, "getElement") as s.Function).exec(null) as Control;
            this.Controls.Add(els);
            (s.Node<Object>.kvs_find1st(o, "init") as s.Function).exec(null);
            this.destroy = (s.Node<Object>.kvs_find1st(o, "destroy") as s.Function);
        }

        private void MainForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            this.destroy.exec(null);
        }
    }

    class SEventHandle
    {
        public SEventHandle(s.Function fun)
        {
            this.fun = fun;
        }
        s.Function fun;

        public void run(object sender, EventArgs e)
        {
            this.fun.exec(null);
        }
    }
    class BuildElement : s.Function
    {
        public override object exec(s.Node<object> args)
        {
            /*inits destroy watch k mve*/
            s.Node<Object> ext = args.Rest();
            s.Node<Object> inits = ext.First() as s.Node<Object>;
            ext = ext.Rest();
            s.Node<Object> destroys = ext.First() as s.Node<Object>;
            /*watch k mve*/
            ext = ext.Rest();


            s.Function watch = ext.First() as s.Function;
            s.Function k = ext.Rest().First() as s.Function;

            /*type params id*/
            s.Node<Object> es = args.First() as s.Node<Object>;
            String type = es.First() as String;
            es = es.Rest();
            s.Node<Object> ps = es.First() as s.Node<Object>;
            es = es.Rest();
            String id = null;
            if (es != null)
            {
                id = es.First() as String;
            }

            Control div=null;
            if (type == "div")
            {
                Panel p = new Panel();

                s.Node<Object> children = s.Node<Object>.kvs_find1st(ps, "children") as s.Node<Object>;
                for (s.Node<Object> tmp = children; tmp != null; tmp = tmp.Rest())
                {
                    s.Node<Object> child = this.exec(
                        s.Node<Object>.extends(new Object[]{ tmp.First(), inits, destroys },ext)
                     ) as s.Node<Object>;
                    Control c = child.First() as Control;
                    child = child.Rest();
                    inits = child.First() as s.Node<Object>;
                    child = child.Rest();
                    destroys = child.First() as s.Node<Object>;
                    p.Controls.Add(c);
                }
                div=p;
            }
            else if (type == "button")
            {
                Button b = new Button();
                b.Text = s.Node<Object>.kvs_find1st(ps, "text") as String;
                s.Function fun = s.Node<Object>.kvs_find1st(ps, "click") as s.Function;
                if (fun != null)
                {
                    b.Click += new EventHandler(new SEventHandle(fun).run);
                }
                Object left=s.Node<Object>.kvs_find1st(ps,"left");
                if(left!=null){
                    b.Left = (int)(left);
                }
                div=b;
            }
            return s.Node<Object>.list(div,inits,destroys);
        }

        public override Function_Type Function_type()
        {
            return Function_Type.Fun_BuildIn;
        }

        public override string ToString()
        {
            return "build-element";
        }
    }
}