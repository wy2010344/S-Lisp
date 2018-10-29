package s.exp;
import s.LocationException;
import s.Node;
import s.Token;

public class LetIdExp extends AtomExp{

	private LetIdExp(Token token) {
		this.token=token;
		this.value=token.Value();
	}
    private String value;
    public String Value() {
    	return value;
    }
	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.Let_ID;
	}
	public static LetIdExp parse(TokenQueue tq) throws LocationException {
		Token x=tq.current();
		tq.shift();
		if(x.Value().indexOf('.')>-1) {
			throw tq.error_token("不是合法的id类型");
		}else {
			return new LetIdExp(x);
		}
	}
	@Override
	public Object eval(Node<Object> scope) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
