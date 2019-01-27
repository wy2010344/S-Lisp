package st.exp;

import st.Exp;
import st.Token;

public class BracketExp extends Exp {
    public final Token left;
    public final Token right;
    public BracketExp(Token left,Token right){
        this.left=left;
        this.right=right;
    }
}
