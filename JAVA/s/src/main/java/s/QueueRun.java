package s;
import s.exp.*;

public class QueueRun {
	public Node<Object> scope;
	public QueueRun(Node<Object> scope) {
		this.scope=scope;
	}
	public Object exec(FunctionExp exp) throws Exception {
		Object r=null;
		Node<Exp> tmp=exp.getChildren();
		while (tmp!=null){
			r=run(tmp.First());
			tmp=tmp.Rest();
		}
		return r;
	}
	Object run(Exp exp) throws IndexException {
		if (exp instanceof LetExp){
			scope=runLet((LetExp) exp,scope);
			return null;
		}else{
			return interprate(exp,scope);
		}
	}
	private static Node<Object> runLet(LetExp letExp,Node<Object> scope) throws IndexException {
		Node<Exp> cs=letExp.getChildren();
		while (cs!=null){
			Exp key=cs.First();
			cs=cs.Rest();
			Exp value_exp=cs.First();
			Object value=interprate(value_exp,scope);
			cs=cs.Rest();
			if (key instanceof LetIDExp){
				scope=kvs_extend(((LetIDExp) key).getValue(),value,scope);
			}else if (key instanceof LetBracketExp){
				scope=letSmallMatch(key,value,scope);
			}else{
				throw key.exception("key不是合法的let-id类型");
			}
		}
		return scope;
	}
	private static Node<Object> kvs_extend(String key,Object value, Node<Object> scope) {
		return Node.kvs_extend(key, value, scope);
	}
	private static Node<Object> letSmallMatch(Exp small,Object value,Node<Object> scope) throws IndexException {
		if(value==null || value instanceof Node) {
			Node<Object> vs=(Node<Object>)value;
			Node<Exp> ks=((LetBracketExp)small).getChildren();
			while (ks!=null){
				Object v=null;
				if(vs!=null) {
					v=vs.First();
				}
				Exp k=ks.First();
				ks=ks.Rest();
				if(k instanceof LetIDExp) {
					scope=kvs_extend(((LetIDExp) k).getValue(),v,scope);
				}else
				if(k instanceof LetBracketExp) {
					scope=letSmallMatch(k,v,scope);
				}else
				if(k instanceof LetRestIDExp) {
					scope=kvs_extend(((LetRestIDExp) k).getValue(), vs, scope);
				}
				if(vs!=null) {
					vs=vs.Rest();
				}
			}
		}else {
			throw small.exception(value.toString()+"不是列表，无法参与匹配");
		}
		return scope;
	}
	public static String getPath(Node<Object> scope){
		String path=null;
		Node<Object> tmp=scope;
		while (tmp!=null && path==null){
			String key= (String) tmp.First();
			tmp=tmp.Rest();
			if ("pathOf".equals(key)){
				if (tmp.First() instanceof  Function){
					Function pathOf= (Function) tmp.First();
					try {
						path= (String) pathOf.exec(null);
					} catch (Exception e) {
						e.printStackTrace();
						path="";
					}
				}
			}
			tmp=tmp.Rest();
		}
		return path;
	}

	private static Node<Object> calNode(BracketExp exp, Node<Object> scope) throws IndexException {
		Node<Object> r=null;
		Node<Exp> tmp=exp.getR_children();
		while (tmp!=null){
			r=Node.extend(interprate(tmp.First(),scope),r);
			tmp=tmp.Rest();
		}
		return r;
	}
	private static IndexException error_throw(String s, Exp exp, Node<Object> scope, Node<Object> children) {
		return exp.exception(getPath(scope)+"\r\n"+children.toString()+"\r\n"+exp.toString());
	}

	private static IndexException match_Exception(Node<Object> scope, String msg, Exp exp) {
		return exp.exception(getPath(scope)+":\t"+msg);
	}
	private static Object interprate(Exp exp, Node<Object> scope) throws IndexException {
		if (exp instanceof CallExp){
			Node<Object> children=calNode((BracketExp)exp,scope);
			Object first=children.First();
			if (first==null){
				throw error_throw("未找到函数定义",exp,scope,children);
			}else if (first instanceof Function){
				try {
					return ((Function) children.First()).exec(children.Rest());
				}catch (IndexException lex){
					lex.addStack(getPath(scope),(CallExp)exp);
					throw lex;
				}catch (Throwable ex){
					throw error_throw("函数执行内部错误"+ex.getMessage(),exp,scope,children);
				}
			}else {
				throw error_throw("不是函数",exp,scope,children);
			}
		}else if(exp instanceof ListExp){
			return calNode((BracketExp)exp,scope);
		}else if(exp instanceof FunctionExp){
			return new Function.UserFunction((FunctionExp) exp,scope);
		}else if(exp instanceof StringExp){
			return ((StringExp) exp).getValue();
		}else if(exp instanceof IntExp){
			return ((IntExp) exp).getValue();
		}else if(exp instanceof BooleanExp){
			return ((BooleanExp) exp).getValue();
		}else if(exp instanceof IdExp){
			Node<Object> ids=((IdExp) exp).getIds();
			if (ids==null){
				throw match_Exception(scope,exp.toString()+"不是合法的id类型",exp);
			}else{
				Node<Object> c_scope=scope;
				Object value=null;
				while (ids!=null){
					String key= (String) ids.First();
					value=Node.kvs_find1st(c_scope,key);
					ids=ids.Rest();
					if (ids!=null){
						if (value==null || value instanceof Node){
							c_scope= (Node<Object>) value;
						}else{
							throw match_Exception(scope,"计算"+ids.toString()+"出错，其中" + value + "不是kvs类型:\t" + exp.toString(), exp);
						}
					}
				}
				return value;
			}
		}else{
			return null;
		}
	}


	public Object exec(String str,String line_split) throws Exception {
		FunctionExp functionExp= Exp.run(Token.run(str));
		return exec(functionExp);
	}
}
