package meta.macro.util;

import mb.RangePathsException;
import meta.*;

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
