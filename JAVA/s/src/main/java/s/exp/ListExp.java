package s.exp;
import s.Node;
import s.Token;

public class ListExp extends BracketsExp{

	public ListExp(Token first, Node<Exp> children, Token last) {
		super(first, children, last);
		this.r_children=Node.reverse(children);
	}
	private Node<Exp> r_children;
	public Node<Exp> R_children(){
		return r_children;
	}
	@Override
	public String left() {
		// TODO Auto-generated method stub
		return "[";
	}
	@Override
	public String right() {
		// TODO Auto-generated method stub
		return "]";
	}
	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.List;
	}
	
}
