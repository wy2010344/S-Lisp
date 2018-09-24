package s.exp;
import s.LocationException;
import s.Node;
import s.Token;
import s.exp.Exp.TokenQueue;

public class IdExp extends AtomExp{
	private Node<String> paths;
    /**
     *此处的id会用于定义，剩余匹配，所以不能报错
     */
	private IdExp(Token token) throws LocationException {
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
    		}else 
    		if(r==null){
    			throw new LocationException(value+"不是合法的id类型，不允许空节点",token.Loc());
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
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.ID;
	}
	
	public static IdExp parse(TokenQueue tq) throws LocationException {
		Token x=tq.current();
		tq.shift();
		return new IdExp(x);
	}
	@Override
	public Object eval(Node<Object> scope) throws Exception {
		// TODO Auto-generated method stub
		Node<String> paths=Paths();
		Node<Object> c_scope=scope;
		Object value=null;
		while(paths!=null) {
			String key=paths.First();
			value=Node.kvs_find1st(c_scope, key);
			paths=paths.Rest();
			if(paths!=null) {
				if(value==null || value instanceof Node) {
					c_scope=(Node<Object>)value;
				}else {
					throw new LocationException("计算"+paths.toString()+"，其中"+value + "不是kvs类型:\t"+toString(),Loc());
				}
			}
		}
		return value;
	}
}
