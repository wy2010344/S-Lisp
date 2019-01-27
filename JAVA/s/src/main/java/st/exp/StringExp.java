package st.exp;

import st.Token;

public class StringExp extends AtomExp {
    public final String value;
    public StringExp(Token token, String value) {
        super(token);
        this.value=value;
    }
}
