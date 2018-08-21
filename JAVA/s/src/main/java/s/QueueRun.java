package s;

public class QueueRun {
	public QueueRun(Node scope) {
		this.scope=scope;
	}
	public Node scope;
	public Object run(Exp x) throws Exception {
		if(x instanceof Exp.SBracketsExp) {
			Exp.SBracketsExp sbe=(Exp.SBracketsExp)x;
			if(isLet(sbe)) {
				/**
				 * 也许应该进行命名规范的约束，未来再放开吧
				 * 前缀表达式添加.来分割
				 * 单个命名不允许.
				 * 减少命名重复的概率
				 */
				for(Node y=sbe.Children().Rest();y!=null;y=y.Rest().Rest()) {
					Exp val=(Exp)y.Rest().First();
					Object values=Eval.interpret(val, scope);
					scope=match(scope,(Exp)y.First(),values);
				}
				return null;
			}else {
				return Eval.interpret(x, scope);
			}
		}else{
			return Eval.interpret(x, scope);
		}
	}

	Node match(Node scope,Exp y,Object values) throws Exception {
		if(y instanceof Exp.SBracketsExp) {
			//小括号，多匹配
			scope=bracket_match(scope,((Exp.SBracketsExp)y).Children(),(Node)values);
		}else 
		if(y instanceof Exp.IdExp){
			String key=((Exp.IdExp)y).value;
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
	boolean isLet(Exp.SBracketsExp sbe) {
		boolean ret=false;
		Exp sbec=(Exp)sbe.Children().First();
		if(sbec instanceof Exp.IdExp) {
			Exp.IdExp sbec_id=(Exp.IdExp)sbec;
			if("let".equals(sbec_id.value)) {
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
	/**
	 * 匹配括号
	 * @param scope
	 * @param key_nodes
	 * @param val_nodes
	 * @return
	 * @throws Exception
	 */
	Node bracket_match(Node scope,Node key_nodes,Node val_nodes) throws Exception {
		Node kv=val_nodes;
		for(Node kt=key_nodes;kt!=null;kt=kt.Rest()) {
			Exp key_exp=(Exp)kt.First();
			String key=key_exp.to_value();
			if(key.startsWith("...") && kt.Rest()==null) {
				//以...xx结尾，匹配后续的列表
				key=key.substring(3);
				if(key.endsWith("*")) {
					//字典匹配
					scope=when_kvs_match(scope,key,kv);
				}else {
					//普通匹配
					scope=when_normal_match(scope,key,kv);
				}
			}else {
				Object value=null;
				if(kv!=null) {
					value=kv.First();
					kv=kv.Rest();
				}
				scope=match(scope,key_exp,value);
			}
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
	Node when_normal_match(Node scope,String key,Object kv) throws Exception {
		if(isValidKey(key)) {
			scope=Library.kvs_extend(key, kv,scope);
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
	Node when_kvs_match(Node scope,String key,Object values) throws Exception {
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
	Node kvs_match(Node scope,final String key_prefix,Object values) throws Exception {
		final Node kvs=(Node)values;
		/**
		 * 附加一个查询字典的函数
		 */
		scope=Library.kvs_extend(key_prefix, new Function() {
			@Override
			public Object exec(Node node)throws Exception {
				// TODO Auto-generated method stub
				return Library.kvs_find1st(kvs, (String)node.First());
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
		final Node svk=Library.reverse((Node)values);
		Node tmp_svk=svk;
		while(tmp_svk!=null) {
			Object vk=tmp_svk.First();
			tmp_svk=tmp_svk.Rest();
			Object tmp_k=tmp_svk.First();
			if(tmp_k instanceof String) {
				//是String类型
				String k=(String)tmp_svk.First();
				if(isValidKey(k)) {
					//命名合法，还需要去除空格等情况。
					scope=Library.kvs_extend(key_prefix+"."+k,vk,scope);//添加分割符，似乎又显得不和谐，与用户自己的风格
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
}
