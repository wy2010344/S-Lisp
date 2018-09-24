package s;

import s.exp.*;

public class QueueRun {
	public QueueRun(Node<Object> scope) {
		this.scope=scope;
	}
	public Node<Object> scope;
	public Object exec(s.exp.FunctionExp exp) throws Exception {
		Object r=null;
		for(Node<Exp> tmp=exp.Children();tmp!=null;tmp=tmp.Rest()) {
			Exp x=tmp.First();
			if(x instanceof s.exp.LetExp) {
				scope=(Node<Object>)x.eval(scope);
				r=null;
			}else {
				r=x.eval(scope);
			}
		}
		return r;
	}
	public Object exec(String str,char lineSplit) throws Exception {
		str="{"+str+"}";
		Node<Token> tokens=Token.tokenize(str, lineSplit);
		s.exp.FunctionExp exp=s.exp.FunctionExp.parse(new s.exp.Exp.TokenQueue(tokens));
		return exec(exp);
	}
}
