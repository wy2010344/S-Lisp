package s;

import s.exp.*;

public class QueueRun {
	public QueueRun(Node<Object> scope) {
		this.scope=scope;
	}
	public Node<Object> scope;
	private Object run(Exp x) throws Exception {
		if(x.xtype()==Exp.Exp_Type.Call) {
			CallExp sbe=(CallExp)x;
			if(isLet(sbe)) {
				/**
				 * 也许应该进行命名规范的约束，未来再放开吧
				 * 前缀表达式添加.来分割
				 * 单个命名不允许.
				 * 减少命名重复的概率
				 */
				for(Node<Exp> y=sbe.Children().Rest();y!=null;y=y.Rest().Rest()) {
					Exp val=y.Rest().First();
					Object values=interpret(val, scope);
					scope=match(scope,(Exp)y.First(),values);
				}
				return null;
			}else {
				return interpret(x, scope);
			}
		}else{
			return interpret(x, scope);
		}
	}
	public Object exec(s.exp.FunctionExp exp) throws Exception {
		Object r=null;
		for(Node<Exp> tmp=exp.Children();tmp!=null;tmp=tmp.Rest()) {
			Exp x=tmp.First();
			r=run(x);
		}
		return r;
	}
	public Object exec(String str,char lineSplit) throws Exception {
		Node<Token> tokens=Token.tokenize(str, lineSplit);
		s.exp.FunctionExp exp=s.exp.FunctionExp.parse(tokens);
		return exec(exp);
	}

	LocationException match_Exception(Node<Object> scope,String msg,Location loc) {
		LocationException lox=new LocationException(getPath(scope)+":\t"+msg,loc);
		return lox;
	}
	Node<Object> match(Node<Object> scope,Exp y,Object values) throws Exception {
		if(y.xtype()==Exp.Exp_Type.ID){
			IdExp id_exp=(IdExp)y;
			String key=id_exp.Value();
			scope=when_normal_match(scope,key,values,id_exp.Loc());
		}else{
			if(y.xtype()==Exp.Exp_Type.Call) {
				//小括号，多匹配
				scope=when_bracket_match(scope,((CallExp)y).Children(),(Node<Object>)values);
			}else {
				throw match_Exception(scope,y.toString()+"类型不正确",y.Loc());
			}
		}
		return scope;
	}
	boolean isLet(CallExp sbe) {
		boolean ret=false;
		Exp sbec=(Exp)sbe.Children().First();
		if(sbec.xtype()==Exp.Exp_Type.ID) {
			IdExp sbec_id=(IdExp)sbec;
			if("let".equals(sbec_id.Value())) {
				ret=true;
			}
		}
		return ret;
	}
	boolean isWait(Exp e) {
		if(e.xtype()==Exp.Exp_Type.ID){
			if(e.to_value().startsWith("...")) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	/**
	 * 匹配括号
	 * @param scope
	 * @param key_nodes
	 * @param val_nodes
	 * @return
	 * @throws Exception
	 */
	Node<Object> when_bracket_match(Node<Object> scope,Node<Exp> keys,Node<Object> values) throws Exception {
		while(keys!=null) {
			Exp key=keys.First();
			if(isWait(key) && keys.Rest()==null) {
				//以...xx结尾，匹配后续的列表
				String key_name=key.to_value();
				key_name=key_name.substring(3);
				//普通匹配
				scope=when_normal_match(scope,key_name,values,key.Loc());
			}else {
				Object value=null;
				if(values!=null) {
					value=values.First();
					values=values.Rest();
				}
				scope=match(scope,key,value);
			}
			keys=keys.Rest();
		}
		return scope;
	}
	/**
	 * 普通匹配
	 * @param scope
	 * @param key
	 * @param kv ?
	 * @return
	 * @throws Exception
	 */
	Node<Object> when_normal_match(Node<Object> scope,String key,Object kv,Location loc) throws Exception {
		if(key.indexOf('.')<0) {
			scope=Node.kvs_extend(key, kv,scope);
		}else {
			throw match_Exception(scope,key+"不是合法的id",loc);
		}
		return scope;
	}
	
	private Node<Object> calNode(Node<Exp> list,Node<Object> scope) throws Exception {
		Node<Object> r=null;
		for(Node<Exp> x=list;x!=null;x=x.Rest()) {
			Exp xe=x.First();
			Object xv=interpret(xe,scope);
			r=Node.extend(xv,r);
		}
		return r;
		//return Library.reverse(r);
	}
	private LocationException errorMessage(String message,CallExp exp,Node<Object> children){
		String exp_msg=exp.toString();
		String result_msg=children.toString();
		LocationException lox= new LocationException(
						message+":\n"+
					    exp_msg+"\n"+
					    result_msg+"\n"+
					    exp.First().Loc().toString(),
					    exp.First().Loc());
		return lox;
	}
	String getPath(Node<Object> scope) {
		String path=null;
		Node<Object> tmp=scope;
		while(tmp!=null && path==null) {
			String key=(String)tmp.First();
			tmp=tmp.Rest();
			if("pathOf".equals(key)) {
				if(tmp.First() instanceof Function) {
					Function pathOf=(Function)tmp.First();
					try {
						path=(String) pathOf.exec(null);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			tmp=tmp.Rest();
		}
		return path;
	}
	private Object interpret(Exp exp,Node<Object> scope) throws Exception{ 
		if(exp.isBracket()) {
			if(exp.xtype()==Exp.Exp_Type.Call) {
				CallExp tmp=(CallExp)exp;
				Node<Object> children=calNode(tmp.R_children(),scope);
				Object first=children.First();
				if(first==null) {
					throw errorMessage("函数1未找到定义",tmp,children);
				}else
				if(first instanceof Function) {
					try {
						return ((Function)first).exec(children.Rest());
					}catch(LocationException ex) {
						ex.addStack(getPath(scope), tmp.First().Loc(), exp.toString());
						throw ex;
					}catch(Exception e) {
						throw errorMessage(e.getMessage(),tmp,children);
					}
				}else {
					throw errorMessage("参数1的结果必须是函数",tmp,children);
				}
			}else
			if(exp.xtype()==Exp.Exp_Type.List) {
				ListExp tmp=(ListExp)exp;
				return calNode(tmp.R_children(),scope);
			}else
			if(exp.xtype()==Exp.Exp_Type.Function) {
				return new Function.UserFunction((FunctionExp)exp, scope);
			}
		}else {
	        if(exp.xtype()==Exp.Exp_Type.String){
	            return ((StringExp)exp).Value();
	        }else
	        if(exp.xtype()==Exp.Exp_Type.Int){
	            return ((IntExp)exp).Value();
	        }else
	        if(exp.xtype()==Exp.Exp_Type.ID) {
				IdExp tmp=(IdExp)exp;
				Node<String> paths=tmp.Paths();
				if(paths==null) {
					throw match_Exception(scope,tmp.Value()+"不是合法的id类型:\t"+exp.toString(),tmp.Loc());
				}else {
					Node<Object> c_scope=scope;
					Object value=null;
					while(paths!=null) {
						String key=paths.First();
						value=Node.kvs_find1st(c_scope, key);
						paths=paths.Rest();
						if(paths!=null) {
							if(value==null || value instanceof Node) {
								c_scope=(Node<Object>)value;
							}else {
								throw match_Exception(scope,"计算"+paths.toString()+"，其中"+value + "不是kvs类型:\t"+exp.toString(), exp.Loc());
							}
						}
					}
					return value;
				}
			}
		}
        /*
        else
        if(exp instanceof FloatExp){
            return ((FloatExp)exp).value;
        }
		*/
		return null;
	}
}
