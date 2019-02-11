package meta;

import mb.RangePathsException;

/**
 * 用户自定义宏。
 */
public class UserReadMacro extends ReadMacro {
    private final ScopeNode parentScope;
    private final Node<Exp> exps;
    private final IDExp name_of_this;
    private final Exp name_of_args;
    private final IDExp name_of_scope;
    public UserReadMacro(
            ScopeNode scope,
            IDExp name_of_scope,
            Exp name_of_args,
            IDExp name_of_this,
            Node<Exp> bodys
    ){
        this.parentScope=scope;
        this.name_of_scope=name_of_scope;
        this.name_of_args=name_of_args;
        this.name_of_this=name_of_this;
        this.exps=bodys;
    }

    /**
     * 读写表达式，写在顶层检查
     * @param scope
     * @param exps
     * @return
     * @throws RangePathsException
     */
    public static Object run(ScopeNode scope,Node<Exp> exps) throws RangePathsException {
        QueueRun qr=new QueueRun(scope);
        return qr.run(exps);
    }
    @Override
    public Object exec(ScopeNode targetScope, BracketExp bracketExp) throws RangePathsException {
        ScopeNode scope=parentScope;
        if (name_of_scope!=null) {
            scope = ScopeNode.extend(name_of_scope.value, targetScope, scope);
        }
        if (name_of_args!=null){
            scope= ExpLetMarco.bind(scope,name_of_args,bracketExp.children.rest);
        }
        if (name_of_this!=null){
            scope=ScopeNode.extend(name_of_this.value,this,scope);
        }
        return run(scope,exps);
    }

}
