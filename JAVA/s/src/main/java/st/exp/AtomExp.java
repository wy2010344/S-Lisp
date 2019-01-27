package st.exp;

import st.Exp;
import st.Token;

public class AtomExp extends Exp {
    public final Token token;
    public AtomExp(Token token){
        this.token=token;
    }
}
