package meta;

import mb.RangePathsException;

public abstract class LibReadMarco extends ReadMacro {
    @Override
    public final Object exec(ScopeNode scope, BracketExp bracketExp) throws RangePathsException {
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
    protected abstract Object run(ScopeNode scope, Node<Exp> args) throws Throwable;
}
