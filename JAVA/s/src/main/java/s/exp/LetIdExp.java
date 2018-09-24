package s.exp;
import s.Token;

public class LetIdExp extends AtomExp{

	public LetIdExp(Token token) {
		this.token=token;
		this.value=token.Value();
	}
    private String value;
    public String Value() {
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
		return value;
	}
	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.Let_ID;
	}
}
