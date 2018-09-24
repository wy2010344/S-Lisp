package s.exp;
import s.Node;
import s.Token;

public class LetExp extends BracketsExp{

	public LetExp(Token first, Node<Exp> children, Token last) {
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
}
