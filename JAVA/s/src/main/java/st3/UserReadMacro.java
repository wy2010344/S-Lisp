package st3;

import mb.RangePathsException;
import s.Node;
import st3.macro.GetValue;

/**
 * 用户自定义宏。
 */
public class UserReadMacro extends ReadMacro {
    private final Node<Object> parentScope;
    private final Node<Exp> exps;
    private final IDExp name_of_this;
    private final Exp name_of_args;
    private final IDExp name_of_scope;
    private final Exp defDxp;
    public UserReadMacro(
            Exp defExp,
            Node<Object> scope,
            IDExp name_of_scope,
            Exp name_of_args,
            IDExp name_of_this,
            Node<Exp> bodys
    ){
        this.defDxp=defExp;
        this.parentScope=scope;
        this.name_of_scope=name_of_scope;
        this.name_of_args=name_of_args;
        this.name_of_this=name_of_this;
        this.exps=bodys;
    }

    /**
     * 读写表达式，写在顶层检查
     * @param exps
     * @param scope
     * @return
     * @throws RangePathsException
     */
    public static Object run(Node<Exp> exps,Node<Object> scope) throws RangePathsException {
        Node<Exp> tmp=exps;
        Object o=null;
        while (tmp!=null){
            Exp exp=tmp.First();
            tmp=tmp.Rest();
            if (exp instanceof BracketExp){
                BracketExp bracketExp= (BracketExp) exp;
                Exp first_exp=bracketExp.children.First();
                Object first= run_read_exp(scope,first_exp);
                if (first instanceof ReadMacro){
                    /*只读作用域宏*/
                    o=((ReadMacro) first).exec(
                            scope,
                            bracketExp
                    );
                }else if(first instanceof WriteMacro){
                    /*读写作用域宏*/
                    MacroReturn mr=((WriteMacro) first).exec(
                            scope,
                            bracketExp
                    );
                    scope=mr.scope;
                    o=mr.value;
                }else{
                    throw first_exp.exception("不是read或write宏");
                }
            }else{
                o=run_id(scope,(IDExp) exp);
            }
        }
        return o;
    }
    @Override
    public Object exec(Node<Object> targetScope, BracketExp bracketExp) throws RangePathsException {
        Node<Object> scope=parentScope;
        if (name_of_scope!=null) {
            scope = Node.kvs_extend(GetValue.of(name_of_scope), targetScope, scope);
        }
        if (name_of_args!=null){
            scope= LetMarco.bind(scope,name_of_args,bracketExp.children.Rest());
        }
        if (name_of_this!=null){
            scope=Node.kvs_extend(GetValue.of(name_of_this),this,scope);
        }
        return run(exps,scope);
    }

}
