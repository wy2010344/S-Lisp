package s.exp;

import s.Token;

public class IntExp extends AtomExp{

	public IntExp(Token token) {
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
	public String to_value() {
		// TODO Auto-generated method stub
		return Integer.toString(value);
	}
	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.Int;
	}
}
