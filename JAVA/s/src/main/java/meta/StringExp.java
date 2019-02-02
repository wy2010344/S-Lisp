package meta;

import mb.RangePathsException;

public class StringExp extends Exp {
    public StringExp(Token token){
        this.token=token;
        String v=token.value;
        v=v.substring(1,v.length()-1);
        v=v.replace("\\\"","\"");
        this.value=v;
    }
    private final Token token;
    public final String value;
    @Override
    public RangePathsException exception(String msg) {
        return new RangePathsException(token.begin,token.begin+token.value.length(),msg);
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(token.value);
    }
}
