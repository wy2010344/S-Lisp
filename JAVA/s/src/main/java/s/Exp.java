package s;
import s.util.threeQuote.Token;
public abstract class Exp {
	public static enum Exp_Type{
		ID,
		String,
		Int,
		Call,//()
		List,//[]
		Function,//{}
		Let,//(let a b c d)
		Let_ID,//a
		Let_Bra,//(a b ...c)
		Let_Rest_ID//...x
	}
	public abstract boolean isBracket();
	public abstract Exp_Type xtype();
    public static void repeat(StringBuilder sb,int indent){
        int i=0;
        while(i<indent){
            sb.append("  ");
            i++;
        }
    }
    public static String replaceQuote(String v) {
    	StringBuilder sb=new StringBuilder();
    	sb.append("\"");
    	for(int i=0;i<v.length();i++) {
    		char c=v.charAt(i);
    		if(c=='\\') {
    			sb.append("\\\\");
    		}else
    		if(c=='"') {
    			sb.append("\\\"");
    		}else
    		{
    			Character x=mb.Util.trans_to_char(c);
    			if(x!=null) {
    				sb.append("\\").append(x);
    			}else {
    				sb.append(c);
    			}
    		}
    	}
    	sb.append("\"");
    	return sb.toString();
    }
    public BracketsExp parent;
    //不换行，组合
    protected abstract void toString(StringBuilder sb);
    //换行
    public abstract String toString(int indent);
    //换行，组合
	protected abstract void toString(StringBuilder sb,int indent);
	public abstract String to_value();
	
	
	 //括号
    public static abstract class BracketsExp extends Exp{
    	private Token first;
    	public Token First() {
    		return first;
    	}
    	private Token last;
    	public Token Last() {
    		return last;
    	}
    	private Node<Exp> children;
    	public Node<Exp> Children() {
    		return children;
    	}
    	public BracketsExp(Token first,Node<Exp> children,Token last) {
    		this.first=first;
    		this.children=children;
    		this.last=last;
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
    		sb.append(left()).append(" ");
            for(Node<Exp> tmp=children;tmp!=null;tmp=tmp.Rest()) {
         	   Exp child=tmp.First();
         	   child.toString(sb);
         	   sb.append(" ");
            }
            sb.append(right());
    	}
    	//换行的工具方法
    	@Override
    	protected void toString(StringBuilder sb,int indent){
           repeat(sb,indent);
           sb.append(left()).append("\n");
           
           for(Node<Exp> tmp=children;tmp!=null;tmp=tmp.Rest()) {
        	   Exp child=tmp.First();
        	   child.toString(sb, indent);
        	   sb.append("\n");
           }
           
           repeat(sb,indent);
           sb.append(right());
       }
    	public abstract String left();
    	public abstract String right();
		@Override
		public String to_value() {
			// TODO Auto-generated method stub
			return left()+right();
		}
		@Override
		public boolean isBracket() {
			// TODO Auto-generated method stub
			return true;
		}
    }
    
    public static class FunctionExp extends BracketsExp{
		public FunctionExp(Token first, Node<Exp> children, Token last) {
			super(first, children, last);
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
    	
    }
    public static class CallExp extends BracketsExp{
		public CallExp(Token first, Node<Exp> children, Token last) {
			super(first, children, last);
			this.r_children=Library.reverse(children);
		}
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
    	
    }
    public static class ListExp extends BracketsExp{
		public ListExp(Token first, Node<Exp> children, Token last) {
			super(first, children, last);
			this.r_children=Library.reverse(children);
		}
		private Node<Exp> r_children;
		public Node<Exp> R_children(){
			return r_children;
		}
		@Override
		public String left() {
			// TODO Auto-generated method stub
			return "[";
		}
		@Override
		public String right() {
			// TODO Auto-generated method stub
			return "]";
		}
		@Override
		public Exp_Type xtype() {
			// TODO Auto-generated method stub
			return Exp_Type.List;
		}
    	
    }
    
    public static abstract class AtomExp extends Exp{
        protected Token token;
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
		@Override
		public boolean isBracket() {
			// TODO Auto-generated method stub
			return false;
		}
    }
    //数字
    public static class IntExp extends AtomExp{
    	public IntExp(Token token) {
    		this.token=token;
    		this.value=Integer.parseInt(token.Value());
    	}
        private Integer value;
        public Integer Value() {
        	return value;
        }
		@Override
		protected void toString(StringBuilder sb) {
			// TODO Auto-generated method stub
			toString(sb,value,"","");
		}
		@Override
		public String to_value() {
			// TODO Auto-generated method stub
			return Integer.toString(value);
		}
		@Override
		public Exp_Type xtype() {
			// TODO Auto-generated method stub
			return Exp_Type.Int;
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
    	public IdExp(Token token) {
    		this.token=token;
    		this.value=token.Value();
    	}
        private String value;
        public String Value() {
        	return value;
        }
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
		@Override
		public String to_value() {
			// TODO Auto-generated method stub
			return value;
		}
		@Override
		public Exp_Type xtype() {
			// TODO Auto-generated method stub
			return Exp_Type.ID;
		}
    }
    
    //字符串
    public static class StrExp extends AtomExp{
    	public StrExp(Token token) {
    		this.token=token;
    		this.value=token.Value();
    	}
        private String value;
        public String Value() {
        	return value;
        }
        
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
            	toString(sb,Exp.replaceQuote(value),"","");
    		}
		}
		@Override
		public String to_value() {
			// TODO Auto-generated method stub
			return value;
		}
		@Override
		public Exp_Type xtype() {
			// TODO Auto-generated method stub
			return Exp_Type.String;
		}
    }

    public static class LetExp extends BracketsExp{
		public LetExp(Token first, Node<Exp> children, Token last) {
			super(first, children, last);
		}
		@Override
		public String left() {
			// TODO Auto-generated method stub
			return "(let";
		}

		@Override
		public String right() {
			// TODO Auto-generated method stub
			return ")";
		}

		@Override
		public Exp_Type xtype() {
			// TODO Auto-generated method stub
			return Exp_Type.Let;
		}
    }
    public static class LetBraExp extends BracketsExp{
		public LetBraExp(Token first, Node<Exp> children, Token last) {
			super(first, children, last);
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
			return Exp_Type.Let_Bra;
		}
    }
    public static class LetIdExp extends AtomExp{
    	public LetIdExp(Token token) {
    		this.token=token;
    		this.value=token.Value();
    	}
        private String value;
        public String Value() {
        	return value;
        }
		@Override
		protected void toString(StringBuilder sb) {
			// TODO Auto-generated method stub
			toString(sb,value,"","");
		}

		@Override
		public String to_value() {
			// TODO Auto-generated method stub
			return value;
		}
		@Override
		public Exp_Type xtype() {
			// TODO Auto-generated method stub
			return Exp_Type.Let_ID;
		}
    }
    public static class LetRestIdExp extends AtomExp{
    	public LetRestIdExp(Token token) {
    		this.token=token;
    		this.value=token.Value();
    	}
        private String value;
        public String Value() {
        	return value;
        }
		@Override
		protected void toString(StringBuilder sb) {
			// TODO Auto-generated method stub
			toString(sb,value,"...","");
		}
		@Override
		public String to_value() {
			// TODO Auto-generated method stub
			return value;
		}
		@Override
		public Exp_Type xtype() {
			// TODO Auto-generated method stub
			return Exp_Type.Let_Rest_ID;
		}
    }
}
