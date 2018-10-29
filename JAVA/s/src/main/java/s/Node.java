package s;

import s.exp.Exp;

public class Node<T> {
	private Node(T v,Node<T> n) {
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
	
	private static void toString(Object v,StringBuilder sb) {
		if(v==null) {
			sb.append("[]");
		}else if(v instanceof Node) {
			((Node)v).toString(sb);
		}else if(v instanceof String) {
			sb.append(mb.Util.string_to_trans(v.toString(),'"','"',null));
		}else if(v instanceof Function) {
			Function f=(Function) v;
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
		}else if(v instanceof Boolean) {
			if((Boolean)v) {
				sb.append("true");
			}else {
				sb.append("false");
			}
		}else if(v instanceof Integer) {
			sb.append(v.toString());
		}else {
			String sx=v.toString();
			if(sx==null) {
				//某些内置库无法被序列化，比如match/cache-run
				sb.append("[]");
			}else {
				//js的toString只有字面值，在列表中需要转义，虽然用'比较有意义。
				//比如内置库
				sb.append("'").append(sx);
			}
		}
	}
	//嵌套不换行
	public void toString(StringBuilder sb) {
		sb.append("[ ");
		for(Node<T> t=this;t!=null;t=t.next) {
			toString(t.First(),sb);
			sb.append(" ");
		}
		sb.append("]");
	}
	
	public static <T> Node<T> extend(T x,Node<T> xs){
		return new Node<T>(x,xs);
	}
    /*反转*/
    public static <T> Node<T> reverse(Node<T> node) {
    	Node<T> r=null;
    	for(Node<T> t=node;t!=null;t=t.Rest()) {
    		r=extend(t.First(),r);
    	}
    	return r;
    }
	public static Node<Object> kvs_extend(String key,Object value,Node<Object> kvs){
		return extend(key,extend(value,kvs));
	}
	public static Object kvs_find1st(Node<Object> kvs,String key) {
    	if(kvs==null) {
    		return null;
    	}else {
    		Node<Object> r_kvs=kvs.Rest();
    		if(r_kvs==null) {
    			System.out.println("as:"+key+":"+kvs.toString());
    		}
    		if(key.equals(kvs.First())) {
    			return r_kvs.First();
    		}else {
    			return kvs_find1st(r_kvs.Rest(),key);
    		}
    	}
	}
}
