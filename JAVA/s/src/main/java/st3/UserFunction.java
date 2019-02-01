package st3;

public class UserFunction extends Function {
    private final ScopeNode parentScope;
    private final Node<Exp> exps;
    private final BracketExp argnames;
    private final IDExp thisname;

    /**
     *
     * @param parentScope
     * @param argnames
     * @param thisname
     * @param exps
     */
    public UserFunction(
            ScopeNode parentScope,
            BracketExp argnames,
            IDExp thisname,
            Node<Exp> exps
    ){
        this.parentScope=parentScope;
        this.argnames=argnames;
        this.exps=exps;
        this.thisname=thisname;
    }
    @Override
    public Object run(Node<Object> args) throws Throwable {
        ScopeNode scope=parentScope;
        if (argnames!=null) {
            scope = LetMarco.bind(scope, argnames, args);
        }
        if (thisname!=null){
            scope=ScopeNode.extend(thisname.value,this,scope);
        }
        return UserReadMacro.run(scope,exps);
    }
}
