package st1.exp;

import s.Node;
import st1.AbstractListExp;
import st1.Exp;

public class MBracketExp extends AbstractListExp {
    public MBracketExp(Node<Exp> children, Node<Exp> r_children) {
        super(children, r_children);
    }
}
