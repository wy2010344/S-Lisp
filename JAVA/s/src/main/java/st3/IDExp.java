package st3;

import mb.RangePathsException;

public class IDExp extends Exp {
    public final Token token;
    public IDExp(Token token){
        this.token=token;
    }

    @Override
    public RangePathsException exception(String msg) {
        return token.exception(msg);
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append("(Exp ").append(token.value).append(")");
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        toString(sb);
        return sb.toString();
    }
}
