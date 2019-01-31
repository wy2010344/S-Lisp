package st3;

import mb.RangeException;
import mb.RangePathsException;
import s.Node;

import java.util.ArrayList;
import java.util.List;

public class Token {
    public final TokenType type;
    public final int begin;
    public final String value;

    public RangePathsException exception(String msg) {
        return new RangePathsException(begin,begin+value.length(),msg);
    }

    enum TokenType{
        SBracketLeftToken,
        SBracketRightToken,
        IDToken
    }
    public Token(TokenType type,int begin,String value){
        this.type=type;
        this.begin=begin;
        this.value=value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Node<Token> run(final String in) throws RangePathsException {
        Node<Token> tokens=null;
        int flag=0;
        while (flag<in.length()){
            char c=in.charAt(flag);
            if (Character.isWhitespace(c)){
                flag++;
            }else{
                if (c=='"'){
                    try{
                        int index=flag;
                        flag++;
                        String string=in.substring(index,mb.Util.parseUntil(in,flag,'"')+1);
                        flag=flag+string.length()-1;
                        tokens=Node.extend(new Token(TokenType.IDToken,index,string),tokens);
                    }catch (RangeException e){
                        throw new RangePathsException(e);
                    }
                }else if(c=='`'){
                    try {
                        int index = flag;
                        flag++;
                        String comment = in.substring(index, mb.Util.parseUntil(in, flag, '`') + 1);
                        flag = flag + comment.length() - 1;
                    } catch (RangeException e) {
                        throw new RangePathsException(e);
                    }
                }else if(c=='('){
                    tokens=Node.extend(new Token(TokenType.SBracketLeftToken,flag,"("),tokens);
                    flag++;
                }else if(c==')'){
                    tokens=Node.extend(new Token(TokenType.SBracketRightToken,flag,")"),tokens);
                    flag++;
                }else{
                    int begin=flag;
                    int end=-1;
                    while (flag<in.length() && end<0){
                        char cv=in.charAt(flag);
                        if (Character.isWhitespace(cv) || cv=='"' || cv=='`' || cv=='(' || cv==')'){
                            end=flag;
                        }else{
                            flag++;
                        }
                    }
                    if (end<0) {
                        end = flag;
                    }
                    String id=in.substring(begin,end);
                    tokens=Node.extend(new Token(TokenType.IDToken,begin,id),tokens);
                }
            }
        }
        return tokens;
    }
}
