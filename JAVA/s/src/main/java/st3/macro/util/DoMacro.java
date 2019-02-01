package st3.macro.util;

import mb.RangePathsException;
import st3.*;

public abstract class DoMacro extends ReadMacro {
    @Override
    public Object exec(ScopeNode scope, BracketExp bracketExp) throws RangePathsException {
        try {
            return run(
                scope,
                bracketExp.children.rest
            );
        }catch (RangePathsException e){
            throw e;
        }catch (Throwable e){
            throw bracketExp.exception(e.getMessage());
        }
    }
    protected abstract Object run(ScopeNode scope, Node<Exp> rest) throws Throwable;
}
