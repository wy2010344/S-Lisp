package s.exp;
import s.Node;
import s.Token;

public class ListExp extends BracketsExp{

	/**
	 * 
	 * @param first
	 * @param r_children
	 * @param last
	 */
	private ListExp(Token first, Node<Exp> r_children, Token last) {
		super(first,Node.reverse(r_children), last);
		this.r_children=r_children;
	}
	private Node<Exp> r_children;
	public Node<Exp> R_children(){
		return r_children;
	}
	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.List;
	}
	public static ListExp parse(TokenQueue tq) throws Exception {
		Token first=tq.current();
		tq.shift();//排出[
		Node<Exp> r_children=null;
		while(tq.notEnd() && tq.current().Type()!=Token.Type.BraR) {
			r_children=Node.extend(Exp.parse(tq,true), r_children);
		}
		Exception e=tq.check_end("]");
		if(e!=null) {
			throw e;
		}else {
			Token last=tq.current();
			tq.shift();//排出"]"
			return new ListExp(first,r_children,last);
		}
	}
	@Override
	public Object eval(Node<Object> scope) throws Exception {
		// TODO Auto-generated method stub
		Node<Object> o=null;
		for(Node<Exp> t=r_children;t!=null;t=t.Rest()) {
			o=Node.extend(t.First().eval(scope), o);
		}
		return o;
	}
}
