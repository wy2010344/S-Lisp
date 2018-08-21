package s;

public interface Function {
	public static enum Type{
		buildIn,//内置函数，toString返回名字
		user,//用户函数，toString返回定义，可能是优化函数
		cache
	}
	/**
	 * 外部调用的方法
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public Object exec(Node node) throws Exception;
	public Type ftype();
	public static class UserFunction implements Function{
		
		public UserFunction(Exp.LBracketsExp exp,Node parentScope) {
			this.parentScope=parentScope;
			this.exp=exp;
		}
		protected Node parentScope;
		Exp.LBracketsExp exp;
		/*node.Value(),node.Next().Value(),.....*/
		@Override
		public Object exec(Node node) throws Exception {
			// TODO Auto-generated method stub
			Node scope=Library.kvs_extend("args", node,parentScope);
			scope=Library.kvs_extend("this",this,scope);
			QueueRun qr=new QueueRun(scope);
			Object r=null;
			for(Node tmp=exp.Children();tmp!=null;tmp=tmp.Rest()) {
				Exp x=(Exp)tmp.First();
				r=qr.run(x);
			}
			return r;
		}
		//不换行，默认
		@Override
		public String toString() {
			return exp.toString();
		}
		//换行
		public String toString(int indent) {
			return exp.toString(indent);
		}
		//嵌套不换行
		public void toString(StringBuilder sb) {
			exp.toString(sb);
		}
		//嵌套换行
		public void toString(StringBuilder sb,int indent) {
			exp.toString(sb, indent);
		}
		@Override
		public Type ftype() {
			// TODO Auto-generated method stub
			return Type.user;
		}
	}
}
