package s;

import mb.RangeException;
import mb.RangePathsException;

import java.util.ArrayList;
import java.util.List;

public class Token {
    public Token(TokenType type, int begin, String content){
        this.type = type;
        this.begin=begin;
        this.content=content;
    }
    private TokenType type;
    private int begin;
    private String content;
    public TokenType getType(){
        return type;
    }
    public int getBegin(){
        return begin;
    }
    public String getContent(){
        return content;
    }

    public RangePathsException exception(String msg){
        return new RangePathsException(getBegin(),getBegin()+content.length(), getType()+":"+getContent()+"=>"+msg);
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
        return run(in,TokenWalker.empty);
    }

    static RangePathsException balance_more_throw(int flag, String in, String type) throws RangePathsException {
        return new RangePathsException(flag,in.length()-1,"过多的"+type);
    }
    static RangePathsException balance_less_throw(String in,int balance,String type){
        return new RangePathsException(in.length()-1,in.length()-1,"缺少"+type+balance+"个");
    }
    public static List<Token> run(final String in,final TokenWalker waker) throws RangePathsException {
        final List<Token> blocks=new ArrayList<Token>();
        int flag=0;

        int l_balance=0;
        int m_balance=0;
        int s_balance=0;
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
                    waker.when_String(index,string);
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
                    waker.when_Comment(index,string);
                    //blocks.add(new Block(BlockType.QuoteBlock,index,string));
                }catch (RangeException r){
                    throw new RangePathsException(r);
                }
            }else if (c=='{'){
                waker.when_LL(flag);
                blocks.add(new Token(TokenType.LLBracketBlock,flag,"{"));
                l_balance++;
                flag++;
            }else if(c=='['){
                waker.when_ML(flag);
                blocks.add(new Token(TokenType.MLBracketBlock,flag,"["));
                m_balance++;
                flag++;
            }else if(c=='('){
                waker.when_SL(flag);
                blocks.add(new Token(TokenType.SLBracketBlock,flag,"("));
                s_balance++;
                flag++;
            }else if(c=='}'){
                waker.when_LR(flag);
                blocks.add(new Token(TokenType.LRBracketBlock,flag,"}"));
                l_balance--;
                if (l_balance<0){
                    throw balance_more_throw(flag,in,"}");
                }
                flag++;
            }else if(c==']'){
                waker.when_MR(flag);
                blocks.add(new Token(TokenType.MRBracketBlock,flag,"]"));
                m_balance--;
                if (m_balance<0){
                    throw balance_more_throw(flag,in,"]");
                }
                flag++;
            }else if(c==')'){
                waker.when_SR(flag);
                blocks.add(new Token(TokenType.SRBracketBlock,flag,")"));
                s_balance--;
                if (s_balance<0){
                    throw balance_more_throw(flag,in,")");
                }
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
                    waker.when_Trans(begin,string);
                    type= TokenType.TransBlock;
                }else{
                    if ("true".equals(string)){
                        waker.when_True(begin,string);
                        type= TokenType.TrueBlock;
                    }else if("false".equals(string)){
                        waker.when_False(begin,string);
                        type= TokenType.FalseBlock;
                    }else if(mb.Util.isInt(string)){
                        waker.when_Int(begin,string);
                        type= TokenType.IntBlock;
                    }else{
                        waker.when_Id(begin,string);
                    }
                }
                blocks.add(new Token(type,begin,string));
            }
        }
        if (l_balance==0){
            if (m_balance==0){
                if (s_balance==0){
                    return blocks;
                }else{
                    throw balance_less_throw(in,s_balance,")");
                }
            }else {
                throw balance_less_throw(in,m_balance,"]");
            }
        }else{
            throw balance_less_throw(in,l_balance,"}");
        }
    }
}
