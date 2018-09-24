using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace s
{
    public class Util
    {
        static char[] trans_map_char={'n','\n','r','\r','t','\t'};
        static Char kvs_find1st(char[] kvs, char c,ref bool unfind)
        {
            char x='\0';
            int i = 0;
            while(i<kvs.Length && unfind)
            {
                char key = kvs[i];
                i++;
                char value = kvs[i];
                if (key == c)
                {
                    x = value;
                    unfind = false;
                }
                i++;
            }
            return x;
        }
        public static String stringFromEscape(String str, char end, int trans_time)
        {
            char[] cs = new char[str.Length - trans_time];
            int i = 0;
            int k = 0;
            while (i < str.Length)
            {
                char c = str[i];
                if (c == '\\')
                {
                    i++;
                    c = str[i];
                    if (c == '\\')
                    {
                        cs[k] = '\\';
                    }else
                    if (c == end)
                    {
                        cs[k] = end;
                    }
                    else
                    {
                        bool unfind = true;
                        char x = kvs_find1st(trans_map_char, c,ref unfind);
                        if (unfind)
                        {
                            throw new Exception("非法转义" + c + "在字符串:" + str);
                        }
                        else
                        {
                            cs[k] = x;
                        }
                    }
                }
                else
                {
                    cs[k] = c;
                }
                k++;
                i++;
            }

            return new String(cs);
        }
        public static String stringToEscape(String s, char start, char end, char[] kvs_map)
        {
            StringBuilder sb = new StringBuilder();
            int i=0;
            sb.Append(start);
            while(i<s.Length)
            {
                char c = s[i];
                if (c == '\\')
                {
                    sb.Append("\\\\");
                }
                else if (c == end)
                {
                    sb.Append("\\").Append(end);
                }
                else
                {
                    if (kvs_map != null)
                    {
                        bool unfind = true;
                        char x = kvs_find1st(kvs_map, c, ref unfind);
                        if (unfind)
                        {
                            sb.Append(c);
                        }
                        else
                        {
                            sb.Append("\\").Append(x);
                        }
                    }
                    else
                    {
                        sb.Append(c);
                    }
                }
                i++;
            }
            sb.Append(end);
            return sb.ToString();
        }

        /**
         * 如果base_path是路径，则以/结尾，是文件，则非/结束
         */
        public static string absolute_from_relative(String base_path, String relative_path)
        {
            if (relative_path.StartsWith("."))
            {
                String[] nodes = base_path.Split('/');
                String[] names = relative_path.Split('/');
                List<String> list = new List<string>(nodes);
                list.RemoveAt(list.Count - 1);//移除最后一个
                for (int i = 0; i < names.Length; i++)
                {
                    String n = names[i];
                    if (n == ".")
                    {
                    }
                    else if (n == "..")
                    {
                        list.RemoveAt(list.Count - 1);
                    }
                    else if (n == "")
                    {
                    }
                    else
                    {
                        list.Add(n);
                    }
                }
                return String.Join("/", list.ToArray());
            }
            else
            {
                return relative_path;
            }
        }
        /// <summary>
        /// 可执行exe路径
        /// </summary>
        /// <returns></returns>
        public static string exe_path(String relative_path)
        {
            return absolute_from_relative(AppDomain.CurrentDomain.BaseDirectory.Replace('\\','/'),relative_path);  
        }
        /// <summary>
        /// 文件路径
        /// </summary>
        /// <returns></returns>
        public static string file_path(String relative_path)
        {
            return absolute_from_relative(Environment.CurrentDirectory.Replace('\\', '/'), relative_path);
        }
        public static String readTxt(String path, char linesplit, Encoding encode)
        {
            StreamReader sr = new StreamReader(path, encode);
            StringBuilder sb = new StringBuilder();
            string line;
            while ((line = sr.ReadLine()) != null)
            {
                sb.Append(line).Append(linesplit);
            }
            sr.Close();
            return sb.ToString();
        }
        public static void writeTxt(String path, String content)
        {
            System.IO.File.WriteAllText(path, content, new UTF8Encoding(false));
        }
        public static void logException(Exception e)
        {
            Console.WriteLine(e.ToString());
        }
    }
}
