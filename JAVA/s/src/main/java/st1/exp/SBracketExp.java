package st1.exp;

import s.Node;
import st1.AbstractListExp;
import st1.Exp;

public class SBracketExp extends AbstractListExp {
    public SBracketExp(Node<Exp> children, Node<Exp> r_children) {
        super(children, r_children);
    }
}
