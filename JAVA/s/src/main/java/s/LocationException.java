package s;

public class LocationException extends Exception {
	public LocationException(String msg,Location loc) {
		super(msg);
		this.loc=loc;
	}
	Location loc;
	public Location Loc() {
		return loc;
	}
	class Stack{
		public String path;
		public Location loc;
		public String exp;
	}
	
	Node<Stack> stacks=null;
	public void addStack(String path,Location loc,String exp) {
		Stack stack=new Stack();
		stack.path=path;
		stack.loc=loc;
		stack.exp=exp;
		stacks=Node.extend(stack, stacks);
	}
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for(Node<Stack> tmp=stacks;tmp!=null;tmp=tmp.Rest()) {
			Stack stack=tmp.First();
			sb.append(stack.path).append("\t")
			  .append(stack.loc.toString()).append("\t")
			  .append(stack.exp).append("\r\n");
		}
		sb.append(loc.toString()).append("\r\n");
		sb.append(super.getMessage()).append("\r\n");
		return sb.toString();
	}
}