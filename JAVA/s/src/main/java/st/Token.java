package st;

import mb.RangeException;
import mb.RangePathsException;

import java.util.ArrayList;
import java.util.List;

public class Token {
    public final int index;
    public final TokenType type;
    public final String value;
    public Token(TokenType type, int index, String value){
        this.type=type;
        this.index=index;
        this.value=value;
    }
    public static enum TokenType{
        StringToken,
        IdToken,
        IntToken,
        TrueToken,
        FalseToken,

        LLBracketToken,
        MLBracketToken,
        SLBracketToken,
        ALBracketToken,//<

        LRBracketToken,
        MRBracketToken,
        SRBracketToken,
        ARBracketToken,//>

        PointToken,//.
        EqualToken,//=
        逗号,
        冒号,
        分号
    }
    public RangePathsException error(String msg){
        return new RangePathsException(index,index+value.length(),type+":"+value+"=>"+msg);
    }
    static boolean isEnd(char c) {
        return Character.isWhitespace(c) ||
                c=='"' ||
                c=='`' ||
                mb.Util.indexOf(brackets_in,c)>-1 ||
                mb.Util.indexOf(brackets_out,c)>-1 ||
                c=='.' ||
                c==':' ||
                c==',' ||
                c==';';
    }
    public static final char[] brackets_in={'{','[','(','<'};
    public static final char[] brackets_out={'>',')',']','}'};
    public static List<Token> run(final String in) throws RangePathsException {
        final List<Token> tokens=new ArrayList<Token>();
        int flag=0;
        while (flag<in.length()){
            char c=in.charAt(flag);
            if (Character.isWhitespace(c)){
                flag++;
            }else{
                if (c=='"'){
                    /*字符串块*/
                    try {
                        int index=flag;
                        flag++;
                        String string = in.substring(index, mb.Util.parseUntil(in, flag, '"') + 1);//包含双引号
                        flag = flag + string.length() - 1;
                        tokens.add(new Token(TokenType.StringToken, index, string));
                    }catch (RangeException r){
                        throw new RangePathsException(r);
                    }
                }else if(c=='`'){
                    /*注释块*/
                    try {
                        int index = flag;
                        flag++;//跳出第一个"
                        String string = in.substring(index, mb.Util.parseUntil(in, flag, '`') + 1);//包含双``
                        flag = flag + string.length() - 1;
                        //tokens.add(new Token(TokenType.QuoteToken,index,string));
                    }catch (RangeException r){
                        throw new RangePathsException(r);
                    }
                }else if (c=='{'){
                    tokens.add(new Token(TokenType.LLBracketToken,flag,"{"));
                    flag++;
                }else if(c=='['){
                    tokens.add(new Token(TokenType.MLBracketToken,flag,"["));
                    flag++;
                }else if(c=='('){
                    tokens.add(new Token(TokenType.SLBracketToken,flag,"("));
                    flag++;
                }else if(c=='}'){
                    tokens.add(new Token(TokenType.LRBracketToken,flag,"}"));
                    flag++;
                }else if(c==']'){
                    tokens.add(new Token(TokenType.MRBracketToken,flag,"]"));
                    flag++;
                }else if(c==')'){
                    tokens.add(new Token(TokenType.SRBracketToken,flag,")"));
                    flag++;
                }else if(c=='<') {
                    tokens.add(new Token(TokenType.ALBracketToken,flag,"<"));
                    flag++;
                }else if(c=='>') {
                    tokens.add(new Token(TokenType.ARBracketToken,flag,">"));
                    flag++;
                }else if(c=='.') {
                    tokens.add(new Token(TokenType.PointToken, flag, "."));
                    flag++;
                }else if(c==','){
                    tokens.add(new Token(TokenType.逗号,flag,","));
                    flag++;
                }else if(c==':') {
                    tokens.add(new Token(TokenType.冒号,flag,":"));
                    flag++;
                }else if(c==';') {
                    tokens.add(new Token(TokenType.分号,flag,";"));
                }else{
                        /*其它块*/
                        int begin=flag;
                        int end=-1;
                        while (flag<in.length() && end<0){
                            char cv=in.charAt(flag);
                            if (isEnd(cv)){
                                end=flag;
                            }else{
                                flag++;
                            }
                        }
                        if (end<0) {
                            end = flag;
                        }
                        String string=in.substring(begin,end);
                        TokenType type= TokenType.IdToken;
                        if(mb.Util.isInt(string)){
                            type= TokenType.IntToken;
                        }else {
                            if ("true".equals(string)) {
                                type = TokenType.TrueToken;
                            } else if ("false".equals(string)) {
                                type = TokenType.FalseToken;
                            }
                        }
                        tokens.add(new Token(type,begin,string));
                    }
                }
            }
            return tokens;
    }
}
