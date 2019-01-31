package st3;

import mb.RangePathsException;
import s.Node;

/**
 * 操作作用域的宏
 */
public abstract class WriteMacro {
    public abstract MacroReturn exec(Node<Object> scope,BracketExp bracketExp) throws RangePathsException;
}
