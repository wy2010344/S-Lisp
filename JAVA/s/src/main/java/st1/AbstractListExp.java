package st1;

import s.Node;

public class AbstractListExp extends Exp {
    public final Node<Exp> r_children;
    public final Node<Exp> children;
    public AbstractListExp(Node<Exp> children, Node<Exp> r_children){
        this.children=children;
        this.r_children=r_children;
    }
}
