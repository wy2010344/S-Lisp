package meta;

import mb.RangePathsException;

public class QueueRun {
    public QueueRun(ScopeNode scope)
    {
        this.scope = scope;
    }
    private ScopeNode scope;
    public Object run(String str) throws RangePathsException {
        Node<Token> tokens=Token.run(str);
        Node<Exp> exps=Exp.parse(tokens);
        return run(exps);
    }
    public Object run(Node<Exp> exps) throws RangePathsException {
        Node<Exp> tmp = exps;
        Object o = null;
        while (tmp != null) {
            Exp exp = tmp.first;
            tmp = tmp.rest;
            if (exp.isBracketExp()) {
                BracketExp bracketExp =exp.asBracketExp();
                Exp first_exp = bracketExp.children.first;
                Object first = ReadMacro.run_read_exp(scope, first_exp);
                if (first instanceof ReadMacro) {
                    /*只读作用域宏*/
                    o = ((ReadMacro) first).exec(
                            scope,
                            bracketExp
                    );
                } else if (first instanceof WriteMacro) {
                    /*读写作用域宏*/
                    scope = ((WriteMacro) first).exec(
                            scope,
                            bracketExp
                    );
                } else {
                    throw first_exp.exception("不是read或write宏");
                }
            } else {
                o = ReadMacro.run_atom(scope, (IDExp) exp);
            }
        }
        return o;
    }
}
