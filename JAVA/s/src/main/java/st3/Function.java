package st3;

import mb.RangePathsException;
import s.Node;

/**
 * 函数
 */
public abstract class Function extends ReadMacro {
    @Override
    public Object exec(Node<Object> scope, BracketExp bracketExp) throws RangePathsException {
        Node<Object> rs=null;
        Node<Exp> tmp=bracketExp.r_rest;
        while (tmp!=null){
            rs=Node.extend(run_read_exp(scope,tmp.First()),rs);
            tmp=tmp.Rest();
        }
        try {
            return run(rs);
        }catch (RangePathsException e){
            throw e;
        }catch (Throwable e){
            throw bracketExp.exception(e.getMessage());
        }
    }

    public abstract Object run(Node<Object> args) throws Throwable;
}
