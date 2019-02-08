package meta.macro;

import meta.Exp;
import meta.LibReadMarco;
import meta.Node;
import meta.ScopeNode;

/**
 * 分支执行，体不是lambda
 * 通过宏执行懒执行
 */
public class If extends LibReadMarco {
    public static Object run_if(ScopeNode scope, Node<Exp> args) throws Throwable  {
        if (args == null || args.length < 2 || args.length > 3) {
            throw new Exception("需要2~3个参数");
        } else {
            Exp condition = args.first;
            args = args.rest;
            Exp truepart = args.first;
            Object o = run_read_exp(scope, condition);
            if (o instanceof Boolean) {
                if ((Boolean) o) {
                    return run_read_exp(scope, truepart);
                } else {
                    if (args == null) {
                        return null;
                    } else {
                        Exp falsepart = args.first;
                        return run_read_exp(scope, falsepart);
                    }
                }
            } else {
                throw new Exception("条件表达式计算结果必须为布尔类型");
            }
        }
    }
    @Override
    protected Object run(ScopeNode scope, Node<Exp> args) throws Throwable {
        return run_if(scope,args);
    }
}
