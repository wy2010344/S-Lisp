package s.exp;

import s.Exp;
import s.Node;
import s.Token;

import java.util.List;

public class ListExp extends BracketExp{
    public ListExp(Token left, Node<Exp> children, Token right, Node<Exp> r_children) {
        super(left, children, right, r_children);
    }
}
