package s.exp;
import s.LocationException;
import s.Token;

public abstract class AtomExp extends Exp{
    protected Token token;
	@Override
	protected void toString(StringBuilder sb) {
		// TODO Auto-generated method stub
		sb.append(token.toString());
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
		if(tq.current().Type()==Token.Type.Bool) {
			return BoolExp.parse(tq);
		}else{
			throw tq.error_token();
		}
	}
	public static Exp parse(TokenQueue tq) throws LocationException {
		return parse(tq,false);
	}
}
