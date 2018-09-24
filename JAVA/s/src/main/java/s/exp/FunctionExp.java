package s.exp;

import s.LocationException;
import s.Node;
import s.Token;

public class FunctionExp extends BracketsExp{
	/**
	 * 
	 * @param first
	 * @param children
	 * @param last
	 */
	public FunctionExp(Token first, Node<Exp> children, Token last) {
		super(first,children,last);
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
	
	static boolean isLet(Node<Token> xs) {
		Token x=xs.First();
		if(x.Type()==Token.Type.Id && "let".equals(x.Value())) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 表达式只允许最后一个是id/str/[]/{}，中间的没有意义
	 * @param xs
	 * @return
	 * @throws Exception 
	 */
	public static FunctionExp parse(TokenQueue tq) throws Exception {
		Token first=tq.current();
		tq.shift();//排出"{"
		Node<Exp> r_children=null;
		while(tq.notEnd() && tq.current().Type()!=Token.Type.BraR) {
			if(tq.current().Type()==Token.Type.BraL) {
				if("{".equals(tq.current().Value()))
				{
					FunctionExp fun=FunctionExp.parse(tq);
					if(tq.notEnd()) {
						if(tq.current().Type()==Token.Type.BraR) {
							if(tq.current().Value().equals("}")) {
								r_children=Node.extend(fun, r_children);
							}else {
								throw tq.error_need_end("}");
							}
						}else {
							tq.warn("函数内部定义未调用，没有意义,"+fun.toString());
						}
					}else {
						throw tq.error_need_end("}");
					}
				}else
				if("[".equals(tq.current().Value())) {
					ListExp list=ListExp.parse(tq);
					/*
					 * list虽然计算结果不会使用，
					 * 但内部的值却会被计算到最终，
					 * 会发生计算，
					 * 比如log等都会打印，
					 * 相当于(list a b c d)这样的语法糖，即本身仍是函数调用。
					 */
					r_children=Node.extend(list, r_children);
				}else
				if("(".equals(tq.current().Value())){
					Token next=tq.next();
					if(next!=null){
						if(next.Type()==Token.Type.Id && "let".equals(next.Value())) {
							//let表达式
							LetExp let=LetExp.parse(tq);
							r_children=Node.extend(let, r_children);
						}else {
							CallExp call=CallExp.parse(tq);
							r_children=Node.extend(call, r_children);
						}
					}else {
						throw new LocationException("以(结束，不正确的语法",tq.current().Loc());
					}
				}
			}else {
				Token next=tq.next();
				if(next!=null) {
					if(next.Type()==Token.Type.BraR) {
						if(next.Value().equals("}")) {
							r_children=Node.extend(AtomExp.parse(tq),r_children);
						}else {
							throw tq.error_need_end("}");
						}
					}else {
						tq.warn("函数内部定义未调用，没有意义,"+tq.current().Type()+":"+tq.current().Value());
						tq.shift();
					}
				}else {
					throw tq.error_need_end("}");
				}
			}
		}
		Exception e=tq.check_end("}");
		if(e!=null) {
			throw e;
		}else {
			Token last=tq.current();
			tq.shift();//排出"}"
			return new FunctionExp(first,Node.reverse(r_children),last);
		}
	}
	@Override
	public Object eval(Node<Object> scope) throws Exception {
		// TODO Auto-generated method stub
		return new s.Function.UserFunction(this, scope);
	}
}
