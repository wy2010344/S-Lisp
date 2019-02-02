package meta2;

import mb.RangePathsException;
import meta.Node;
import meta.Token;

public class Exp {
    public final ExpType type;

    /*单原子*/
    private final Token token;
    public final String value;
    /*字符串*/
    public final String originalValue;
    public final String quoteValueOneLine;/*不换行的表示法*/
    public Exp(Token token) throws RangePathsException {
        this.token=token;
        if (token.type== Token.TokenType.StringToken){
            type=ExpType.StringExp;
            String v=token.value;
            v=v.substring(1,v.length()-1);
            v=v.replace("\\\"","\"");
            this.value=v;
            this.originalValue=token.value;
            this.quoteValueOneLine=token.value.replace("\n","\\n")
                    .replace("\r","\\r");
        }else if (token.type==Token.TokenType.IDToken){
            type=ExpType.IDExp;
            this.value=token.value;
            this.originalValue="";
            this.quoteValueOneLine="";
        }else{
            throw token.exception("只允许ID或String");
        }

        this.left=null;
        this.right=null;
        this.children=null;
    }
    /*列表*/
    private final Token left;
    public final Node<Exp> children;
    private final Token right;
    public Exp(Token left, Node<Exp> children, Token right){
        this.token=null;
        this.value="";

        type=ExpType.BracketExp;
        this.left=left;
        this.children=children;
        this.right=right;
        this.originalValue="";
        this.quoteValueOneLine="";
    }

    void toString(StringBuilder sb){
        if (type==ExpType.BracketExp){
            sb.append("( ");
            for(Node<Exp> tmp=children;tmp!=null;tmp=tmp.rest){
                tmp.first.toString(sb);
                sb.append(" ");
            }
            sb.append(")");
        }else{
            sb.append(token.value);
        }
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        toString(sb);
        return sb.toString();
    }
    public RangePathsException exception(String msg){
        if (type==ExpType.BracketExp){
            return new RangePathsException(
                    left.begin,
                    right.begin+right.value.length(),
                    toString()+":"+msg
            );
        }else {
            return new RangePathsException(
                    token.begin,
                    token.begin+token.value.length(),
                    toString()+":"+msg
            );
        }
    }
    /***************************************************************************************************************************/
    static class Cache{
        public final Token right;
        public Node<Exp> children;
        public Cache(Token right){
            this.right=right;
        }
    }
    public static Node<Exp> parse(Node<Token> tokens) throws RangePathsException {
        int flag=0;
        Token rootRight=new Token(Token.TokenType.SBracketRightToken,0,")");
        Node<Cache> caches=Node.extend(new Cache(rootRight),null);
        while (tokens!=null){
            Token token=tokens.first;
            tokens=tokens.rest;
            switch (token.type){
                case IDToken:
                case StringToken:
                    caches.first.children=Node.extend(
                            new Exp(token),
                            caches.first.children
                    );
                    break;
                case SBracketLeftToken:
                    Cache cache = caches.first;
                    caches=caches.rest;
                    Exp bracketExp=new Exp(
                            token,
                            cache.children,
                            cache.right
                    );
                    if (caches==null){
                        throw bracketExp.exception("过早结束文段");
                    }
                    caches.first.children=Node.extend(
                            bracketExp,
                            caches.first.children
                    );
                    break;
                case SBracketRightToken:
                    caches=Node.extend(new Cache(token),caches);
                    break;
            }
        }
        if (caches==null){
            throw rootRight.exception("过多地结束");
        }
        if (caches.length>1){
            throw caches.first.right.exception("未对应匹配");
        }
        return caches.first.children;
    }
}
