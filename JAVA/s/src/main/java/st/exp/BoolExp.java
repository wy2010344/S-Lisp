package st.exp;

import st.Token;

public class BoolExp extends AtomExp {
    public final boolean value;

    public BoolExp(Token token, boolean value) {
        super(token);
        this.value=value;
    }
}
