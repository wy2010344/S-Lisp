package s.library;

import s.Function;
import s.Node;
import s.Token;

public class Parse implements Function{
	public Parse(Node<Object> base_scope,char line_split) {
		this.base_scope=base_scope;
		this.line_split=line_split;
		
	}
	private Node<Object> base_scope;
	private char line_split;
	@Override
	public Object exec(Node<Object> args) throws Exception {
		// TODO Auto-generated method stub
		String txt=(String)args.First();
		args=args.Rest();
		Node<Object> scope=base_scope;
		if(args!=null) {
			scope=(Node<Object>)args.First();
		}
		return eval(txt,scope,line_split);
	}

	public static Object eval(String code,Node<Object> scope,char line_split) throws Exception {
		Node<Token> tokens=Token.tokenize("{"+code+"}", line_split);
		s.exp.FunctionExp exp=s.exp.FunctionExp.parse(new s.exp.Exp.TokenQueue(tokens));
		return new Function.UserFunction(exp, scope).exec(null);
	}
	@Override
	public Type ftype() {
		// TODO Auto-generated method stub
		return Function.Type.buildIn;
	}

	@Override
	public String toString() {
		return "parse";
	}
}
