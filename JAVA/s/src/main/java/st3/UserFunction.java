package st3;

import s.Node;

public class UserFunction extends Function {
    private final Node<Object> parentScope;
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
            Node<Object> parentScope,
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
        Node<Object> scope=parentScope;
        if (argnames!=null) {
            scope = LetMarco.bind(scope, argnames, args);
        }
        if (thisname!=null){
            scope=Node.kvs_extend(thisname.token.value,this,scope);
        }
        return UserReadMacro.run(exps,scope);
    }
}
