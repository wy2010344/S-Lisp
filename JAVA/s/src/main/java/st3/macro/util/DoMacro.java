package st3.macro.util;

import mb.RangePathsException;
import s.Node;
import st3.BracketExp;
import st3.Exp;
import st3.ReadMacro;

public abstract class DoMacro extends ReadMacro {
    @Override
    public Object exec(Node<Object> scope, BracketExp bracketExp) throws RangePathsException {
        try {
            return run(
                scope,
                bracketExp.children.Rest()
            );
        }catch (RangePathsException e){
            throw e;
        }catch (Throwable e){
            throw bracketExp.exception(e.getMessage());
        }
    }
    protected abstract Object run(Node<Object> scope, Node<Exp> rest) throws Throwable;
}
