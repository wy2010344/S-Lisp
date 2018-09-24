package s.exp;
import s.LocationException;
import s.Node;
import s.Token;

public class LetExp extends BracketsExp{

	private LetExp(Token first, Node<Exp> children, Token last) {
		super(first, children, last);
	}
	@Override
	public String left() {
		// TODO Auto-generated method stub
		return "(let";
	}

	@Override
	public String right() {
		// TODO Auto-generated method stub
		return ")";
	}

	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.Let;
	}
	/**
	 * k-v-k-v
	 * @param xs
	 * @return
	 * @throws Exception
	 */
	public static LetExp parse(TokenQueue tq) throws Exception {
		Token first=tq.current();
		tq.shift();//排出"("
		tq.shift();//排出"let"
		Node<Exp> r_children=null;
		while(tq.notEnd() && tq.current().Type()!=Token.Type.BraR) {
			/*key部分*/
			if(tq.current().Type()==Token.Type.BraL && "(".equals(tq.current().Value())) {
				r_children=Node.extend(LetBracketExp.parse(tq), r_children);
			}else 
			if(tq.current().Type()==Token.Type.Id) {
				r_children=Node.extend(LetIdExp.parse(tq), r_children);
			}else{
				throw tq.error_token("不是期待的let表达式key类型");
			}
			/*value部分*/
			if(tq.notEnd()&& tq.current().Type()!=Token.Type.BraR) {
				r_children=Node.extend(Exp.parse(tq), r_children);
			}else {
				if(tq.notEnd()) {
					throw new LocationException("let表达式的key需要一个值，却结束let表达式",tq.current().Loc());
				}else {
					throw new Exception("let表达式未结束，却到达结尾");
				}
			}
		}
		Exception e=tq.check_end(")");
		if(e!=null) {
			throw e;
		}else {
			Token last=tq.current();
			tq.shift();//排出")"
			if(r_children==null) {
				throw new LocationException("let表达式不允许为空",first.Loc());
			}else {
				return new LetExp(first,Node.reverse(r_children),last);
			}
		}
	}

	protected Node<Object> idDef(Exp key,Object value,Node<Object> scope){
		return Node.kvs_extend(((LetIdExp) key).Value(), value, scope);
	}
	
	protected Node<Object> braDef(Exp key,Object value,Node<Object> scope) throws LocationException{
		LetBracketExp bkey=(LetBracketExp)key;
		if(value==null || value instanceof Node) {
			Node<Object> vs=(Node<Object>)value;
			for(Node<Exp> t=bkey.Children();t!=null;t=t.Rest()) {
				Exp tf=t.First();
				Object v=null;
				if(vs!=null) {
					v=vs.First();
				}
				if(tf instanceof LetIdExp) {
					scope=idDef(tf,v,scope);
				}else
				if(tf instanceof LetBracketExp) {
					scope=braDef(tf,v,scope);
				}else
				if(tf instanceof LetRestIdExp) {
					scope=Node.kvs_extend(((LetRestIdExp) tf).Value(), vs, scope);
				}
				if(vs!=null) {
					vs=vs.Rest();
				}
			}
		}else {
			throw new LocationException(value.toString()+"不是列表，无法参与匹配"+key.toString(),key.Loc());
		}
		return scope;
	}
	@Override
	public Object eval(Node<Object> scope) throws Exception {
		Node<Exp> xs=Children();
		while(xs!=null) {
			Exp key=xs.First();
			xs=xs.Rest();
			Object value=xs.First().eval(scope);
			xs=xs.Rest();
			if(key instanceof LetIdExp) {
				scope=idDef(key,value,scope);
			}else
			if(key instanceof LetBracketExp) {
				scope=braDef(key,value,scope);
			}
		}
		return scope;
	}
}
