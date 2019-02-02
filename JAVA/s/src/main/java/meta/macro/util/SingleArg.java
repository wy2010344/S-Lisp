package meta.macro.util;

import mb.RangePathsException;
import meta.Exp;
import meta.Node;
import meta.ScopeNode;

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
