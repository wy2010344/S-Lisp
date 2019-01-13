package s.exp;

import s.Exp;
import s.Node;
import s.Token;

import java.util.List;

public class LetBracketExp extends BracketExp {

    public LetBracketExp(Token left, Node<Exp> children, Token right, Node<Exp> r_children) {
        super(left, children, right, r_children);
    }

    @Override
    public void buildString(StringBuilder sb) {
        sb.append("(");
        buildStringBody(sb);
        sb.append(")");
    }
}
