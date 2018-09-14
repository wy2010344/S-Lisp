package s;

public class QueueRun {
	public QueueRun(Node<Object> scope) {
		this.scope=scope;
	}
	public Node<Object> scope;
	public Object run(Exp x) throws Exception {
		if(x.xtype()==Exp.Exp_Type.Call) {
			Exp.CallExp sbe=(Exp.CallExp)x;
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

	Node<Object> match(Node<Object> scope,Exp y,Object values) throws Exception {
		if(y.xtype()==Exp.Exp_Type.Call) {
			//小括号，多匹配
			scope=when_bracket_match(scope,((Exp.CallExp)y).Children(),(Node<Object>)values);
		}else 
		if(y.xtype()==Exp.Exp_Type.ID){
			String key=((Exp.IdExp)y).Value();
			if(key.endsWith("*")) {
				scope=when_kvs_match(scope,key,values);
			}else {
				scope=when_normal_match(scope,key,values);
			}
		}else{
			throw new Exception("类型不正确");
		}
		return scope;
	}
	boolean isLet(Exp.CallExp sbe) {
		boolean ret=false;
		Exp sbec=(Exp)sbe.Children().First();
		if(sbec.xtype()==Exp.Exp_Type.ID) {
			Exp.IdExp sbec_id=(Exp.IdExp)sbec;
			if("let".equals(sbec_id.Value())) {
				ret=true;
			}
		}
		return ret;
	}
	/**
	 * 是否是合法的标识符。
	 * 不允许.与*
	 * @param key
	 * @return
	 */
	boolean isValidKey(String key) {
		boolean r=true;
		if(key.charAt(0)=='.' || key.charAt(key.length()-1)=='.') {
			return false;
		}else {
			for(int i=0;i<key.length();i++) {
				char c=key.charAt(i);
				if(c =='*') {
					r=false;
				}else
				if(Character.isWhitespace(c)) {
					r=false;
				}
			}
			return r;
		}
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
				if(key_name.endsWith("*")) {
					//字典匹配
					scope=when_kvs_match(scope,key_name,values);
				}else {
					//普通匹配
					scope=when_normal_match(scope,key_name,values);
				}
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
	Node<Object> when_normal_match(Node<Object> scope,String key,Object kv) throws Exception {
		if(isValidKey(key)) {
			scope=Node.kvs_extend(key, kv,scope);
		}else {
			throw new Exception(key+"不是合法的id");
		}
		return scope;
	}
	
	final boolean WITH_KVS_MATCH=false;
	/**
	 * kvs-match匹配
	 * @param scope
	 * @param key
	 * @param values
	 * @return
	 * @throws Exception
	 */
	Node<Object> when_kvs_match(Node<Object> scope,String key,Object values) throws Exception {
		if(WITH_KVS_MATCH) {
			key=key.substring(0, key.length()-1);
			if(isValidKey(key)) {
				scope=kvs_match(scope,key,values);
			}else {
				throw new Exception(key+"不是合法的id");
			}
			return scope;
		}else {
			throw new Exception("为了雪藏的kvs-match，暂时不允许以*号结尾");
		}
	}
	/**
	 * 匹配字典
	 * @param scope
	 * @param key_prefix
	 * @param values
	 * @return
	 * @throws Exception
	 */
	Node<Object> kvs_match(Node<Object> scope,final String key_prefix,Object values) throws Exception {
		final Node<Object> kvs=(Node<Object>)values;
		/**
		 * 附加一个查询字典的函数
		 */
		scope=Node.kvs_extend(key_prefix, new Function() {
			@Override
			public Object exec(Node<Object> node)throws Exception {
				// TODO Auto-generated method stub
				return Node.kvs_find1st(kvs, (String)node.First());
			}
			@Override
			public String toString() {
				//其中kvs为何？依赖闭包，在列表中会被转成'xx
				return "{ (kvs-find1st kvs (first args)) }";
			}
			@Override
			public Type ftype() {
				// TODO Auto-generated method stub
				return Function.Type.user;
			}
		},scope);
		final Node<Object> svk=Node.reverse((Node<Object>)values);
		Node<Object> tmp_svk=svk;
		while(tmp_svk!=null) {
			Object vk=tmp_svk.First();
			tmp_svk=tmp_svk.Rest();
			Object tmp_k=tmp_svk.First();
			if(tmp_k instanceof String) {
				//是String类型
				String k=(String)tmp_svk.First();
				if(isValidKey(k)) {
					//命名合法，还需要去除空格等情况。
					scope=Node.kvs_extend(key_prefix+"."+k,vk,scope);//添加分割符，似乎又显得不和谐，与用户自己的风格
				}else {
					//忽略
				}
			}else {
				throw new Exception(tmp_k+"不是字符串类型");
			}
			tmp_svk=tmp_svk.Rest();
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
	private void errorMessage(String message,Exp.CallExp exp,Node<Object> children) throws Exception {
		String exp_msg=exp.toString();
		String result_msg=children.toString();
		throw new Exception(
			message+":\n"+
		    exp_msg+"\n"+
		    result_msg+"\n"+
		    exp.First().Loc().toString()
		);
	}
	private Object interpret(Exp exp,Node<Object> scope) throws Exception{ 
		if(exp.isBracket()) {
			if(exp.xtype()==Exp.Exp_Type.Call) {
				Exp.CallExp tmp=(Exp.CallExp)exp;
				Node<Object> children=calNode(tmp.R_children(),scope);
				Object first=children.First();
				if(first==null) {
					errorMessage("函数1未找到定义",tmp,children);
				}else
				if(first instanceof Function) {
					return ((Function)first).exec(children.Rest());
				}else {
					errorMessage("参数1的结果必须是函数",tmp,children);
				}
			}else
			if(exp.xtype()==Exp.Exp_Type.List) {
				Exp.ListExp tmp=(Exp.ListExp)exp;
				return calNode(tmp.R_children(),scope);
			}else
			if(exp.xtype()==Exp.Exp_Type.Function) {
				return new Function.UserFunction((Exp.FunctionExp)exp, scope);
			}
		}else {
			if(exp.xtype()==Exp.Exp_Type.ID) {
				Exp.IdExp tmp=(Exp.IdExp)exp;
				return Node.kvs_find1st(scope, tmp.Value());
			}else
	        if(exp.xtype()==Exp.Exp_Type.String){
	            return ((Exp.StrExp)exp).Value();
	        }else
	        if(exp.xtype()==Exp.Exp_Type.Int){
	            return ((Exp.IntExp)exp).Value();
	        }
		}
        /*
        else
        if(exp instanceof Exp.FloatExp){
            return ((Exp.FloatExp)exp).value;
        }
		*/
		return null;
	}
}
