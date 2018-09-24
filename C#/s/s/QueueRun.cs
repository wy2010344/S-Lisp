using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public class QueueRun
    {
        private Node<Object> scope;
        public QueueRun(Node<Object> scope)
        {
            this.scope = scope;
        }
        /// <summary>
        /// 执行
        /// </summary>
        /// <param name="exp"></param>
        /// <returns></returns>
        public Object exec(Exp exp)
        {
            Object ret = null;
            for (Node<Exp> tmp = exp.Children(); tmp != null; tmp = tmp.Rest())
            {
                ret=run(tmp.First());
            }
            return ret;
        }
        Object run(Exp exp)
        {
            if (exp.Exp_type() == Exp.Exp_Type.Exp_Small)
            {
                if (exp.Children() != null)
                {
                    Exp t=exp.Children().First();
                    if (t.Exp_type() == Exp.Exp_Type.Exp_Id && t.Value() == "let")
                    {
                        Node<Exp> rst = exp.Children().Rest();
                        while (rst != null)
                        {
                            Exp key = rst.First();
                            rst = rst.Rest();
                            Exp value = rst.First();
                            rst = rst.Rest();
                            Object vas = interpret(value, scope);
                            scope = match(scope, key, vas);
                        }
                        return null;
                    }
                    else
                    {
                        try
                        {
                            return interpret(exp, scope);
                        }
                        catch (Exception ex)
                        {
                            Console.WriteLine(exp.ToString());
                            throw ex;
                        }
                    }
                }else
                {
                    return interpret(exp, scope);
                }
            }
            else
            {
                return interpret(exp, scope);
            }
        }
        String getPath(Node<Object> scope)
        {
            String path = null;
            Node<Object> tmp = scope;
            while (tmp != null && path == null)
            {
                String key = tmp.First() as String;
                tmp = tmp.Rest();
                if (key == "pathOf")
                {
                    if (tmp.First() is Function)
                    {
                        Function pathOf = tmp.First() as Function;
                        path = pathOf.exec(null) as String;
                    }
                }
                tmp = tmp.Rest();
            }
            return path;
        }
        LocationException match_Exception(Node<Object> scope, String msg, Location loc)
        {
            LocationException lox = new LocationException(loc, getPath(scope) + ":\t" + msg);
            return lox;
        }
        Node<Object> when_normal_match(Node<Object> scope, String id, Object Value,Location loc)
        {
            if (id.IndexOf('.') < 0)
            {
                return Node<object>.extend(id, Node<object>.extend(Value, scope));
            }
            else
            {
                throw match_Exception(scope, id + "不是合法的id声明，不允许包含.",loc);
            }
        }

        static bool isWait(Exp exp)
        {
            if (exp.Exp_type() == Exp.Exp_Type.Exp_Id)
            {
                return exp.Value().StartsWith("...");
            }
            else
            {
                return false;
            }
        }
        Node<Object> when_bracket_match(Node<Object> scope, Node<Exp> keys, Node<Object> values)
        {
            while (keys != null)
            {
                Exp key = keys.First();
                Object value = null;
                if (keys.Rest() == null && isWait(key))
                {
                    String subvk = key.Value().Substring(3);
                    scope = when_normal_match(scope, subvk, values,key.Loc());
                }
                else
                {
                    if (values != null)
                    {
                        value = values.First();
                        values = values.Rest();
                    }
                    scope = match(scope, key, value);
                }
                keys = keys.Rest();
            }
            return scope;
        }
        Node<Object> match(Node<Object> scope, Exp key, Object value)
        {
            if (key.Exp_type() == Exp.Exp_Type.Exp_Id)
            {
                String id = key.Value();
                scope = when_normal_match(scope, id, value,key.Loc());
            }
            else
            {
                if (key.Exp_type() == Exp.Exp_Type.Exp_Small)
                {
                    scope = when_bracket_match(scope, key.Children(), (Node<Object>)value);
                }
                else
                {
                    throw match_Exception(scope,key.Value() + "不是合法匹配类型",key.Loc());
                }
            }
            return scope;
        }
        Node<Object> calNode(Node<Exp> list, Node<Object> scope)
        {
            Node<Object> r = null;
            for (Node<Exp> x = list; x != null; x = x.Rest())
            {
                r = Node<object>.extend(
                    interpret(x.First(),scope),
                    r
                );
            }
            return r;
        }
        LocationException error_throw(String msg, Exp exp,Node<Object> scope,Node<Object> children)
        {
            LocationException locexp=new LocationException(exp.Loc(),  msg + ":\r\n"+children.ToString()+"\r\n"+ exp.Children().First() + "\r\n" + exp.Children().ToString());
            locexp.addStack(getPath(scope),exp.Loc(),exp.ToString());
            return locexp;
        }
        Object interpret(Exp exp, Node<Object> scope)
        {
            if (exp.Exp_type() == Exp.Exp_Type.Exp_Small)
            {
                Node<Object> children = calNode(exp.R_children(), scope);
                Object o=children.First();
                
                if (o is Function)
                {
                    try
                    {
                        return ((Function)o).exec(children.Rest());
                    }
                    catch (LocationException lex)
                    {
                        lex.addStack(getPath(scope), exp.Loc(), exp.ToString());
                        throw lex;
                    }
                    catch (Exception ex)
                    {
                        throw error_throw("函数执行内部错误" + ex.Message, exp, scope, children);
                    }
                }
                else
                {
                    if (o == null)
                    {
                        throw error_throw("未找到函数定义", exp, scope, children);
                    }
                    else
                    {
                        throw error_throw("不是函数", exp, scope, children);
                    }
                }
            }
            else if (exp.Exp_type() == Exp.Exp_Type.Exp_Medium)
            {
                return calNode(exp.R_children(), scope);
            }
            else if (exp.Exp_type() == Exp.Exp_Type.Exp_Large)
            {
                return new UserFunction(exp, scope);
            }
            else if (exp.Exp_type() == Exp.Exp_Type.Exp_String)
            {
                return exp.Value();
            }
            else if (exp.Exp_type() == Exp.Exp_Type.Exp_Int)
            {
                return int.Parse(exp.Value());
            }
            else if (exp.Exp_type() == Exp.Exp_Type.Exp_Id)
            {
                Node<String> paths = exp.KVS_paths();
                if (paths == null)
                {
                    throw match_Exception(scope, exp.Value() + "不是合法的ID类型:\t"+exp.ToString(), exp.Loc());
                }
                else
                {
                    Node<Object> c_scope = scope;
                    Object value = null;
                    while (paths != null)
                    {
                        String key = paths.First();
                        value = Node<Object>.kvs_find1st(c_scope, key);
                        paths = paths.Rest();
                        if (paths != null)
                        {
                            if (value==null || value is Node<Object>)
                            {
                                c_scope = value as Node<Object>;
                            }
                            else
                            {
                                throw match_Exception(scope, "计算"+paths.ToString()+"，其中"+value + "不是kvs类型:\t"+exp.ToString(), exp.Loc());
                            }
                        }
                    }
                    return value;
                }
            }else
            {
                return null;
            }
        }
    }
}
