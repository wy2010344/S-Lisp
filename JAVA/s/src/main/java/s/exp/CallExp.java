package s.exp;
import s.Function;
import s.LocationException;
import s.Node;
import s.QueueRun;
import s.Token;

public class CallExp extends BracketsExp{

	private CallExp(Token first,Exp fun, Node<Exp> r_children, Token last) {
		super(first,Node.extend(fun, Node.reverse(r_children)),last);
		this.funExp=fun;
		this.r_children=r_children;
	}
	private Exp funExp;
	private Node<Exp> r_children;
	public Node<Exp> R_children(){
		return r_children;
	}
	
	@Override
	public String left() {
		// TODO Auto-generated method stub
		return "(";
	}

	@Override
	public String right() {
		// TODO Auto-generated method stub
		return ")";
	}

	@Override
	public Exp_Type xtype() {
		// TODO Auto-generated method stub
		return Exp_Type.Call;
	}
	/**
	 * 
	 * 解析调用
	 * xs不能为空，为了报错
	 * 使用let作参数要警告
	 * Call/List还有执行顺序的问题，虽然禁止let表达式
	 * 但函数内可能在IO副作用。以及Cache副作用，不想每次翻转，所以计算r_children，直接就是顺序的计算结果。
	 * 但Call应该先计算函数吧，再颠倒计算参数吧。
	 * 如果未来支持宏，如and_q或or_q，即与预期不一致的多。
	 * @param xs
	 * @return
	 * @throws Exception
	 */
	public static CallExp parse(TokenQueue tq) throws Exception {
		Token first=tq.current();
		tq.shift();//排出(
		if(tq.notEnd()) {
			Exp fun=null;
			if(tq.current().Type()==Token.Type.Id) {
				//在作用域上寻找定义
				fun=IdExp.parse(tq);
			}else
			if(tq.current().Type()==Token.Type.BraL) {
				if("{".equals(tq.current().Value())){
					fun=FunctionExp.parse(tq);
				}else
				if("(".equals(tq.current().Value())) {
					fun=CallExp.parse(tq);
				}else {
					throw tq.error_token("函数调用时函数必须为id或{}，或()");
				}
			}else {
				throw tq.error_token("函数调用时函数必须为id或{}，或()");
			}
			Node<Exp> r_children=null;
			while(tq.notEnd() && tq.current().Type()!=Token.Type.BraR) {
				r_children=Node.extend(Exp.parse(tq),r_children);
			}
			Exception e=tq.check_end(")");
			if(e!=null) {
				throw e;
			}else {
				Token last=tq.current();
				tq.shift();//排出")"
				return new CallExp(first,fun,r_children,last);
			}
		}else {
			throw tq.error_token();
		}
		
	}

	protected LocationException errorMessage(String msg) {
		LocationException lox= new LocationException(
						funExp.toString()+msg,
						First().Loc()
		);
		return lox;
	}
	@Override
	public Object eval(Node<Object> scope) throws Exception {
		// TODO Auto-generated method stub
		Object o=funExp.eval(scope);
		if(o!=null) {
			if(o instanceof Function) {
				Function f=(Function)o;
				Node<Object> args=null;
				for(Node<Exp> t=r_children;t!=null;t=t.Rest()) {
					args=Node.extend(t.First().eval(scope),args);
				}
				try {
					return f.exec(args);
				}catch(LocationException lox) {
					lox.addStack(getPath(scope), First().Loc(), this.toString());
					throw lox;
				}
			}else {
				throw errorMessage("不是函数");
			}
		}else {
			throw errorMessage("未找到定义");
		}
	}
}
