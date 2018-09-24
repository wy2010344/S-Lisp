package s.exp;

import s.Node;
import s.Token;
public class IntExp extends AtomExp{

	private IntExp(Token token) {
		this.token=token;
		this.value=Integer.parseInt(token.Value());
	}
    private Integer value;
    public Integer Value() {
    	return value;
    }
	@Override
	protected void toString(StringBuilder sb) {
		// TODO Auto-generated method stub
		toString(sb,value,"","");
	}
	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.Int;
	}

	public static IntExp parse(TokenQueue tq) {
		Token x=tq.current();
		tq.shift();
		return new IntExp(x);
	}
	@Override
	public Object eval(Node<Object> scope) {
		// TODO Auto-generated method stub
		return value;
	}
}
