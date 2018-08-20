package s;
import s.util.threeQuote.Token;
public abstract class Exp {
    public Token token;
    public static void repeat(StringBuilder sb,int indent){
        int i=0;
        while(i<indent){
            sb.append("  ");
            i++;
        }
    }
    public static String replaceQuote(String v) {
    	String x=v.replace("\\", "\\\\")
    			  .replace("\"", "\\\"")
    			  .replace("\r", "\\r")
    			  .replace("\t", "\\t")
    			  .replace("\n", "\\n");
    	/*
    	System.out.println(v);
    	System.out.println(x);
    	*/
    	return x;
    }
    public BracketsExp parent;
    //不换行，组合
    protected abstract void toString(StringBuilder sb);
    //换行
    public abstract String toString(int indent);
    //换行，组合
	protected abstract void toString(StringBuilder sb,int indent);
	
	
	 //括号
    public static abstract class BracketsExp extends Exp{
    	public BracketsExp(String left,String right) {
    		this.left=left;
    		this.right=right;
    	}
    	String left;
    	String right;
    	private Node children;
    	public Node Children() {
    		return children;
    	}
    	public void append(Exp child) {
    		children=new Node(child,children);
    	}
    	public void reverstChildren(){
    		children=Library.reverse(children);
    	}
        @Override
        public String toString() {
        	 StringBuilder sb=new StringBuilder();
             toString(sb);
             return sb.toString();
        }
        @Override
        public String toString(int indent) {
            // TODO Auto-generated method stub
       		StringBuilder sb=new StringBuilder();
            toString(sb,indent);
            return sb.toString();
        }
    	//不换行的工具方法 
    	@Override
    	protected void toString(StringBuilder sb) {
    		sb.append(left).append(" ");
            for(Node tmp=children;tmp!=null;tmp=tmp.Rest()) {
         	   Exp child=(Exp)tmp.First();
         	   child.toString(sb);
         	   sb.append(" ");
            }
            sb.append(right);
    	}
    	//换行的工具方法
    	@Override
    	protected void toString(StringBuilder sb,int indent){
           repeat(sb,indent);
           sb.append(left).append("\n");
           
           for(Node tmp=children;tmp!=null;tmp=tmp.Rest()) {
        	   Exp child=(Exp)tmp.First();
        	   child.toString(sb, indent);
        	   sb.append("\n");
           }
           
           repeat(sb,indent);
           sb.append(right);
       }
    }

    //小括号
    public static class SBracketsExp extends BracketsExp{
        public SBracketsExp() {
			super("(", ")");
		}
    }
    
    //中括号
    public static class MBracketsExp extends BracketsExp{
		public MBracketsExp() {
			super("[","]");
		}
    }
    
    //花括号
    public static class LBracketsExp extends BracketsExp{
		public LBracketsExp() {
			super("{", "}");
			// TODO Auto-generated constructor stub
		}
    }
    
    public static abstract class AtomExp extends Exp{
    	@Override
    	public String toString() {
    		StringBuilder sb=new StringBuilder();
    		toString(sb);
    		return sb.toString();
    	}
    	@Override
    	public String toString(int indent) {
    		StringBuilder sb=new StringBuilder();
            repeat(sb,indent);
    		toString(sb);
    		return sb.toString();
    	}
		@Override
		protected void toString(StringBuilder sb, int indent) {
			// TODO Auto-generated method stub
            repeat(sb,indent);
    		toString(sb);
		}
        protected void toString(StringBuilder sb,Object value,String before,String after){
            sb.append(before).append(value).append(after);
        }
    }
    //数字
    public static class IntExp extends AtomExp{
        public Integer value;
		@Override
		protected void toString(StringBuilder sb) {
			// TODO Auto-generated method stub
			toString(sb,value,"","");
		}
    }
    
    /*
    public static class FloatExp extends Exp{
        public Float value;

        @Override
        public String toString(int indent) {
            // TODO Auto-generated method stub
            return toString(indent,value,"","");
        }
        @Override
        public String toString() {
        	return ""+value;
        }
    }
    */
    
    //id
    public static class IdExp extends AtomExp{
        public String value;
		@Override
		protected void toString(StringBuilder sb) {
			// TODO Auto-generated method stub
    		if(token.Original_type()==Token.Type.Quote) {
    			//中括号引用转id
                toString(sb,value,"'","");
    		}else {
    			//默认id
    			toString(sb,value,"","");
    		}
		}
    }
    
    //字符串
    public static class StrExp extends AtomExp{
        public String value;
        @Override
        public String toString() {
        	return value;
        }
		@Override
		protected void toString(StringBuilder sb) {
			// TODO Auto-generated method stub
    		if(token.Original_type()==Token.Type.Quote) {
    			//小括号、大括号引用转字符串
    			toString(sb,value,"'","");
    		}else 
    		if(token.Original_type()==Token.Type.Id){
    			//中括号id转字符串
    			toString(sb,value,"","");
    		}else{
    			//默认字符串
            	toString(sb,Exp.replaceQuote(value),"\"","\"");
    		}
		}
    }
}
