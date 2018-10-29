package s.exp;
import s.Node;
import s.Token;

public class LetBracketExp extends BracketsExp{

	private LetBracketExp(Token first, Node<Exp> children, Token last) {
		super(first, children, last);
	}
	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.Let_Bra;
	}
	
	public static LetBracketExp parse(TokenQueue tq) throws Exception {
		Token first=tq.current();
		tq.shift();//排出"("
		Node<Exp> r_children=null;
		while(tq.notEnd() && tq.current().Type()!=Token.Type.BraR) {
			if(tq.current().Type()==Token.Type.BraL && "(".equals(tq.current().Value()))
			{
				/**
				 * 括号表达式
				 */
				r_children=Node.extend(LetBracketExp.parse(tq), r_children);
			}else
			if(tq.current().Type()==Token.Type.Id) {
				Token next=tq.next();
				if(next!=null) {
					if(next.Type()==Token.Type.BraR && 
					   ")".equals(next.Value()) && 
					  tq.current().Value().startsWith("...")
					) {
						r_children=Node.extend(LetRestIdExp.parse(tq), r_children);
					}else {
						r_children=Node.extend(LetIdExp.parse(tq), r_children);
					}
				}else {
					throw new Exception("let表达式到达结尾未正常匹配");
				}
			}
		}
		Exception e=tq.check_end(")");
		if(e!=null) {
			throw e;
		}else {
			Token last=tq.current();
			tq.shift();//排出"]"
			return new LetBracketExp(first,Node.reverse(r_children),last);
		}
	}
	@Override
	public Object eval(Node<Object> scope) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
