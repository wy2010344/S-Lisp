package s.exp;
import s.LocationException;
import s.Node;
import s.Token;

public class IdExp extends AtomExp{
	private Node<String> paths;
    /**
     *此处的id会用于定义，剩余匹配，所以不能报错
     */
	public IdExp(Token token) throws LocationException {
		this.token=token;
		this.value=token.Value();
		if(value.charAt(0)=='.' || value.charAt(value.length()-1)=='.') {
			//throw new LocationException(value+"不是合法的id类型，不能以.开始或结束",token.Loc());
		}else {
    		int i=0;
    		int last_i=0;
    		Node<String> r=null;
    		boolean has_error=false;
    		while(i<value.length()) {
    			char c=value.charAt(i);
    			if(c=='.') {
    				String node=value.substring(last_i, i);
    				last_i=i+1;
    				if("".equals(node)) {
    					has_error=true;
    				}else {
    					r=Node.extend(node, r);
    				}
    			}
    			i++;
    		}
    		r=Node.extend(value.substring(last_i), r);
    		if(has_error) {
    			throw new LocationException(value+"不是合法的id类型，不允许连续的.号",token.Loc());
    		}else {
    			paths=Node.reverse(r);
    		}
		}
	}
    private String value;
    public Node<String> Paths() {
    	return paths;
    }
    public String Value() {
    	return value;
    }
	@Override
	protected void toString(StringBuilder sb) {
		// TODO Auto-generated method stub
		if(token.Original_type()==Token.Type.Quote) {
			//中括号引用转id
            toString(sb,value,"'","");
		}else {
			//默认id
			toString(sb,value,"","");
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
		return Exp_Type.ID;
	}
}
