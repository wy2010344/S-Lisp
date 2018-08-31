package s;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import s.Exp.BracketsExp;
import s.util.Location;
import s.util.threeQuote.Token;

public class Eval {
	static String[] kvs_quote= {
			"(",")",
			"[","]",
			"{","}"
	};
	static String quoteLeftToRight(String left) {
		int i=0;
		String right=null;
		while(i<kvs_quote.length&&right==null) {
			String t_l=kvs_quote[i];
			i++;
			String t_r=kvs_quote[i];
			i++;
			if(t_l.equals(left)) {
				right=t_r;
			}
		}
		return right;
	}
	static String quoteRightToLeft(String right) {
		int i=0;
		String left=null;
		while(i<kvs_quote.length&&left==null) {
			String t_l=kvs_quote[i];
			i++;
			String t_r=kvs_quote[i];
			i++;
			if(t_r.equals(right)) {
				left=t_l;
			}
		}
		return left;
	}
	static class Cache{
		public Cache(Token value,Node<Exp> children) {
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
	
	/*
	static Node<Exp> reverse(BracketsExp exps){
		
	}
	static Node<Exp> whenFunction(Node<Exp> children){
		Node<Exp> caches=null;
		for(Node<Exp> t=children;t!=null;t=t.Rest()) {
			Exp e=t.First();
			if(e.isBracket()) {
				if(e.xtype()==Exp.Exp_Type.Call) {
					
				}else
				if(e.xtype()==Exp.Exp_Type.List) {
					caches=new Node<Exp>(reverse((BracketsExp) e),caches);
				}else
				{
					caches=new Node<Exp>(e,caches);
				}
			}else {
				caches=new Node<Exp>(e,caches);
			}
		}
		return caches;
	}
	*/

	static Node<Exp> whenFunction(Node<Exp> children){
		return children;
	}
	/**
	 * tokens未翻转(a b c)->) c b a (
	 * 递归得太多！
	 * @param tokens
	 * @return
	 * @throws Exception 
	 */
	@Deprecated
	public static Exp.FunctionExp parse(
			Node<Token> tokens,//供解析的列表
			Node<Cache> caches,//栈(括号,列表)
			Node<Exp> children//平行列表
			) throws Exception{
		if(tokens==null) {
			return new Exp.FunctionExp(null, children, null);
		}else {
			Token x=tokens.First();
			Node<Token> xs=tokens.Rest();
			if(x.Type()==Token.Type.BraR) {
				return parse(
					xs,
					new Node<Cache>(
						new Cache(x,children),
						caches
					),
					null
				);
			}else
			if(x.Type()==Token.Type.BraL) {
				Cache cache=caches.First();
				String c_right=cache.token().Value();
				String right=quoteLeftToRight(x.Value());
				if(c_right.equals(right)){
					Exp e=null;
					if("}".equals(c_right)) {
						//上一级是函数
						e=new Exp.FunctionExp(
							cache.token(),
							whenFunction(children),
							x
						);
					}else
					if("]".equals(c_right)) {
						e=new Exp.ListExp(
							cache.token(), 
							children, 
							x
						);
					}else
					if(")".equals(c_right)) {
						e=new Exp.CallExp(
							cache.token(), 
							children, 
							x
						);
					}
					return parse(
						xs,
						caches.Rest(),
						new Node<Exp>(e,cache.Children())
					);
				}else {
					String msg="括号不匹配"+x.Value()+":"+c_right+"在位置:"+x.Loc().toString();
					System.out.println(msg);
					throw new Exception(msg);
				}
			}else {
				Exp e=null;
				if(x.Type()==Token.Type.Str) {
					e=new Exp.StrExp(x);
				}else
				if(x.Type()==Token.Type.Int) {
					e=new Exp.IntExp(x);
				}else{
					//ID
					Cache cache=caches.First();
					if(cache==null) {
						System.out.println(caches.toString());
					}
					if("]".equals(cache.token().Value()))
					{
						//中括号号
						if(x.Type()==Token.Type.Quote) {
							e=new Exp.IdExp(x);
						}else 
						if(x.Type()==Token.Type.Id){
							e=new Exp.StrExp(x);
						}
					}else {
						//其它括号
						if(x.Type()==Token.Type.Quote) {
							e=new Exp.StrExp(x);
						}else
						if(x.Type()==Token.Type.Id){
							e=new Exp.IdExp(x);
						}
					}
				}
				if(e==null) {
					if(x.Type()==Token.Type.Comment ) {
						return parse(
							xs,
							caches,
							children
						);
					}else {
						String msg="出错，未解析正确"+x.Type()+":"+x.Value();
						System.out.println(msg);
						throw new Exception(msg);
					}
				}else {
					return parse(
						xs,
						caches,
						new Node<Exp>(e,children)
					);
				}
			}
		}
	}
	@Deprecated
	public static Exp.FunctionExp parseV1(
			Node<Token> tokens//供解析的列表
	) throws Exception
	{
		return parse(
				tokens,
				new Node<Cache>(
					new Cache(
						new Token(
							"}",
							new Location(0,0,0),
							Token.Type.BraR
						),
						null
					),null
				),
				null);
	}	
	/**
	 * 转成非递归调用的while语句
	 * @param tokens
	 * @return
	 * @throws Exception
	 */
	public static Exp.FunctionExp parse(final Node<Token> tokens) throws Exception{
		//栈(括号,列表)
		Node<Cache> caches=new Node<Cache>(
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
				caches=new Node<Cache>(
					new Cache(x,children),
					caches
				);
				children=null;
			}else
			if(x.Type()==Token.Type.BraL) {
				Cache cache=caches.First();
				String c_right=cache.token().Value();
				String right=quoteLeftToRight(x.Value());
				if(c_right.equals(right)){
					Exp e=null;
					if("}".equals(c_right)) {
						//上一级是函数
						e=new Exp.FunctionExp(
							cache.token(),
							whenFunction(children),
							x
						);
					}else
					if("]".equals(c_right)) {
						e=new Exp.ListExp(
							cache.token(), 
							children, 
							x
						);
					}else
					if(")".equals(c_right)) {
						e=new Exp.CallExp(
							cache.token(), 
							children, 
							x
						);
					}
					caches=caches.Rest();
					children=new Node<Exp>(e,cache.Children());
				}else {
					String msg="括号不匹配"+x.Value()+":"+c_right+"在位置:"+x.Loc().toString();
					System.out.println(msg);
					throw new Exception(msg);
				}
			}else {
				Exp e=null;
				if(x.Type()==Token.Type.Str) {
					e=new Exp.StrExp(x);
				}else
				if(x.Type()==Token.Type.Int) {
					e=new Exp.IntExp(x);
				}else{
					//ID
					Cache cache=caches.First();
					if("]".equals(cache.token().Value()))
					{
						//中括号号
						if(x.Type()==Token.Type.Quote) {
							e=new Exp.IdExp(x);
						}else 
						if(x.Type()==Token.Type.Id){
							e=new Exp.StrExp(x);
						}
					}else {
						//其它括号
						if(x.Type()==Token.Type.Quote) {
							e=new Exp.StrExp(x);
						}else
						if(x.Type()==Token.Type.Id){
							e=new Exp.IdExp(x);
						}
					}
				}
				if(e==null) {
					if(x.Type()!=Token.Type.Comment ) {
						String msg="出错，未解析正确"+x.Type()+":"+x.Value();
						System.out.println(msg);
						throw new Exception(msg);
					}
				}else {
					children=new Node<Exp>(e,children);
				}
			}
		}
		return new Exp.FunctionExp(null, children, null);
	}
	/**
	 * 执行文本
	 * @param codes
	 * @param scope
	 * @param lineSplit
	 * @return
	 * @throws LocationException
	 */
	public static Object run(String codes,Node<Object> scope,char lineSplit) throws LocationException, Exception{
		Exp.FunctionExp	exp=parse(
			s.util.threeQuote.Eval.tokenize(codes,lineSplit)
		);
        return (new Function.UserFunction(exp,scope)).exec(null);
	}
	/**
	 * 主要是控制台使用
	 * @param codes
	 * @param qr
	 * @param lineSplit
	 * @return
	 * @throws LocationException 
	 * @throws Exception
	 */
	public static Object run(String codes,QueueRun qr,char lineSplit) throws LocationException, Exception {
		Exp.FunctionExp lbe=parse(
			s.util.threeQuote.Eval.tokenize(codes,lineSplit)
		);
		Object r=null;
		for(Node<Exp> t=lbe.Children();t!=null;t=t.Rest()) {
			r=qr.run(t.First());
		}
		return r;
	}
	 static class Result{
		 public Object result;
	 }
	 static HashMap<String,Result> files_defs=new HashMap<String,Result>();
	 
	 /*计算绝对路径*/
	 static String calculate_path(String path,String c_path) {
			if(c_path.startsWith(".")) {
				//相对路径
				String current_path=path;//按理说应该有闭包，但这里没有。
				String[] pnodes=current_path.split("/");
				String[] cpnodes=c_path.split("/");
				ArrayList<String> n_nodes=new ArrayList<String>();
				for(int i=0;i<pnodes.length-1;i++) {
					n_nodes.add(pnodes[i]);
				}
				for(int i=0;i<cpnodes.length;i++) {
					String cpnode=cpnodes[i];
					if("..".equals(cpnode)) {
						//回到上级
						n_nodes.remove(n_nodes.size()-1);
					}else
					if(".".equals(cpnode)) {
						//不处理
					}else
					{
						n_nodes.add(cpnode);
					}
				}
				StringBuilder sb=new StringBuilder();
				for(int i=0;i<n_nodes.size();i++) {
					sb.append(n_nodes.get(i)).append("/");
				}
				sb.setLength(sb.length()-"/".length());
				c_path=sb.toString();
			}
			return c_path;
	 }
	 public static Object run(final String path,final Node<Object> library) throws Exception{
		 String codes=mb.Util.readTxt(path, "\n", "UTF-8");
		 Node<Object> pkg=Library.kvs_extend("calculate-path", new Function() {
			@Override
			public Object exec(Node<Object> node) throws Exception {
				// TODO Auto-generated method stub
				return calculate_path(path,(String)node.First());
			}

			@Override
			public Type ftype() {
				// TODO Auto-generated method stub
				return Function.Type.buildIn;
			}
		 },library);
		 pkg=Library.kvs_extend("load", new Function() {
			@Override
			public Object exec(Node<Object> node) throws Exception{
				// TODO Auto-generated method stub
				String c_path=(String)node.First();
				if(c_path==null) {
					System.out.println("路径参数为空？");
					return null;
				}
				c_path=calculate_path(path,c_path);
				//绝对路径
				Result file_def=files_defs.get(c_path);
				if(file_def==null) {
					/**
					文件加载了一次，就不加载第二次，使用第一次结果
					 */
					file_def=new Result();
					try {
						file_def.result=Eval.run(c_path,library);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println(c_path);
						e.printStackTrace();
					}
					files_defs.put(c_path, file_def);
				}
				return file_def.result;
			}

			@Override
			public Type ftype() {
				// TODO Auto-generated method stub
				return Function.Type.buildIn;
			}
		 },pkg);
		 try {
			return run(codes,pkg,'\n');
		} catch (LocationException e) {
			// TODO Auto-generated catch block
			e.setFile(path);
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	 }
}
