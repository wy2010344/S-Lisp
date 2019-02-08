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
     * 计算直到剩余项
     * @param scope
     * @param exp
     * @param length
     * @return
     * @throws RangePathsException
     */
    public static Node<Object> calculate_rest(ScopeNode scope,BracketExp exp,int length) throws RangePathsException {
        Node<Object> rs=null;
        Node<Exp> tmp=exp.r_children;
        if (tmp!=null) {
            while (tmp.length!=length) {
                rs = Node.extend(run_read_exp(scope, tmp.first), rs);
                tmp = tmp.rest;
            }
        }
        return rs;
    }

    /**
     * 读表达式
     * @param exp
     * @param scope
     * @return
     * @throws RangePathsException
     */
    public static Object run_read_exp(ScopeNode scope,Exp exp) throws RangePathsException {
        if (exp instanceof BracketExp){
            return run_bracket_exp(scope,exp);
        }else{
            return run_atom(scope, exp);
        }
    }

    /**
     * id默认转译为字符串
     * @param scope
     * @param exp
     * @return
     * @throws RangePathsException
     */
    public static Object run_read_exp_trans(ScopeNode scope,Exp exp) throws RangePathsException {
        if (exp instanceof BracketExp){
            return run_bracket_exp(scope,exp);
        }else{
            return run_atom_trans(exp);
        }
    }
    /**
     * 括号表达式
     * @param scope
     * @param exp
     * @return
     * @throws RangePathsException
     */
    public static  Object run_bracket_exp(ScopeNode scope,Exp exp) throws RangePathsException {
        BracketExp bracketExp= exp.asBracketExp();
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
    }

    /**
     * ID和字符串，都识别为字符串
     * @param exp
     * @return
     * @throws RangePathsException
     */
    public static String run_atom_trans(Exp exp) throws RangePathsException {
        if(exp.isIDExp()){
            return exp.asIDExp().value;
        }else if (exp.isStringExp()){
            return exp.asStringExp().value;
        }else{
            throw exp.exception("尚不知道的类型");
        }
    }
    /**
     * 找ID，未找到按null处理
     * @param scope
     * @param exp
     * @return
     */
    public static Object run_atom(ScopeNode scope, Exp exp) throws RangePathsException {
        if (exp.isStringExp()){
            return exp.asStringExp().value;
        }else if(exp.isIDExp()) {
            try {
                return ScopeNode.find_1st(scope, exp.asIDExp().value);
            } catch (Exception e) {
                throw exp.exception(e.getMessage());
            }
        }else{
            throw exp.exception("尚未支持的exp");
        }
    }
}
