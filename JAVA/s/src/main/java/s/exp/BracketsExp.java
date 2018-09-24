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
	public s.Location Loc(){
		return first.Loc();
	}
	public BracketsExp(Token first,Node<Exp> children,Token last) {
		this.first=first;
		this.children=children;
		this.last=last;
	}
    @Override
    public String toString() {
    	 StringBuilder sb=new StringBuilder();
         toString(sb);
         return sb.toString();
    }
    @Override
    public String toString(int indent) {
        // TODO Auto-generated method stub
   		StringBuilder sb=new StringBuilder();
        toString(sb,indent);
        return sb.toString();
    }
	//不换行的工具方法 
	@Override
	public void toString(StringBuilder sb) {
		sb.append(left()).append(" ");
        for(Node<Exp> tmp=children;tmp!=null;tmp=tmp.Rest()) {
     	   Exp child=tmp.First();
     	   child.toString(sb);
     	   sb.append(" ");
        }
        sb.append(right());
	}
	//换行的工具方法
	@Override
	public void toString(StringBuilder sb,int indent){
       repeat(sb,indent);
       sb.append(left()).append("\n");
       
       for(Node<Exp> tmp=children;tmp!=null;tmp=tmp.Rest()) {
    	   Exp child=tmp.First();
    	   child.toString(sb, indent);
    	   sb.append("\n");
       }
       
       repeat(sb,indent);
       sb.append(right());
   }
	public abstract String left();
	public abstract String right();
	@Override
	public String to_value() {
		// TODO Auto-generated method stub
		return left()+right();
	}
	@Override
	public boolean isBracket() {
		// TODO Auto-generated method stub
		return true;
	}
}
