package s;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import s.Exp.BracketsExp;
import s.util.Code;
import s.util.Location;

public class Eval {
	/***tokenize***********************************************************************************************************************************************/
	static Character[] trans_map= {
			'n','\n',
			'r','\r',
			't','\t'
	};
	/**
	 * 解析字符串、注释
	 * @param code
	 * @param end
	 * @return
	 * @throws LocationException 
	 */
	static String parseStr(Code code,char end) throws LocationException{
	    Location loc=code.currentLoc();
	    code.shift();
	    int start=code.index();
	    boolean nobreak=true;
	    int trans_time=0;
	    while(code.current()!=null && nobreak){
	        if(code.current()==end){
	            nobreak=false;
	        }else{
	            if(code.current()=='\\'){
	            	code.shift();
	            	trans_time++;
	            }
	            code.shift();
	        }
	    }
	    if(code.current()==null){
	        throw code.msgThrow(end);
	    }else{
	    	String s=code.substr(start, code.index()-start);
	    	if(trans_time!=0) {
	    		try {
	    			s=mb.Util.string_from_trans(s,end,trans_map,trans_time);
	    		}catch(Exception e){
	        		throw new LocationException(e.getMessage(),loc);
	    		}
	    	}
	        code.shift();
	        return s;
	    }
	}
	
	static char[] brackets_in={'(','[','{'};
	static char[] brackets_out={')',']','}'};
	
	static boolean has(char c,char[] cs){
	    boolean ret=false;
	    for(int i=0;i<cs.length;i++){
	        if(cs[i]==c){
	            ret=true;
	        }
	    }
	    return ret;
	}
	
	static boolean isNotEnd(char c){
	    return !(Character.isWhitespace(c)|| has(c,brackets_in) || has(c,brackets_out));
	}
	
	
	static boolean isInt(String s){
	    boolean ret=true;
	    int index=0;
	    if(s.charAt(0)=='-'){
	        index=1;
	    }
	    while(index<s.length()){
	        if(!Character.isDigit(s.charAt(index))){
	            ret=false;
	        }
	        index++;
	    }
	    return ret;
	}
	
	static boolean isFloat(String s){
	    boolean ret=true;
	    int index=0;
	    if(s.charAt(0)=='-'){
	        index=1;
	    }
	    boolean noPoint=true;
	    while(index<s.length()){
	        char c=s.charAt(index);
	        if(c=='.'){
	            if(noPoint){
	                noPoint=false;
	            }else{
	                ret=false;
	            }
	        }else
	        if(!Character.isDigit(c)){
	            ret=false;
	        }
	        index++;
	    }
	    return ret;
	}
	public static s.Node<Token> tokenize(String codes,char lineSplit) throws LocationException{
	    Code code=new Code(codes,lineSplit);
	    s.Node<Token> tokens=null;
	    while(code.current()!=null){
	        if(Character.isWhitespace(code.current())){
	        	code.shift();
	        }else
	        if(code.current()=='"'){
	            //字符串
	            Location loc=code.currentLoc();
	            String s=parseStr(code,'"');
	            loc.setLength(s.length()+2);
	            tokens=new s.Node<Token>(new Token(s,loc,Token.Type.Str),tokens);
	        }else
	        if(code.current()=='`'){
	            //注释
	            Location loc=code.currentLoc();
	            String s=parseStr(code,'`');
	            loc.setLength(s.length()+2);
	            tokens=new s.Node<Token>(new Token(s,loc,Token.Type.Comment),tokens);
	            //不处理
	        }else
	        if(has(code.current(),brackets_in)){
	            //([{
	            Location loc=code.currentLoc();
	            loc.setLength(1);
	            tokens=new s.Node<Token>(new Token(code.current()+"",loc,Token.Type.BraL),tokens);
	            code.shift();
	        }else
	        if(has(code.current(),brackets_out)){
	            //)]}
	            Location loc=code.currentLoc();
	            loc.setLength(1);
	            tokens=new s.Node<Token>(new Token(code.current()+"",loc,Token.Type.BraR),tokens);
	            code.shift();
	        }else
	        {
	            //id
	            Location loc=code.currentLoc();
	            int start=code.index();
	            while(code.current()!=null && isNotEnd(code.current())){
	                code.shift();
	            }
	            String s=code.substr(start, code.index()-start);
	            loc.setLength(s.length());
	        	//长度超过1
	            if(s.charAt(0)=='\'' && s.length()!=1){
	                //转义
	                if(s.length()==1){
	                    code.msgThrow('\'');
	                }else{
	                    s=s.substring(1);
	                    tokens=new s.Node<Token>(new Token(s,loc,Token.Type.Quote),tokens);
	                }
	            }else
	            if((!s.equals("-"))&&isInt(s)){
	                tokens=new s.Node<Token>(new Token(s,loc,Token.Type.Int),tokens);
	            }else
	            /*
	            if(isFloat(s)){
	                tokens.add(new Token(s,loc,Token.Type.Float));
	            }
	            else
	            */
	            {
	            	tokens=new s.Node<Token>(new Token(s,loc,Token.Type.Id),tokens);
	            }
	        }
	    }
	    /*
	     *因为没有翻转，与文章完全相反 
	     */
	    return tokens;
	}
	/***解析***********************************************************************************************************************************************/
	static String[] kvs_quote= {
			"(",")",
			"[","]",
			"{","}"
	};
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
	static Node<Exp> whenFunction(Node<Exp> children){
		return children;
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
				String right=mb.Util.kvs_find1st(kvs_quote, x.Value());
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
	/***执行***********************************************************************************************************************************************/
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
			tokenize(codes,lineSplit)
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
			tokenize(codes,lineSplit)
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
	 static boolean onload=false;
	 
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
	 /**
	  * 执行路径文件
	  * @param path
	  * @param library
	  * @return
	  * @throws Exception
	  */
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
				if(onload) {
					throw new Exception("加载期间不允许其它加载");
				}else{
					onload=true;
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
					onload=false;
					return file_def.result;
				}
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
