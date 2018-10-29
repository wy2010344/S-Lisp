package s.exp;

import s.LocationException;
import s.Node;
import s.Token;

public class LetRestIdExp extends AtomExp{
	private LetRestIdExp(Token token) {
		this.token=token;
		this.value=token.Value().substring(3);
	}
    private String value;
    public String Value() {
    	return value;
    }
	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.Let_Rest_ID;
	}
	public static LetRestIdExp parse(TokenQueue tq) throws LocationException {
		Token x=tq.current();
		tq.shift();
		if(x.Value().length()>3 && x.Value().lastIndexOf('.')<3) {
			return new LetRestIdExp(x);
		}else {
			throw tq.error_token("不是合法的剩余匹配类型");
		}
	}
	@Override
	public Object eval(Node<Object> scope) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
