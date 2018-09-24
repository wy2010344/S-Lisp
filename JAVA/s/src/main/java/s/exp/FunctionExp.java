package s.exp;

import s.Location;
import s.LocationException;
import s.Node;
import s.Token;

public class FunctionExp extends BracketsExp{
	public FunctionExp(Token first, Node<Exp> children, Token last) {
		super(first, children, last);
	}
	@Override
	public String left() {
		// TODO Auto-generated method stub
		return "{";
	}
	@Override
	public String right() {
		// TODO Auto-generated method stub
		return "}";
	}

	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.Function;
	}

	/***解析***********************************************************************************************************************************************/
	static String[] kvs_quote= {
			"(",")",
			"[","]",
			"{","}"
	};
	static class Cache{
		public Cache(
			Token value,
			Node<Exp> children
		) {
			this.value=value;
			this.children=children;
		}
		private Token value;
		public Token token() {
			return value;
		}
		private Node<Exp> children;
		public Node<Exp> Children() {
			return children;
		}
	}
	static Node<Exp> whenFunction(Node<Exp> children){
		return children;
	}
	/**
	 * 转成非递归调用的while语句
	 * @param tokens
	 * @return
	 * @throws LocationException 
	 * @throws Exception
	 */
	public static FunctionExp parse(final Node<Token> tokens) throws LocationException{
		//栈(括号,列表)
		Node<Cache> caches=Node.extend(
			new Cache(
				new Token(
					"}",
					new Location(0,0,0),
					Token.Type.BraR
				),
				null
			),null
		);
		//平行列表
		Node<Exp> children=null;
		//所有tokens
		Node<Token> xs=tokens;
		while(xs!=null) {
			Token x=xs.First();
			xs=xs.Rest();
			if(x.Type()==Token.Type.BraR) {
				caches=Node.extend(
					new Cache(x,children),
					caches
				);
				children=null;
			}else
			if(x.Type()==Token.Type.BraL) {
				Cache cache=caches.First();
				String c_right=cache.token().Value();
				String right=mb.Util.kvs_find1st(kvs_quote, x.Value());
				if(c_right.equals(right)){
					Exp e=null;
					if("}".equals(c_right)) {
						//上一级是函数
						e=new FunctionExp(
							cache.token(),
							whenFunction(children),
							x
						);
					}else
					if("]".equals(c_right)) {
						e=new ListExp(
							cache.token(), 
							children, 
							x
						);
					}else
					if(")".equals(c_right)) {
						e=new CallExp(
							cache.token(), 
							children, 
							x
						);
					}
					caches=caches.Rest();
					children=Node.extend(e,cache.Children());
				}else {
					String msg="括号不匹配"+x.Value()+":"+c_right+"在位置:"+x.Loc().toString();
					System.out.println(msg);
					throw new LocationException(msg,x.Loc());
				}
			}else {
				Exp e=null;
				if(x.Type()==Token.Type.Str) {
					e=new StringExp(x);
				}else
				if(x.Type()==Token.Type.Int) {
					e=new IntExp(x);
				}else{
					//ID
					Cache cache=caches.First();
					if("]".equals(cache.token().Value()))
					{
						//中括号号
						if(x.Type()==Token.Type.Quote) {
							e=new IdExp(x);
						}else 
						if(x.Type()==Token.Type.Id){
							e=new StringExp(x);
						}
					}else {
						//其它括号
						if(x.Type()==Token.Type.Quote) {
							e=new StringExp(x);
						}else
						if(x.Type()==Token.Type.Id){
							e=new IdExp(x);
						}
					}
				}
				if(e==null) {
					if(x.Type()!=Token.Type.Comment ) {
						String msg="出错，未解析正确"+x.Type()+":"+x.Value();
						System.out.println(msg);
						throw new LocationException(msg,x.Loc());
					}
				}else {
					children=Node.extend(e,children);
				}
			}
		}
		return new FunctionExp(null, children, null);
	}
}
