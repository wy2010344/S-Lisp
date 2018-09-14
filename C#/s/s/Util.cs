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
        /// <summary>
        /// 可执行exe路径
        /// </summary>
        /// <returns></returns>
        public static string exe_path()
        {
            return AppDomain.CurrentDomain.BaseDirectory;  
        }
        /// <summary>
        /// 文件路径
        /// </summary>
        /// <returns></returns>
        public static string file_path()
        {
            return Environment.CurrentDirectory;
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
        public static void logException(Exception e)
        {
            Console.WriteLine(e.ToString());
        }
    }
}
