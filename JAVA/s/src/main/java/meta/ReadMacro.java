package meta;

import mb.RangePathsException;

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
    public abstract Object exec(ScopeNode scope,BracketExp bracketExp) throws RangePathsException;
    /**
     * 读表达式
     * @param exp
     * @param scope
     * @return
     * @throws RangePathsException
     */
    public static Object run_read_exp(ScopeNode scope,Exp exp) throws RangePathsException {
        if (exp instanceof BracketExp){
            BracketExp bracketExp= (BracketExp) exp;
            Exp first_exp=bracketExp.children.first;
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
            return run_atom(scope, exp);
        }
    }

    /**
     * 找ID，未找到按null处理
     * @param scope
     * @param exp
     * @return
     */
    public static Object run_atom(ScopeNode scope, Exp exp) throws RangePathsException {
        if (exp instanceof StringExp){
            return ((StringExp) exp).value;
        }else if(exp instanceof IDExp) {
            try {
                return ScopeNode.find_1st(scope, ((IDExp) exp).value);
            } catch (Exception e) {
                throw exp.exception(e.getMessage());
            }
        }else{
            throw exp.exception("尚未支持的exp");
        }
    }
}
