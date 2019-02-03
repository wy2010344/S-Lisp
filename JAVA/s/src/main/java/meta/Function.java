package meta;

import mb.RangePathsException;

/**
 * 函数
 */
public abstract class Function extends ReadMacro {
    @Override
    public final Object exec(ScopeNode scope, BracketExp bracketExp) throws RangePathsException {
        Node<Object> rs=null;
        Node<Exp> tmp=bracketExp.r_rest;
        while (tmp!=null){
            rs=Node.extend(run_read_exp(scope,tmp.first),rs);
            tmp=tmp.rest;
        }
        try {
            return run(rs);
        }catch (RangePathsException e){
            e.addStack("",bracketExp.left.begin,bracketExp.right.begin+bracketExp.right.value.length(),bracketExp.toString());
            throw e;
        }catch (Throwable e){
            throw bracketExp.exception(e.getMessage());
        }
    }

    public abstract Object run(Node<Object> args) throws Throwable;
}
