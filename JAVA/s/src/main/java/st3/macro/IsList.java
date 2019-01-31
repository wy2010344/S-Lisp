package st3.macro;

import mb.RangePathsException;
import s.Node;
import st3.*;
import st3.macro.util.SingleArg;

/**
 * 判断某个参数是否是列表宏。
 */
public class IsList extends SingleArg {
    @Override
    protected Object run(Node<Object> scope, Exp exp){
        return (exp instanceof BracketExp);
    }
}
