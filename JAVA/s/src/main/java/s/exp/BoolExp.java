package s.exp;

import s.Node;
import s.Token;

public class BoolExp extends AtomExp{

	private BoolExp(Token token) {
		this.token=token;
		this.value="true".equals(token.Value());
	}
	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp.Exp_Type.Bool;
	}

	private Boolean value;
	public Boolean Value() {
		return value;
	}
	
	public static BoolExp parse(TokenQueue tq) {
		Token x=tq.current();
		tq.shift();
		return new BoolExp(x);
	}
	@Override
	public Object eval(Node<Object> scope) throws Exception {
		// TODO Auto-generated method stub
		return value;
	}

}
