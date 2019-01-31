package st3.macro;

import mb.RangePathsException;
import s.Node;
import st3.Exp;
import st3.IDExp;
import st3.macro.util.SingleArg;

/**
 * 只支持ID作参数？不支持动态计算路径？
 */
public class Load extends SingleArg {
    @Override
    protected Object run(Node<Object> scope, Exp exp) throws RangePathsException {
        if (exp instanceof IDExp){
            String path=((IDExp) exp).token.value;
            
        }else{
            throw exp.exception("需要一个ID类型的表达式");
        }
        return null;
    }
}
