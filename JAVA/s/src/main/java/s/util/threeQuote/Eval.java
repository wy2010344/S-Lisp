package s.util.threeQuote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import s.LocationException;
import s.util.Code;
import s.util.Location;

public class Eval {

	static HashMap<Character,Character> trans_map=new HashMap<Character,Character>();
	static {
		trans_map.put('n', '\n');//换行
		trans_map.put('r', '\r');//回车
		trans_map.put('t', '\t');//制表
	}
    /**
     * 解析字符串、注释
     * @param code
     * @param end
     * @return
     * @throws LocationException 
     */
    static String parseStr(Code code,char end) throws LocationException{
        code.shift();
        boolean nobreak=true;
        StringBuilder sb=new StringBuilder();
        while(code.current()!=null && nobreak){
            if(code.current()==end){
                nobreak=false;
            }else{
                if(code.current()=='\\'){
                	code.shift();
                	if(code.current()==end) {
                		//添加普通转义符
                		sb.append(end);
                	}else 
                	if(code.current()=='\\'){
                		sb.append("\\");
                	}else {
                		Character c=trans_map.get(code.current());
                		if(c!=null) {
                			sb.append(c);
                		}else {
                    		System.out.println(sb.toString());
                			throw code.msgThrow(code.current());
                		}
                	}
                }else{
                	sb.append(code.current());
                }
                code.shift();
            }
        }
        if(code.current()==null){
            throw code.msgThrow(end);
        }else{
            code.shift();
            return sb.toString();
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
    public static List<Token> tokenize(String codes,char lineSplit) throws LocationException{
        Code code=new Code(codes,lineSplit);
        ArrayList<Token> tokens=new ArrayList<Token>();
        while(code.current()!=null){
            if(Character.isWhitespace(code.current())){
                while(code.current()!=null && Character.isWhitespace(code.current())){
                    code.shift();
                }
            }else
            if(code.current()=='"'){
                //字符串
                Location loc=code.currentLoc();
                String s=parseStr(code,'"');
                loc.setLength(s.length()+2);
                tokens.add(new Token(s,loc,Token.Type.Str));
            }else
            if(code.current()=='`'){
                //注释
                Location loc=code.currentLoc();
                String s=parseStr(code,'`');
                loc.setLength(s.length()+2);
                tokens.add(new Token(s,loc,Token.Type.Comment));
                //不处理
            }else
            if(has(code.current(),brackets_in)){
                //([{
                Location loc=code.currentLoc();
                loc.setLength(1);
                tokens.add(new Token(code.current()+"",loc,Token.Type.BraL));
                code.shift();
            }else
            if(has(code.current(),brackets_out)){
                //)]}
                Location loc=code.currentLoc();
                loc.setLength(1);
                tokens.add(new Token(code.current()+"",loc,Token.Type.BraR));
                code.shift();
            }else
            {
                //id
                Location loc=code.currentLoc();
                StringBuilder sb=new StringBuilder();
                while(code.current()!=null && isNotEnd(code.current())){
                    sb.append(code.current());
                    code.shift();
                }
                String s=sb.toString();
                loc.setLength(s.length());
            	//长度超过1
                if(s.charAt(0)=='\'' && s.length()!=1){
                    //转义
                	s=s.substring(1);
                    tokens.add(new Token(s,loc,Token.Type.Quote));
                }else
                if((!s.equals("-"))&&isInt(s)){
                    tokens.add(new Token(s,loc,Token.Type.Int));
                }else
                /*
                if(isFloat(s)){
                    tokens.add(new Token(s,loc,Token.Type.Float));
                }
                else
                */
                {
                	tokens.add(new Token(s,loc,Token.Type.Id));
                }
            }
        }
        return tokens;
    }
}
