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

        Node<Object> letSmallMatch(Exp small, Object v, Node<Object> scope)
        {
            Node<Exp> ks = small.Children();
            if (v == null || v is Node<Object>)
            {
                Node<Object> vs = v as Node<Object>;
                while (ks != null)
                {
                    v = null;
                    if (vs != null)
                    {
                        v = vs.First();
                    }
                    Exp k = ks.First();
                    ks = ks.Rest();

                    if (k.Exp_type() == Exp.Exp_Type.Exp_LetId)
                    {
                        scope = Node<Object>.kvs_extend(k.Value(), v, scope);
                    }
                    else if (k.Exp_type() == Exp.Exp_Type.Exp_LetSmall)
                    {
                        scope = letSmallMatch(k, v, scope);
                    }
                    else if (k.Exp_type() == Exp.Exp_Type.Exp_LetRest)
                    {
                        scope = Node<Object>.kvs_extend(k.Value(), vs, scope);
                    }
                    else
                    {
                        throw new LocationException(k.Loc(),"异常匹配" + k.ToString());
                    }
                    if (vs != null)
                    {
                        vs = vs.Rest();
                    }
                }
                return scope;
            }
            else
            {
                throw new LocationException(small.Loc(), v.ToString() + "不是合法的List类型，无法参与元组匹配:" + small.ToString());
            }
        }
        Object run(Exp exp)
        {
            if (exp.Exp_type() == Exp.Exp_Type.Exp_Let)
            {
                Node<Exp> cs = exp.Children().Rest();
                while (cs != null)
                {
                    Exp key = cs.First();
                    cs=cs.Rest();
                    Object value = interpret(cs.First(), scope);
                    cs = cs.Rest();
                    if (key.Exp_type() == Exp.Exp_Type.Exp_LetId)
                    {
                        scope = Node<Object>.kvs_extend(key.Value(), value, scope);
                    }
                    else if (key.Exp_type() == Exp.Exp_Type.Exp_LetSmall)
                    {
                        scope = letSmallMatch(key, value, scope);
                    }
                }
                return null;
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
                return exp.Int_Value();
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
