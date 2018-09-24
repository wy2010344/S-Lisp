package s.exp;
import s.LocationException;
import s.Token;

public abstract class AtomExp extends Exp{
    protected Token token;
    @Override
    public s.Location Loc() {
    	return token.Loc();
    }
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		toString(sb);
		return sb.toString();
	}
	@Override
	public String toString(int indent) {
		StringBuilder sb=new StringBuilder();
        repeat(sb,indent);
		toString(sb);
		return sb.toString();
	}
	@Override
	protected void toString(StringBuilder sb, int indent) {
		// TODO Auto-generated method stub
        repeat(sb,indent);
		toString(sb);
	}
    protected void toString(StringBuilder sb,Object value,String before,String after){
        sb.append(before).append(value).append(after);
    }
	@Override
	public boolean isBracket() {
		// TODO Auto-generated method stub
		return false;
	}
	public static Exp parse(TokenQueue tq,boolean trans) throws LocationException {
		if(tq.current().Type()==Token.Type.Id) {
			if(trans) {
				return StringExp.parse(tq);
			}else {
				return IdExp.parse(tq);
			}
		}else
		if(tq.current().Type()==Token.Type.Quote) {
			if(trans) {
				return IdExp.parse(tq);
			}else {
				return StringExp.parse(tq);
			}
		}else
		if(tq.current().Type()==Token.Type.Str) {
			return StringExp.parse(tq);
		}else
		if(tq.current().Type()==Token.Type.Int) {
			return IntExp.parse(tq);
		}else 
		if(tq.current().Type()==Token.Type.Comment) {
			//注释，不采纳
			return null;
		}else {
			throw tq.error_token();
		}
	}
	public static Exp parse(TokenQueue tq) throws LocationException {
		return parse(tq,false);
	}
}
