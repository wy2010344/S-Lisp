package st3;

import mb.RangePathsException;
import s.Node;

/**
 * 只读作用域的宏
 */
public abstract class ReadMacro {
    /**
     * 必要时重写这一个
     * @param scope
     * @param bracketExp
     * @return
     * @throws RangePathsException
     */
    public abstract Object exec(Node<Object> scope,BracketExp bracketExp) throws RangePathsException;
    /**
     * 读表达式
     * @param exp
     * @param scope
     * @return
     * @throws RangePathsException
     */
    public static Object run_read_exp(Node<Object> scope,Exp exp) throws RangePathsException {
        if (exp instanceof BracketExp){
            BracketExp bracketExp= (BracketExp) exp;
            Exp first_exp=bracketExp.children.First();
            Object first= run_read_exp(scope,first_exp);
            if (first instanceof ReadMacro){
                /*
                只解析宏表达式
                */
                return ((ReadMacro) first).exec(scope,bracketExp);
            }else{
                throw first_exp.exception("不是正确只读可执行的宏表达式");
            }
        }else{
            return run_id(scope,(IDExp) exp);
        }
    }
    public static Object run_id(Node<Object> scope,IDExp exp){
        return Node.kvs_find1st(scope,exp.token.value);
    }
}
