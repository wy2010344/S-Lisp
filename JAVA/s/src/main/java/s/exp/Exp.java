package s.exp;

import s.Function;
import s.LocationException;
import s.Node;
import s.Token;
import s.exp.Exp.TokenQueue;

public abstract class Exp {
	public static enum Exp_Type{
		ID,
		String,
		Int,
		Bool,
		Call,//()
		List,//[]
		Function,//{}
		Let,//(let a b c d)
		Let_ID,//a
		Let_Bra,//(a b ...c)
		Let_Rest_ID//...x
	}
	public abstract Exp_Type xtype();
    public abstract Object eval(Node<Object> scope) throws Exception;
    //不换行，组合
    protected abstract void toString(StringBuilder sb);
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	/*****/
	protected static enum ParseStep{
		Function,
		List,
		Let,
		Call,/*函数调用*/
		LetBracket
	}
	protected static class Cache{
		public Cache(
			Token value,
			Node<Exp> children,
			ParseStep step
		) {
			this.value=value;
			this.children=children;
			this.step=step;
		}
		private Token value;
		public Token token() {
			return value;
		}
		private Node<Exp> children;
		public Node<Exp> Children() {
			return children;
		}
		private ParseStep step;
		public ParseStep Step() {
			return step;
		}
	}
	
	public static class TokenQueue{
		public TokenQueue(Node<Token> tokens) {
			this.tokens=tokens;
			jumpComment();
		}
		private void jumpComment() {
			while(tokens!=null && tokens.First().Type()==Token.Type.Comment) {
				tokens=tokens.Rest();
			}
		}
		private Node<Token> tokens;
		public Token current() {
			return tokens.First();
		}
		public void shift() {
			tokens=tokens.Rest();
			jumpComment();
		}
		public Token next() {
			if(tokens.Rest()!=null) {
				return tokens.Rest().First();
			}else {
				return null;
			}
		}
		public boolean notEnd() {
			return tokens!=null;
		}
		
		public void warn(String msg) {
			System.out.println("在位置:"+current().Loc().toString()+"\r\n"+msg);
		}
		
		public Exception error_need_end(String end) {
			return new Exception("期待"+end+"结束表达式，却到达结尾");
		}
		public LocationException error_token(String msg) {
			return new LocationException(msg+"-不期待的token:"+current().Type()+":"+current().Value(),current().Loc());
		}
		public LocationException error_token() {
			return error_token("");
		}
		public Exception check_end(String end) {
			if(notEnd()){
				if(end.equals(current().Value()))
				{
					//正常结束闭合
					return null;
				}else {
					return new LocationException("期待"+end+"结束表达式，却得到"+current().Value(),current().Loc());
				}
			}else
			{
				return error_need_end(end);
			}
		}
	}

	public static Exp parse(TokenQueue tq,boolean trans) throws Exception {
		if(tq.current().Type()==Token.Type.BraL) {
			if("{".equals(tq.current().Value()))
			{
				return FunctionExp.parse(tq);
			}else
			if("[".equals(tq.current().Value()))
			{
				return ListExp.parse(tq);
			}else
			if("(".equals(tq.current().Value()))
			{
				return CallExp.parse(tq);
			}else {
				throw tq.error_token();
			}
		}else {
			return AtomExp.parse(tq, trans);
		}
	}
	public static Exp parse(TokenQueue tq) throws Exception {
		return parse(tq,false);
	}
	
	public static String getPath(Node<Object> scope) {
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
}
