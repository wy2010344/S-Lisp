package s.library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import s.Function;
import s.Node;
import s.Token;

public class Load implements Function{
	static class PathOf implements Function {
		 private String base_path;
		 public PathOf(String base_path) {
			 this.base_path=base_path;
		 }
		 /*计算绝对路径*/
		 public static String calculate_path(String path,String c_path) {
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
		@Override
		public String toString() {
			return "pathOf";
		}
		@Override
		public Object exec(Node<Object> args) throws Exception {
			if(args==null) {
				return base_path;
			}else {
				return calculate_path(base_path,(String)args.First());
			}
		}

		@Override
		public Type ftype() {
			// TODO Auto-generated method stub
			return Function.Type.buildIn;
		}
	 }
	 static class Result{
		 public Object result;
	 }
	 static HashMap<String,Result> files_defs=new HashMap<String,Result>();
	 static boolean onload=false;
	 public static Object run_e(String c_path,Node<Object> scope,char split) throws Exception {
			if(onload) {
				throw new Exception("加载期间不允许其它加载");
		}else{
			onload=true;
			//绝对路径
			Result file_def=files_defs.get(c_path);
			if(file_def==null) {
				/**
				文件加载了一次，就不加载第二次，使用第一次结果
				 */
				file_def=new Result();
				try {
					String codes=mb.Util.readTxt(c_path, ""+split, "UTF-8");
					Node<Token> tokens=Token.tokenize(codes, split);
					s.exp.FunctionExp exp=s.exp.FunctionExp.parse(tokens);
					scope=Node.kvs_extend("load", new Load(c_path,scope,split), scope);
					scope=Node.kvs_extend("pathOf", new PathOf(c_path), scope);
					file_def.result=new Function.UserFunction(exp, scope).exec(null);
				} catch (IOException e) {
					System.out.println(c_path);
					e.printStackTrace();
				}
				files_defs.put(c_path, file_def);
			}
			onload=false;
			return file_def.result;
		}
	}
	 
	public Load(String base_path,Node<Object> scope,char split) {
		 this.base_path=base_path;
		 this.scope=scope;
		 this.split=split;
	}
	private String base_path;
	private Node<Object> scope;
	private char split;
	@Override
	public Object exec(Node<Object> node) throws Exception {
		String c_path=(String)node.First();
		if(c_path==null) {
			System.out.println("路径参数为空？");
			return null;
		}else {
			c_path=PathOf.calculate_path(base_path,c_path);
			return run_e(c_path,scope,split);
		}
	}
	@Override
	public String toString() {
		return "load";
	}
	@Override
	public Type ftype() {
		// TODO Auto-generated method stub
		return Function.Type.buildIn;
	}

}
