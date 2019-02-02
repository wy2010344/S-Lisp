package meta;

import mb.RangePathsException;

/**
 * 操作作用域的宏
 */
public abstract class WriteMacro {
    public abstract ScopeNode exec(ScopeNode scope,BracketExp bracketExp) throws RangePathsException;
}
