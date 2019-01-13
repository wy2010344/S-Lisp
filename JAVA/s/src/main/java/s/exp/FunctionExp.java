package s.exp;

import s.Exp;
import s.Node;
import s.Token;

import java.util.List;

public class FunctionExp extends BracketExp {
    public FunctionExp(Token left, Node<Exp> children, Token right, Node<Exp> r_children) {
        super(left, children, right, r_children);
    }
}
