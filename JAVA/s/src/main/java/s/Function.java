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
	public Object exec(Node<Object> node) throws Exception;
	public Type ftype();
	public static class UserFunction implements Function{
		
		public UserFunction(Exp.FunctionExp exp,Node<Object> parentScope) {
			this.parentScope=parentScope;
			this.exp=exp;
		}
		protected Node<Object> parentScope;
		Exp.FunctionExp exp;
		/*node.Value(),node.Next().Value(),.....*/
		@Override
		public Object exec(Node<Object> node) throws Exception {
			// TODO Auto-generated method stub
			Node<Object> scope=Library.kvs_extend("args", node,parentScope);
			scope=Library.kvs_extend("this",this,scope);
			QueueRun qr=new QueueRun(scope);
			Object r=null;
			for(Node<Exp> tmp=exp.Children();tmp!=null;tmp=tmp.Rest()) {
				Exp x=tmp.First();
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
