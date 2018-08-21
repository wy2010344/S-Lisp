package s;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import s.Exp.LBracketsExp;
import s.util.threeQuote.Token;

public class Eval {
	static HashMap<String,String> opposite_quote;
	static {
		opposite_quote=new HashMap<String,String>();
		opposite_quote.put("(", ")");
		opposite_quote.put("[", "]");
		opposite_quote.put("{", "}");
	}
	public static Exp.LBracketsExp parse(List<Token> tokens) throws LocationException{
		Exp.LBracketsExp root=new Exp.LBracketsExp();
		
		Exp.BracketsExp current=root;
		int i=0;
		while(i<tokens.size()) {
			Token token=tokens.get(i);
			if(token.Type()==Token.Type.BraL) {
				Exp.BracketsExp tmp=null;
                if("(".equals(token.Value())){
                    tmp=new Exp.SBracketsExp();
                }else
                if("[".equals(token.Value())){
                    tmp=new Exp.MBracketsExp();
                }else
                if("{".equals(token.Value())){
                    tmp=new Exp.LBracketsExp();
                }
                tmp.parent=current;
                tmp.token=token;
                current.append(tmp);
                current=tmp;
			}else
			if(token.Type()==Token.Type.BraR) {
				if(!token.Value().equals(opposite_quote.get(current.token.Value()))) {
					throw new LocationException("括号不匹配"+current.token.Value()+token.Value(),token.Loc());
				}else {
	                Exp.BracketsExp tmp=current.parent;
					current.reverstChildren();
	                current=tmp;
	                if(tmp==null) {
	                	throw new LocationException("括号不匹配，过早结束",token.Loc());
	                }
				}
			}else {
                if(current instanceof Exp.SBracketsExp || current instanceof Exp.LBracketsExp){
                    //小括号\大括号内
                    if(token.Type()==Token.Type.Quote){
                        //'
                        token.Type(Token.Type.Str);
                    }
                }else
                if(current instanceof Exp.MBracketsExp){
                    //中括号内
                    if(token.Type()==Token.Type.Id){
                        //识别为字符串
                        token.Type(Token.Type.Str);
                    }else
                    if(token.Type()==Token.Type.Quote){
                        //识别为id
                        token.Type(Token.Type.Id);
                    }
                }
                
                Exp exp=null;
                if(token.Type()==Token.Type.Id){
                    //id
                    Exp.IdExp tmp=new Exp.IdExp();
                    tmp.value=token.Value();
                    exp=tmp;
                }else
                if(token.Type()==Token.Type.Str){
                    //字符串
                    Exp.StrExp tmp=new Exp.StrExp();
                    tmp.value=token.Value();
                    exp=tmp;
                }else
                if(token.Type()==Token.Type.Int){
                    //int
                    Exp.IntExp tmp=new Exp.IntExp();
                    tmp.value=Integer.parseInt(token.Value());
                    exp=tmp;
                }
                /*
                else
                if(token.Type()==Token.Type.Float){
                    //float
                    Exp.FloatExp tmp=new Exp.FloatExp();
                    tmp.value=Float.parseFloat(token.Value());
                    exp=tmp;
                }
                */
                if(exp!=null) {
                    exp.token=token;
                    exp.parent=current;
                    current.append(exp);
                }
            }
            i++;
		}
		root.reverstChildren();
		return root;
	}
	static Node calNode(Node list,Node scope) throws Exception {
		Node r=null;
		for(Node x=list;x!=null;x=x.Rest()) {
			Exp xe=(Exp)x.First();
			Object xv=interpret(xe,scope);
			r=new Node(xv,r);
		}
		return Library.reverse(r);
	}
	static void errorMessage(String message,Exp.SBracketsExp exp,Node children) throws Exception {
		String exp_msg=exp.toString();
		String result_msg=children.toString();
		throw new Exception(
							message+":\n"+
						    exp_msg+"\n"+
						    result_msg+"\n"+
						    exp.token.Loc().toString()
						   );
	}
	public static Object interpret(Exp exp,Node scope) throws Exception{ 
		if(exp instanceof Exp.SBracketsExp) {
			Exp.SBracketsExp tmp=(Exp.SBracketsExp)exp;
			Node children=calNode(tmp.Children(),scope);
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
		if(exp instanceof Exp.MBracketsExp) {
			Exp.MBracketsExp tmp=(Exp.MBracketsExp)exp;
			return calNode(tmp.Children(),scope);
		}else
		if(exp instanceof Exp.LBracketsExp) {
			return new Function.UserFunction((Exp.LBracketsExp)exp, scope);
		}else
		if(exp instanceof Exp.IdExp) {
			Exp.IdExp tmp=(Exp.IdExp)exp;
			return Library.kvs_find1st(scope, tmp.value);
		}else
        if(exp instanceof Exp.StrExp){
            return ((Exp.StrExp)exp).value;
        }else
        if(exp instanceof Exp.IntExp){
            return ((Exp.IntExp)exp).value;
        }
        /*
        else
        if(exp instanceof Exp.FloatExp){
            return ((Exp.FloatExp)exp).value;
        }
		*/
		return null;
	}
	
	/**
	 * 执行文本
	 * @param codes
	 * @param scope
	 * @param lineSplit
	 * @return
	 * @throws LocationException
	 */
	public static Object run(String codes,Node scope,char lineSplit) throws LocationException, Exception{
        return ((Function)interpret(
	        		parse(
	        				s.util.threeQuote.Eval.tokenize(codes,lineSplit)
	        		),
	        		scope
	        	)).exec(null);
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
		LBracketsExp lbe=parse(s.util.threeQuote.Eval.tokenize(codes,lineSplit));
		Object r=null;
		for(Node t=lbe.Children();t!=null;t=t.Rest()) {
			r=qr.run((Exp)t.First());
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
	 public static Object run(final String path,final Node library) throws Exception{
		 String codes=mb.Util.readTxt(path, "\n", "UTF-8");
		 Node pkg=Library.kvs_extend("calculate-path", new Function() {
			@Override
			public Object exec(Node node) throws Exception {
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
			public Object exec(Node node) throws Exception{
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
