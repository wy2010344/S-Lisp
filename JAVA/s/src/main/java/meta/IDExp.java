package meta;

import mb.RangePathsException;

public class IDExp extends Exp {
    private final Token token;
    public final String value;
    public IDExp(Token token){
        this.token=token;
        this.value=token.value;
    }

    @Override
    public RangePathsException exception(String msg) {
        return token.exception(msg);
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(token.value);
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        toString(sb);
        return sb.toString();
    }
}
