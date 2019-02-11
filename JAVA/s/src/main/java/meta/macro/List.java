package meta.macro;

import mb.RangePathsException;
import meta.*;

public class List extends ReadMacro {
    private final boolean trans;
    public List(boolean trans){
        this.trans=trans;
    }
    @Override
    public Object exec(ScopeNode scope, BracketExp bracketExp) throws RangePathsException {
        Node<Exp> r_args=bracketExp.r_children;
        Node<Object> list=null;
        while (r_args.length!=1){
            Exp exp=r_args.first;
            r_args=r_args.rest;
            list=Node.extend(
                    trans?run_read_exp_trans(scope,exp):run_read_exp(scope,exp),
                    list
            );
        }
        return list;
    }
}
