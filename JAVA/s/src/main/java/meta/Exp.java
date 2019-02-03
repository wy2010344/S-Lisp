package meta;

import mb.RangePathsException;

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

    public boolean isBracketExp(){
        return this instanceof BracketExp;
    }
    public boolean isIDExp(){
        return this instanceof IDExp;
    }
    public boolean isStringExp(){
        return this instanceof StringExp;
    }
    public BracketExp asBracketExp(){
        return (BracketExp)this;
    }
    public IDExp asIDExp(){
        return (IDExp)this;
    }
    public StringExp asStringExp(){
        return (StringExp)this;
    }
    /*
     逆向解析
     */
    public static Node<Exp> parse(Node<Token> tokens) throws RangePathsException {
        int begin = 0;
        if (tokens != null) {
            Token first = tokens.first;
            begin = first.begin + first.value.length();
        }
        Token rootRight=new Token(Token.TokenType.SBracketRightToken,begin,")");
        Node<Cache> caches=Node.extend(new Cache(rootRight),null);
        while (tokens!=null){
            Token token=tokens.first;
            tokens=tokens.rest;
            switch (token.type){
                case IDToken://id包括字符串，不区分字符串
                    caches.first.children=Node.extend(
                            new IDExp(token),
                            caches.first.children
                    );
                    break;
                case StringToken:
                    caches.first.children=Node.extend(
                            new StringExp(token),
                            caches.first.children
                    );
                    break;
                case SBracketLeftToken://"("
                    Cache cache = caches.first;
                    caches=caches.rest;
                    BracketExp bracketExp=new BracketExp(
                            token,
                            cache.children,
                            cache.right
                    );
                    if (caches==null){
                        throw bracketExp.exception("括号缺失");
                    }
                    caches.first.children=Node.extend(
                            bracketExp,
                            caches.first.children
                    );
                    break;
                case SBracketRightToken://")"
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
