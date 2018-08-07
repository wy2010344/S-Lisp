package s;

public class Node {
	public Node(Object v,Node n) {
		this.value=v;
		this.next=n;
		if(n!=null) {
			this.length=n.Length()+1;
		}else {
			this.length=1;
		}
	}
	private int length;
	public int Length() {
		return length;
	}
	private Object value;
	private Node next;
	public Object First() {return value;}
	public Node Rest() {return next;}

	//不换行，默认
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		toString(sb);
		return sb.toString();
	}
	//换行
	public String toString(int indent){
		StringBuilder sb=new StringBuilder();
		toString(sb,indent);
		return sb.toString();
	}
	
	//嵌套不换行
	public void toString(StringBuilder sb) {
		sb.append("[ ");
		for(Node t=this;t!=null;t=t.next) {
			if(t.value==null) {
				sb.append("[]");
			}else
			if(t.value instanceof Node) {
				((Node)t.value).toString(sb);
			}else 
			if(t.value instanceof Function.UserFunction){
				((Function.UserFunction)t.value).toString(sb);
			}else
			if(t.value instanceof String){
				sb.append("\"").append(Exp.replaceQuote(t.value.toString())).append("\"");
			}else
			{
				sb.append(t.value.toString());
			}
			sb.append(" ");
		}
		sb.append("]");
	}
	
	//嵌套换行
	public void toString(StringBuilder sb,int indent) {
		Exp.repeat(sb,indent);
		sb.append("[");
		sb.append("\n");
		for(Node t=this;t!=null;t=t.next) {
			if(t.value==null) {
				Exp.repeat(sb,indent+1);
				sb.append("null");
			}else
			if(t.value instanceof Node) {
				((Node)t.value).toString(sb, indent+1);
			}else 
			if(t.value instanceof Function.UserFunction){
				((Function.UserFunction)t.value).toString(sb,indent+1);
			}else
			if(t.value instanceof String){
				Exp.repeat(sb,indent+1);
				sb.append("\"").append(Exp.replaceQuote(t.value.toString())).append("\"");
			}else
			{
				Exp.repeat(sb,indent+1);
				sb.append(t.value.toString());
			}
			sb.append("\n");
		}
		Exp.repeat(sb,indent);
		sb.append("]");
	}
}
