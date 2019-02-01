package st3.macro.util;

import mb.RangePathsException;
import st3.Exp;
import st3.Node;
import st3.ScopeNode;

public abstract class SingleArg extends DoMacro {
    @Override
    protected Object run(ScopeNode scope, Node<Exp> rest) throws Throwable {
        if (rest==null || rest.length!=1){
            throw new Exception("参数必须为1");
        }else{
            return run(scope,rest.first);
        }
    }
    protected abstract Object run(ScopeNode scope,Exp exp) throws RangePathsException;
}
