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
    @Override
    protected Object run(ScopeNode scope, Node<Exp> rest) throws Throwable {
        if (rest==null||rest.length<2||rest.length>3){
            throw new Exception("需要2~3个参数");
        }else {
            Exp condition = rest.first;
            rest = rest.rest;
            Exp truepart = rest.first;
            Object o = run_read_exp(scope, condition);
            if (o instanceof Boolean) {
                if ((Boolean)o){
                    return run_read_exp(scope,truepart);
                }else{
                    if (rest == null) {
                        return null;
                    }else{
                        Exp falsepart = rest.first;
                        return run_read_exp(scope,falsepart);
                    }
                }
            } else {
                throw new Exception("条件表达式计算结果必须为布尔类型");
            }
        }
    }
}