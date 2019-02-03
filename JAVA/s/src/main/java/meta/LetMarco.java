package meta;

import mb.RangePathsException;

/**
 * let表达式
 * 很特殊，改变目标作用域，而且似乎仅此一例
 * Marco似乎应该默认都是对目标作用域只读的。
 * let k v k v的形式
 * k可为(   ...vs)
 */
public class LetMarco extends WriteMacro {

    @Override
    public ScopeNode exec(ScopeNode scope, BracketExp bracketExp) throws RangePathsException {
        Node<Exp> args=bracketExp.children.rest;
        if(args==null){
            throw bracketExp.exception("不允许空的定义表达式");
        }else if (args.length%2!=0) {
            throw bracketExp.exception("定义表达式的参数非偶数个");
        }else{
            while (args!=null){
                Exp key_exp=args.first;
                args=args.rest;
                Exp value_exp=args.first;
                args=args.rest;
                Object value= ReadMacro.run_read_exp(scope,value_exp);
                scope=bind(scope,key_exp,value);
            }
        }
        return scope;
    }

    /**
     *
     * @param scope 作用域
     * @param key_exp
     * @param value
     * @return
     * @throws RangePathsException
     */
    public static ScopeNode bind(ScopeNode scope,Exp key_exp, Object value) throws RangePathsException {
        if (key_exp instanceof BracketExp){
            if (value==null || (value instanceof Node)){
                Node<Object> vs= (Node<Object>) value;
                Node<Exp> children=key_exp.asBracketExp().children;
                Node<Exp> tmp=children;
                while (tmp!=null){
                    Exp c=tmp.first;
                    tmp=tmp.rest;
                    Object v=null;
                    if (vs!=null){
                        v=vs.first;
                    }
                    if (tmp!=null){
                        //c不是最后一个
                        scope=bind(scope,c,v);
                    }else{
                        //c是最后一个
                        if (c.isIDExp()) {
                            String key =c.asIDExp().value;
                            if (key.length() > 3 && key.startsWith("...")) {
                                //满足剩余匹配
                                scope = ScopeNode.extend(key.substring(3), vs, scope);
                            } else {
                                scope = bind(scope, c, v);
                            }
                        }else{
                            scope=bind(scope,c,v);
                        }
                    }
                    if (vs!=null){
                        vs=vs.rest;
                    }
                }
            }else{
                throw key_exp.exception("绑定值不是列表类型:"+value);
            }
            return scope;
        }else{
            if (key_exp.isIDExp()) {
                return ScopeNode.extend(key_exp.asIDExp().value, value, scope);
            }else{
                throw key_exp.exception("不是合法的供绑定类型");
            }
        }
    }
}
