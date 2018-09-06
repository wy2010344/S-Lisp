package s;

public class Node<T> {
	public Node(T v,Node<T> n) {
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
	private T value;
	private Node<T> next;
	public T First() {return value;}
	public Node<T> Rest() {return next;}

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
		for(Node<T> t=this;t!=null;t=t.next) {
			if(t.value==null) {
				sb.append("[]");
			}else
			if(t.value instanceof Node) {
				((Node)t.value).toString(sb);
			}else
			if(t.value instanceof Function) {
				Function f=(Function) t.value;
				if(f instanceof Function.UserFunction) {
					((Function.UserFunction)f).toString(sb);
				}else
				if(f.ftype()==Function.Type.user) {
					/*优化但并非用户函数*/
					sb.append(f.toString());
				}else
				if(f.ftype()==Function.Type.buildIn) {
					/*内置函数*/
					sb.append("'").append(f.toString());
				}else
				if(f.ftype()==Function.Type.cache) {
					/*cache函数*/
					sb.append("[]");
				}
			}else
			if(t.value instanceof String){
				sb.append(mb.Util.string_to_trans(t.value.toString(),'"','"',null));
			}else
			if(t.value instanceof Integer){				
				sb.append(t.value);
			}else
			{
				String sx=t.value.toString();
				if(sx==null) {
					//某些内置库无法被序列化，比如match/cache-run
					sb.append("[]");
				}else {
					//js的toString只有字面值，在列表中需要转义，虽然用'比较有意义。
					//比如内置库
					sb.append("'").append(sx);
				}
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
		for(Node<T> t=this;t!=null;t=t.next) {
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
				sb.append(mb.Util.string_to_trans(t.value.toString(),'"','"',null));
			}else
			if(t.value instanceof Integer){		
				Exp.repeat(sb, indent+1);
				sb.append(t.value);
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
