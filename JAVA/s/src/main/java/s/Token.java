package s;

import mb.RangeException;
import mb.RangePathsException;

import java.util.ArrayList;
import java.util.List;

public class Token {
    public Token(TokenType blockType, int begin, String content){
        this.blockType=blockType;
        this.begin=begin;
        this.content=content;
    }
    private TokenType blockType;
    private int begin;
    private String content;
    public TokenType getBlockType(){
        return blockType;
    }
    public int getBegin(){
        return begin;
    }
    public String getContent(){
        return content;
    }
    
    public enum TokenType {
        StringBlock,
        //QuoteBlock,
        TransBlock,
        IdBlock,
        IntBlock,
        TrueBlock,
        FalseBlock,
        LLBracketBlock,
        MLBracketBlock,
        SLBracketBlock,
        LRBracketBlock,
        MRBracketBlock,
        SRBracketBlock
    }

    public RangePathsException exception(String msg){
        return new RangePathsException(getBegin(),getBegin()+content.length(),getBlockType()+":"+getContent()+"=>"+msg);
    }

    @Override
    public String toString() {
        return content;
    }

    /*****************************************************************************************************************************************/

    public static boolean isFloat(String s) {
        boolean ret = true;
        int index = 0;
        if (s.charAt(0) == '-') {
            index = 1;
        }
        boolean noPoint = true;
        while (index < s.length()) {
            char c = s.charAt(index);
            if (c == '.') {
                if (noPoint) {
                    noPoint = false;
                } else {
                    ret = false;
                }
            } else if (!Character.isDigit(c)) {
                ret = false;
            }
            index++;
        }
        return ret;
    }
    public static boolean isEnd(char cv){
       return Character.isWhitespace(cv) ||
               cv=='"' ||
               cv=='`' ||
               mb.Util.indexOf(brackets_in,cv)>-1 ||
               mb.Util.indexOf(brackets_out,cv)>-1;
    }
    public static final char[] brackets_in={'{','[','('};
    public static final char[] brackets_out={')',']','}'};
    public static List<Token> run(final String in) throws RangePathsException {
        final List<Token> blocks=new ArrayList<Token>();
        int flag=0;
        while (flag<in.length()){
            char c=in.charAt(flag);
            if (Character.isWhitespace(c)){
                flag++;
            }else if (c=='"'){
                /*字符串块*/
                try {
                    int index = flag;
                    flag++;//跳出第一个"
                    String string = in.substring(index, mb.Util.parseUntil(in, flag, '"') + 1);//包含双引号
                    flag = flag + string.length() - 1;
                    blocks.add(new Token(TokenType.StringBlock, index, string));
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
                    //blocks.add(new Block(BlockType.QuoteBlock,index,string));
                }catch (RangeException r){
                    throw new RangePathsException(r);
                }
            }else if (c=='{'){
                blocks.add(new Token(TokenType.LLBracketBlock,flag,"{"));
                flag++;
            }else if(c=='['){
                blocks.add(new Token(TokenType.MLBracketBlock,flag,"["));
                flag++;
            }else if(c=='('){
                blocks.add(new Token(TokenType.SLBracketBlock,flag,"("));
                flag++;
            }else if(c=='}'){
                blocks.add(new Token(TokenType.LRBracketBlock,flag,"}"));
                flag++;
            }else if(c==']'){
                blocks.add(new Token(TokenType.MRBracketBlock,flag,"]"));
                flag++;
            }else if(c==')'){
                blocks.add(new Token(TokenType.SRBracketBlock,flag,")"));
                flag++;
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
                if (end<0){
                    end=flag;
                }
                String string=in.substring(begin,end);
                TokenType type= TokenType.IdBlock;
                if (string.length()>1 && string.charAt(0)=='\''){
                    type= TokenType.TransBlock;
                }else{
                    if ("true".equals(string)){
                        type= TokenType.TrueBlock;
                    }else if("false".equals(string)){
                        type= TokenType.FalseBlock;
                    }else if(mb.Util.isInt(string)){
                        type= TokenType.IntBlock;
                    }
                }
                blocks.add(new Token(type,begin,string));
            }
        }
        return blocks;
    }
}
