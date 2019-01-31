package st3;

import mb.RangePathsException;
import s.Node;
import st3.*;

/**
 * let表达式
 * 很特殊，改变目标作用域，而且似乎仅此一例
 * Marco似乎应该默认都是对目标作用域只读的。
 * let k v k v的形式
 * k可为(   ...vs)
 */
public class LetMarco extends WriteMacro {

    @Override
    public MacroReturn exec(Node<Object> scope, BracketExp bracketExp) throws RangePathsException {
        Node<Exp> args=bracketExp.children.Rest();
        if(args==null){
            throw bracketExp.exception("不允许空的定义表达式");
        }else if (args.Length()%2!=0) {
            throw bracketExp.exception("定义表达式的参数非偶数个");
        }else{
            while (args!=null){
                Exp key_exp=args.First();
                args=args.Rest();
                Exp value_exp=args.First();
                args=args.Rest();
                Object value= ReadMacro.run_read_exp(scope,value_exp);
                scope=bind(scope,key_exp,value);
            }
        }
        return new MacroReturn(scope,null);
    }

    /**
     *
     * @param scope 作用域
     * @param key_exp
     * @param value
     * @return 作用域
     * @throws RangePathsException
     */
    public static Node<Object> bind(Node<Object> scope,Exp key_exp, Object value) throws RangePathsException {
        if (key_exp instanceof BracketExp){
            if (value==null || (value instanceof Node)){
                Node<Object> vs= (Node<Object>) value;
                Node<Exp> children=((BracketExp) key_exp).children;
                Node<Exp> tmp=children;
                while (tmp!=null){
                    Exp c=tmp.First();
                    tmp=tmp.Rest();
                    Object v=null;
                    if (vs!=null){
                        v=vs.First();
                    }
                    if (tmp!=null){
                        //c不是最后一个
                        scope=bind(scope,c,v);
                    }else{
                        //c是最后一个
                        if (c instanceof IDExp) {
                            String key = ((IDExp) c).token.value;
                            if (key.length() > 3 && key.startsWith("...")) {
                                //满足剩余匹配
                                scope = Node.kvs_extend(key.substring(3), vs, scope);
                            } else {
                                scope = bind(scope, c, v);
                            }
                        }else{
                            scope=bind(scope,c,v);
                        }
                    }
                    if (vs!=null){
                        vs=vs.Rest();
                    }
                }
            }else{
                throw key_exp.exception("绑定值不是列表类型:"+value);
            }
            return scope;
        }else{
            return Node.kvs_extend(((IDExp)key_exp).token.value,value,scope);
        }
    }
}
