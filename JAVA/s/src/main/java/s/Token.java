package s;

public class Token {
    public Token(
            String _value,
            Location _loc,
            Type _type){
        value=_value;
        loc=_loc;
        type=_type;
        original_type=_type;
    }
    //未转义前原始类型
    private Type original_type;
    public Type Original_type() {
    	return original_type;
    }
    private Type type;
    public Type Type(){
        return type;
    }
    //外部修改
    public void Type(Type _type){
        type=_type;
    }
    private String value;
    public String Value(){
        return value;
    }
    private Location loc;
    public Location Loc(){
        return loc;
    }
    
    @Override
    public String toString() {
    	return value;
    }
    
    public static enum Type{
        Id,//
        Quote,//'
        BraL,//([{
        BraR,//)]}
        Comment,//``
        Str,//"  "
        Int,
        Float,
    }

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
	    return !(Character.isWhitespace(c)|| has(c,brackets_in) || has(c,brackets_out) || c=='"' || c=='`');
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
	            tokens=Node.extend(new Token(s,loc,Token.Type.Str),tokens);
	        }else
	        if(code.current()=='`'){
	            //注释
	            Location loc=code.currentLoc();
	            String s=parseStr(code,'`');
	            loc.setLength(s.length()+2);
	            //tokens=Node.extend(new Token(s,loc,Token.Type.Comment),tokens);
	            //不处理
	        }else
	        if(has(code.current(),brackets_in)){
	            //([{
	            Location loc=code.currentLoc();
	            loc.setLength(1);
	            tokens=Node.extend(new Token(code.current()+"",loc,Token.Type.BraL),tokens);
	            code.shift();
	        }else
	        if(has(code.current(),brackets_out)){
	            //)]}
	            Location loc=code.currentLoc();
	            loc.setLength(1);
	            tokens=Node.extend(new Token(code.current()+"",loc,Token.Type.BraR),tokens);
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
	                    tokens=Node.extend(new Token(s,loc,Token.Type.Quote),tokens);
	                }
	            }else
	            if((!s.equals("-"))&&isInt(s)){
	                tokens=Node.extend(new Token(s,loc,Token.Type.Int),tokens);
	            }else
	            /*
	            if(isFloat(s)){
	                tokens.add(new Token(s,loc,Token.Type.Float));
	            }
	            else
	            */
	            {
	            	tokens=Node.extend(new Token(s,loc,Token.Type.Id),tokens);
	            }
	        }
	    }
	    /*
	     *因为没有翻转，与文章完全相反 
	     */
	    return Node.reverse(tokens);
	}
}
