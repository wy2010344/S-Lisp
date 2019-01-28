package st1.exp;

import s.Node;
import st1.AbstractListExp;
import st1.Exp;

public class PipExp extends AbstractListExp {
    public PipExp(Node<Exp> children, Node<Exp> r_children) {
        super(children, r_children);
    }
}
