package meta.macro;

import mb.RangePathsException;
import meta.*;

public class Map extends ReadMacro {
    private final boolean trans;
    public Map(boolean trans){
        this.trans=trans;
    }
    @Override
    public Object exec(ScopeNode scope, BracketExp bracketExp) throws RangePathsException {
        Node<Exp> r_args=bracketExp.r_children;
        if (r_args.length%2!=1){
            throw bracketExp.exception("需要偶数个参数");
        }else{
            ScopeNode map=null;
            while (r_args.length!=1){
                Exp value_exp=r_args.first;
                r_args=r_args.rest;
                Exp key_exp=r_args.first;
                r_args=r_args.rest;

                String key="";
                if (key_exp.isIDExp()){
                    key=key_exp.asIDExp().value;
                }else if(key_exp.isStringExp()){
                    key=key_exp.asStringExp().value;
                }else{
                    throw key_exp.exception("不是合法的类型");
                }
                map=ScopeNode.extend(
                        key,
                        trans?run_read_exp_trans(scope,value_exp):run_read_exp(scope,value_exp),
                        map
                );
            }
            return map;
        }
    }
}
