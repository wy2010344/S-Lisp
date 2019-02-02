package meta;

import mb.RangePathsException;

public class ExpLetMarco extends WriteMacro {
    @Override
    public ScopeNode exec(ScopeNode scope, BracketExp bracketExp) throws RangePathsException {
        Node<Exp> args=bracketExp.children.rest;
        if (args==null){
            throw bracketExp.exception("不允许空的定义表达式");
        }else if(args.length%2!=0){
            throw bracketExp.exception("定义表达式的参数非偶数");
        }else{
            while (args!=null){
                Exp key_exp=args.first;
                args=args.rest;
                Exp value_exp=args.first;
                args=args.rest;
                Object value=ReadMacro.run_read_exp(scope,value_exp);
                scope=bind(scope,key_exp,value);
            }
        }
        return scope;
    }
    public static ScopeNode bind(ScopeNode scope,Exp key_exp,Object value) throws RangePathsException {
        if (key_exp instanceof BracketExp){
            if (value==null || value instanceof BracketExp){
                Node<Exp> vs= ((BracketExp)value).children;
                Node<Exp> children=((BracketExp) key_exp).children;
                Node<Exp> tmp=children;
                while (tmp!=null){
                    Exp c=tmp.first;
                    tmp=tmp.rest;
                    Object v=null;
                    if (vs!=null){
                        v=vs.first;
                    }
                    if (tmp!=null){
                        scope=bind(scope,c,v);
                    }else{
                        if (c instanceof IDExp){
                            String key=((IDExp) c).value;
                            if (key.length()>3 && key.startsWith("...")){
                                scope=ScopeNode.extend(key.substring(3),vs,scope);
                            }else{
                                scope=bind(scope,c,v);
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
                throw key_exp.exception("绑定值不是表达式类型"+value.toString());
            }
            return scope;
        }else{
            if (key_exp instanceof IDExp){
                return ScopeNode.extend(((IDExp)key_exp).value,value,scope);
            }else{
                throw key_exp.exception("不是合法的供绑定类型");
            }
        }
    }
}
