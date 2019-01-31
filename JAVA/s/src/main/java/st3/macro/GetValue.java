package st3.macro;

import mb.RangePathsException;
import s.Node;
import st3.Exp;
import st3.IDExp;
import st3.macro.util.SingleArg;

/**
 * 获得字符串
 */
public class GetValue extends SingleArg {

    @Override
    protected Object run(Node<Object> scope, Exp exp) throws RangePathsException {
        if (exp instanceof IDExp){
            return of((IDExp) exp);
        }else{
            throw exp.exception("需要是ID类型");
        }
    }
    public static String of(IDExp exp){
        return exp.token.value;
    }
}
