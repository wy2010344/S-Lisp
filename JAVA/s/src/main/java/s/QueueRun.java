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
					if(y.First() instanceof Exp.SBracketsExp) {
						//小括号，多匹配
						scope=bracket_match(scope,((Exp.SBracketsExp)y.First()).Children(),(Node)values);
					}else {
						String key=((Exp.IdExp)y.First()).value;
						if(key.endsWith("*")) {
							//匹配kvs字典，添加前缀
							key=key.substring(0, key.length()-1);
							if(isValidKey(key)) {
								scope=kvs_match(scope,key,values);
							}else {
								throw new Exception(key+"不是合法的id");
							}
						}else {
							if(isValidKey(key)) {
								//单值
								scope=Library.kvs_extend(key,values,scope);
							}else {
								throw new Exception(key+"不是合法的id");
							}
						}
					}
				}
				return null;
			}else {
				return Eval.interpret(x, scope);
			}
		}else{
			return Eval.interpret(x, scope);
		}
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
		for(int i=0;i<key.length();i++) {
			char c=key.charAt(i);
			if(c=='.' || c =='*') {
				r=false;
			}else
			if(Character.isWhitespace(c)) {
				r=false;
			}
		}
		return r;
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
			String key=((Exp.IdExp)kt.First()).value;

			//以...xx结尾，匹配后续的列表
			if(key.startsWith("...") && kt.Rest()==null) {
				key=key.substring(3);
				if(isValidKey(key)) {
					scope=Library.kvs_extend(key, kv,scope);
				}else {
					throw new Exception(key+"不是合法的id");
				}
			}else {
				Object value=null;
				if(kv!=null) {
					value=kv.First();
					kv=kv.Rest();
				}
				if(isValidKey(key)) {
					scope=Library.kvs_extend(key,value,scope);
				}else {
					throw new Exception(key+"不是合法的id");
				}
			}
		}
		return scope;
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
		final Node svk=Library.reverse((Node)values);
		/**
		 * 附加一个查询字典的函数
		 */
		scope=Library.kvs_extend(key_prefix, new Function() {
			@Override
			public Object exec(Node node)throws Exception {
				// TODO Auto-generated method stub
				return Library.kvs_find1st(svk, key_prefix);
			}
			@Override
			public String toString() {
				//其中kvs为何？依赖闭包，在列表中会被转成'xx
				return null;//"kvs-match";//"{ (kvs-find1st kvs (first args)) }";
			}
		},scope);
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
