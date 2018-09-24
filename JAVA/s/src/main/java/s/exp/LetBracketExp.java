package s.exp;
import s.Node;
import s.Token;

public class LetBracketExp extends BracketsExp{

	public LetBracketExp(Token first, Node<Exp> children, Token last) {
		super(first, children, last);
	}
	@Override
	public String left() {
		// TODO Auto-generated method stub
		return "(";
	}
	@Override
	public String right() {
		// TODO Auto-generated method stub
		return ")";
	}
	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.Let_Bra;
	}
}
