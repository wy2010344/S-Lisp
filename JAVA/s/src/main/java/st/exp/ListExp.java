package st.exp;

import st.Exp;
import st.Token;

import java.util.List;

public class ListExp extends BracketExp {
    public final List<Exp> children;

    public ListExp(Token left, List<Exp> children, Token right) {
        super(left, right);
        this.children=children;
    }
}
