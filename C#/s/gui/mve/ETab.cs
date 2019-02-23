using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace gui.mve
{
    
    class ETab:EC<TabControl>
    {
        s.Function build_children;
        public ETab(U u) : base(u)
        {
            build_children = u.build_children_factory.exec(s.Node<Object>.list(
                "key","pages",
                "appendChild",new TabAppendChild(),
                "removeChild",new TabRemoveChild(),
                "insertChildBefore",new TabInsertBefore()
                )) as s.Function;
        }
        TabReplaceChild tabReplaceChild = new TabReplaceChild();

        public override CommonReturn<TabControl> run(s.Node<object> x, s.Node<object> o)
        {
            TabControl tab = new TabControl();
            s.Node<Object> obj = u.exec_buildChild(build_children, tab, tabReplaceChild, x, o);
            return new CommonReturn<TabControl>(tab, getK(obj), getInits(obj), getDestroys(obj));
        }
    }
    class TabAppendChild : EAppendChild<TabControl, TabPage>
    {
        public override void run(TabControl pel, TabPage el)
        {
            pel.TabPages.Add(el);
        }
    }
    class TabRemoveChild : ERemoveChild<TabControl, TabPage>
    {
        public override void run(TabControl pel, TabPage el)
        {
            pel.TabPages.Remove(el);
        }
    }
    class TabInsertBefore : EInsertChildBefore<TabControl, TabPage>
    {
        public override int getIndex(TabControl pel, TabPage old_el)
        {
            return pel.TabPages.IndexOf(old_el);
        }
        public override void insert(TabControl pel, TabPage new_el, int index)
        {
            pel.TabPages.Insert(index, new_el);
        }
    }
    class TabReplaceChild : EReplaceChild<TabPage>
    {
        public override void replace(object e, TabPage old_el, TabPage new_el)
        {
            TabControl pel=old_el.Parent as TabControl;
            int index = pel.TabPages.IndexOf(old_el);
            if (index > -1)
            {
                pel.TabPages.Insert(index, new_el);
                pel.TabPages.Remove(old_el);
            }
        }
    }
}
