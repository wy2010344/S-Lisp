package st3.macro.util;

import mb.RangePathsException;
import s.Node;
import st3.BracketExp;
import st3.Exp;
import st3.ReadMacro;

public abstract class SingleArg extends DoMacro {
    @Override
    protected Object run(Node<Object> scope, Node<Exp> rest) throws Throwable {
        if (rest==null || rest.Length()!=1){
            throw new Exception("参数必须为1");
        }else{
            return run(scope,rest.First());
        }
    }
    protected abstract Object run(Node<Object> scope,Exp exp) throws RangePathsException;
}
