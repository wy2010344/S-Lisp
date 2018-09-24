package s.exp;

import s.Token;

public class StringExp extends AtomExp{
	public StringExp(Token token) {
		this.token=token;
		this.value=token.Value();
	}
    private String value;
    public String Value() {
    	return value;
    }
    
    @Override
    public String toString() {
    	return value;
    }
	@Override
	protected void toString(StringBuilder sb) {
		// TODO Auto-generated method stub
		if(token.Original_type()==Token.Type.Quote) {
			//小括号、大括号引用转字符串
			toString(sb,value,"'","");
		}else 
		if(token.Original_type()==Token.Type.Id){
			//中括号id转字符串
			toString(sb,value,"","");
		}else{
			//默认字符串
        	toString(sb,mb.Util.string_to_trans(value,'"','"',null),"","");
		}
	}
	@Override
	public String to_value() {
		// TODO Auto-generated method stub
		return value;
	}
	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.String;
	}
}
