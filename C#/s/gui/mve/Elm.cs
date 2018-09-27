using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace gui.mve
{
    /// <summary>
    /// 类的继承关系大致参照
    /// https://docs.microsoft.com/zh-cn/dotnet/api/system.windows.forms.buttonbase?view=netframework-4.7.2
    /// 其中中间没用的类，大概暂时省略掉，比如ScrollPanel
    /// 一些字符串、数字的简单属性可以用反射解决，其实可以进一步判断与转换类型，支持bool等。
    /// </summary>
    public abstract class Elm
    {
        protected void set_real_control(Object r_c)
        {
            this.real_control=r_c;
        }
        private Object real_control;
        public Object Real_Control()
        {
            return real_control;
        }
        public virtual Object attr(String key,s.Node<Object> args)
        {
            Type Ts = Real_Control().GetType();
            System.Reflection.PropertyInfo info=Ts.GetProperty(key);
            if (info != null)
            {
                if (args == null)
                {
                    Object o=info.GetValue(Real_Control(), null);
                    if (o is Color)
                    {
                        return System.Drawing.ColorTranslator.ToHtml((Color)o);
                    }
                    else if (o is bool)
                    {
                        if ((bool)o)
                        {
                            return "true";
                        }
                        else
                        {
                            return "false";
                        }
                    }
                }
                else
                {
                    Object o = args.First();
                    if (info.PropertyType == typeof(bool))
                    {
                        if ("true" == o as String)
                        {
                            o = true;
                        }
                        else
                        {
                            o = false;
                        }
                    }else if(info.PropertyType==typeof(Color))
                    {
                        String color = args.First() as String;
                        if (color.StartsWith("#"))
                        {
                            o = System.Drawing.ColorTranslator.FromHtml(color);
                        }
                        else if (color.StartsWith("rgba"))
                        {
                            int start = color.IndexOf("(") + 1;
                            int end = color.IndexOf(")");
                            color = color.Substring(start, end - start);
                            String[] cs = color.Split(',');
                            if (cs.Length == 4)
                            {

                                o = Color.FromArgb((int)(double.Parse(cs[3]) * 255 / 100), int.Parse(cs[0]), int.Parse(cs[1]), int.Parse(cs[2]));
                            }
                            else if (cs.Length == 3)
                            {
                                o = Color.FromArgb(int.Parse(cs[0]), int.Parse(cs[1]), int.Parse(cs[2]));
                            }
                        }
                    }
                    else
                    {
                        o = Convert.ChangeType(o, info.PropertyType);
                    }
                    info.SetValue(Real_Control(), o, null);
                }
            }
            else
            {
                Console.WriteLine("未找到该属性");
            }
            return null;
        }
        public virtual Object text(s.Node<Object> args)
        {
            return null;
        }
        public virtual Object value(s.Node<Object> args)
        {
            return null;
        }
        public virtual void appendChild(Elm el)
        {
        }
        public virtual void removeChild(Elm el)
        {
        }
        public static void shareReplaceWith(Control old_el, Control new_el)
        {
        }
        public virtual  void replaceWith(Elm el)
        {
        }
        public virtual void action(String key, s.Function fun)
        {
        }

        public static bool resolveBool(String b)
        {
            if (b == "true")
            {
                return true;
            }
            else
            {
                return false;
            }
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
            try
            {
                this.fun.exec(null);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.ToString());
            }
        }
    }
}
