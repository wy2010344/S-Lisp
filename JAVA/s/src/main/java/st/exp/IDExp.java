package st.exp;

import st.Token;

public class IDExp extends AtomExp {
    private final String value;
    public IDExp(Token token, String value) {
        super(token);
        this.value=value;
    }
}
