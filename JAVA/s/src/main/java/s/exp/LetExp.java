package s.exp;

import s.Exp;
import s.Node;
import s.Token;

import java.util.List;

public class LetExp extends BracketExp{
    public LetExp(Token left, Node<Exp> children, Token right, Node<Exp> r_children) {
        super(left, children, right, r_children);
    }

    @Override
    public void buildString(StringBuilder sb) {
        sb.append(getLeft().getContent());
        sb.append("let");
        buildStringBody(sb);
        if (getRight()!=null){
            sb.append(getRight().getContent());
        }
    }
}
