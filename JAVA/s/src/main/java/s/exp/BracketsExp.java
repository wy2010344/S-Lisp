package s.exp;

import s.Node;
import s.Token;

public abstract class BracketsExp extends Exp {

	private Token first;
	public Token First() {
		return first;
	}
	private Token last;
	public Token Last() {
		return last;
	}
	private Node<Exp> children;
	public Node<Exp> Children() {
		return children;
	}
	public BracketsExp(Token first,Node<Exp> children,Token last) {
		this.first=first;
		this.children=children;
		this.last=last;
	}
	//不换行的工具方法 
	@Override
	public void toString(StringBuilder sb) {
		sb.append(first.toString()).append(" ");
        for(Node<Exp> tmp=children;tmp!=null;tmp=tmp.Rest()) {
     	   Exp child=tmp.First();
     	   child.toString(sb);
     	   sb.append(" ");
        }
        sb.append(last.toString());
	}
}
