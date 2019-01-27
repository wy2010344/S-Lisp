package st.exp;

import st.Exp;
import st.Token;

import java.util.Map;

public class MapExp extends BracketExp {
    public final Map<Token, Exp> children;
    public MapExp(Token left, Map<Token, Exp> children, Token right) {
        super(left, right);
        this.children=children;
    }
}
