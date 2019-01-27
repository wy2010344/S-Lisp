package st.exp;

import st.Token;

public class IntExp extends AtomExp {
    public final int value;

    public IntExp(Token token, int value) {
        super(token);
        this.value=value;
    }
}
