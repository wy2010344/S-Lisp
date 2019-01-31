package st3;

import mb.RangePathsException;
import s.Node;

import java.util.List;

public abstract class Exp {
    public abstract RangePathsException exception(String msg);

    protected abstract void toString(StringBuilder sb);

    static class Cache{
        public final Token right;
        public Node<Exp> children;
        public Cache(Token right){
            this.right=right;
        }
    }

    /*
     逆向解析
     */
    public static Node<Exp> parse(Node<Token> tokens) throws RangePathsException {
        int flag=0;
        Token rootLeft=new Token(Token.TokenType.SBracketLeftToken,0,"(");
        Node<Cache> caches=Node.extend(new Cache(rootLeft),null);
        while (tokens!=null){
            Token token=tokens.First();
            tokens=tokens.Rest();
            switch (token.type){
                case IDToken://id包括字符串，不区分字符串
                    caches.First().children=Node.extend(
                            new IDExp(token),
                            caches.First().children
                    );
                    break;
                case SBracketLeftToken://"("
                    Cache cache = caches.First();
                    caches=caches.Rest();
                    BracketExp bracketExp=new BracketExp(
                            token,
                            cache.children,
                            cache.right
                    );
                    if (caches==null){
                        throw bracketExp.exception("过早结束文段");
                    }
                    caches.First().children=Node.extend(bracketExp,caches.First().children);
                    break;
                case SBracketRightToken://")"
                    caches=Node.extend(new Cache(token),caches);
                    break;
            }
        }
        if (caches==null){
            throw rootLeft.exception("过多地结束");
        }
        if (caches.Length()>1){
            throw caches.First().right.exception("未对应匹配");
        }
        return caches.First().children;
    }
}
