package s;
import s.exp.*;

public interface Function {
	enum Type{
		buildIn,//内置函数，toString返回名字
		user,//用户函数，toString返回定义，可能是优化函数
		cache
	}
	/**
	 * 外部调用的方法
	 * @param args
	 * @return
	 * @throws Exception
	 */
	Object exec(Node<Object> args) throws Exception;
	Type ftype();
	public static class UserFunction implements Function{
		public UserFunction(FunctionExp exp,Node<Object> parentScope) {
			this.parentScope=parentScope;
			this.exp=exp;
		}
		protected Node<Object> parentScope;
		FunctionExp exp;
		/*node.Value(),node.Next().Value(),.....*/
		@Override
		public Object exec(Node<Object> node) throws Exception {
			Node<Object> scope=Node.kvs_extend("args", node,parentScope);
			scope=Node.kvs_extend("this",this,scope);
			QueueRun qr=new QueueRun(scope);
			return qr.exec(exp);
		}
		//不换行，默认
		@Override
		public String toString() {
			return exp.toString();
		}
		//嵌套不换行
		public void toString(StringBuilder sb) {
			exp.buildString(sb);
		}
		@Override
		public Type ftype() {
			return Type.user;
		}
	}
}
