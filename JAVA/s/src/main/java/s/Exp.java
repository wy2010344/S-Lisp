package s;

import mb.RangePathsException;
import s.exp.*;

import java.util.List;

public abstract class Exp {
    public abstract void buildString(StringBuilder sb);

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        buildString(sb);
        return sb.toString();
    }
    public abstract RangePathsException exception(String s);
    public abstract void warn(String msg);

    private static Node<Exp> parseList(Node<Exp> r_children,List<Token> blocks,Token left) throws RangePathsException {
        ListExp listExp=parseListBody(left,blocks);
        return Node.extend(listExp,r_children);
    }
    private static ListExp parseListBody(Token left, List<Token> blocks) throws RangePathsException {
        ListExp exp=null;
        Node<Exp> r_children=null;
        while (blocks.size()!=0 && exp==null) {
            Token block = blocks.remove(0);
            switch (block.getType()) {
                case MRBracketBlock:
                    //遇到]，结束列表
                    exp=new ListExp(left,Node.reverse(r_children),block,r_children);
                    break;
                    /***************************************重复，结束符************************************************/
                case LLBracketBlock:
                    r_children=parseFunction(r_children,blocks,block);
                    break;
                case MLBracketBlock:
                    r_children=parseList(r_children,blocks,block);
                    break;
                case SLBracketBlock:
                    r_children=parseCall(r_children,blocks,block);
                    break;
                case StringBlock:
                    r_children=parseString(r_children, block);
                    break;
                case TransBlock:
                    r_children=parseTrans(r_children, block, false);
                    break;
                case IdBlock:
                    r_children=parseId(r_children, block, true);
                    break;
                case IntBlock:
                    r_children=parseInt(r_children, block);
                    break;
                case TrueBlock:
                    r_children=parseTrue(r_children, block);
                    break;
                case FalseBlock:
                    r_children=parseFalse(r_children, block);
                    break;
                default:
                    throw block.exception("List调用解析不合法");
            }
        }
        return exp;
    }
    protected static Node<Exp> parseCall(Node<Exp> r_children, List<Token> blocks, Token block) throws RangePathsException {
        CallExp callExp=parseCallBody(block,blocks);
        return Node.extend(callExp,r_children);
    }
    private static CallExp parseCallBody(Token left, List<Token> blocks) throws RangePathsException {
        CallExp exp=null;
        Node<Exp> r_children=null;
        while (blocks.size()!=0 && exp==null) {
            Token block = blocks.remove(0);
            switch (block.getType()) {
                case SRBracketBlock:
                    //遇到)，结束函数调用
                    Node<Exp> children=Node.reverse(r_children);
                    if (children==null){
                        throw left.exception("不允许空的call表达式");
                    } else {
                        Exp c_exp = children.First();
                        if (!(c_exp instanceof FunctionExp || c_exp instanceof CallExp || c_exp instanceof IdExp)) {
                            throw c_exp.exception( "函数调用的函数位不是{}|()|id");
                        }
                    }
                    exp=new CallExp(left,children,block,r_children);
                    break;
                /***************************************重复，结束符************************************************/
                case LLBracketBlock:
                    r_children=parseFunction(r_children,blocks,block);
                    break;
                case MLBracketBlock:
                    r_children=parseList(r_children,blocks,block);
                    break;
                case SLBracketBlock:
                    r_children=parseCall(r_children,blocks,block);
                    break;
                case StringBlock:
                    r_children=parseString(r_children, block);
                    break;
                case TransBlock:
                    r_children=parseTrans(r_children, block);
                    break;
                case IdBlock:
                    r_children=parseId(r_children, block);
                    break;
                case IntBlock:
                    r_children=parseInt(r_children, block);
                    break;
                case TrueBlock:
                    r_children=parseTrue(r_children, block);
                    break;
                case FalseBlock:
                    r_children=parseFalse(r_children, block);
                    break;
                default:
                    throw block.exception("函数调用解析不合法");
            }
        }
        return exp;
    }
    private static LetExp parseLetBody(Token left,List<Token> blocks) throws RangePathsException {
        LetExp exp=null;
        Node<Exp> r_children=null;
        while (blocks.size()!=0 && exp==null){
            Token block = blocks.remove(0);
            switch (block.getType()){
                case SRBracketBlock:
                    //遇到)，结束Let表达式
                    exp=new LetExp(left,Node.reverse(r_children),block,r_children);
                    break;
                /***************************************重复，结束符************************************************/
                case SLBracketBlock:
                    //遇到(，进入Let的匹配表达式
                    r_children=parseLetBracket(r_children,blocks,block);
                    break;
                case IdBlock:
                    String v=block.getContent();
                    if (v.indexOf('.')>-1){
                        throw block.exception("key位id不合法");
                    }else {
                        r_children=Node.extend(new LetIDExp(block,v),r_children);
                    }
                    break;
                default:
                    throw block.exception("let-key位不合法");
            }
            /*value部分*/
            if (exp==null) {
                if (blocks.size() > 0) {
                    block = blocks.remove(0);
                    switch (block.getType()) {
                        case LLBracketBlock:
                            r_children=parseFunction(r_children, blocks, block);
                            break;
                        case MLBracketBlock:
                            r_children=parseList(r_children, blocks, block);
                            break;
                        case SLBracketBlock:
                            r_children=parseCall(r_children, blocks, block);
                            break;
                        case StringBlock:
                            r_children=parseString(r_children, block);
                            break;
                        case TransBlock:
                            r_children=parseTrans(r_children, block);
                            break;
                        case IdBlock:
                            r_children=parseId(r_children, block);
                            break;
                        case IntBlock:
                            r_children=parseInt(r_children, block);
                            break;
                        case TrueBlock:
                            r_children=parseTrue(r_children, block);
                            break;
                        case FalseBlock:
                            r_children=parseFalse(r_children, block);
                            break;
                        default:
                            throw block.exception("let-value位不合法");
                    }
                } else {
                    throw block.exception("过早结束，无匹配的value");
                }
            }
        }
        return exp;
    }
    private static Node<Exp> parseFunction(Node<Exp> r_children,List<Token> blocks,Token left) throws RangePathsException {
        FunctionExp exp=parseFunctionBody(left,blocks,false);
        return Node.extend(exp,r_children);
    }
    private static FunctionExp parseFunctionBody(Token left,List<Token> blocks,boolean allowNoEnd) throws RangePathsException {
        FunctionExp exp=null;
        Node<Exp> r_children=null;
        while (blocks.size()!=0 && exp==null){
            Token block=blocks.remove(0);
            switch (block.getType()){
                case LRBracketBlock:
                    //遇到}，退出函数
                    check_function(r_children);
                    exp=new FunctionExp(left,Node.reverse(r_children),block,r_children);
                    break;
                /***************************************重复，结束符************************************************/
                case LLBracketBlock:
                    r_children=parseFunction(r_children,blocks,block);
                    break;
                case MLBracketBlock:
                    r_children=parseList(r_children,blocks,block);
                    break;
                case SLBracketBlock:
                    Token first=blocks.get(0);
                    if (first.getType()== TokenType.IdBlock && "let".equals(first.getContent())){
                        //let表达式
                        blocks.remove(0);//移除let本身
                        LetExp letExp=parseLetBody(block,blocks);
                        r_children=Node.extend(letExp,r_children);
                    }else{
                        r_children=parseCall(r_children,blocks,block);
                    }
                    break;
                case StringBlock:
                    r_children=parseString(r_children,block);
                    break;
                case TransBlock:
                    r_children=parseTrans(r_children,block);
                    break;
                case IdBlock:
                    r_children=parseId(r_children,block);
                    break;
                case IntBlock:
                    r_children=parseInt(r_children,block);
                    break;
                case TrueBlock:
                    r_children=parseTrue(r_children,block);
                    break;
                case FalseBlock:
                    r_children=parseFalse(r_children,block);
                    break;
                default:
                    throw block.exception("函数解析不合法");
            }
        }
        if (allowNoEnd){
            /*文件内，允许结束*/
            if (exp==null){
                check_function(r_children);
                return new FunctionExp(left,Node.reverse(r_children),new Token(TokenType.LRBracketBlock,0,"}"),r_children);
            }else{
                throw exp.exception("过多的结束符号");
            }
        }else{
            /*普通函数*/
            if (exp==null){
                throw left.exception("提前结束，不允许");
            }else{
                return exp;
            }
        }
    }
    private static  Node<Exp> parseLetBracket(Node<Exp> r_children,List<Token> blocks,Token left) throws RangePathsException {
        LetBracketExp exp=parseLetBracketBody(left,blocks);
        return Node.extend(exp,r_children);
    }
    public static LetBracketExp parseLetBracketBody(Token left,List<Token> blocks) throws RangePathsException {
        LetBracketExp exp=null;
        Node<Exp> r_children=null;
        while (blocks.size()!=0 && exp==null) {
            Token block = blocks.remove(0);
            switch (block.getType()) {
                case SRBracketBlock:
                    if (r_children!=null) {
                        Node<Exp> tmp = r_children.Rest();
                        while (tmp != null) {
                            if(tmp.First() instanceof LetRestIDExp){
                                throw tmp.First().exception("rest匹配必须在最后一位");
                            }
                            tmp=tmp.Rest();
                        }
                    }
                    exp=new LetBracketExp(left,Node.reverse(r_children),block,r_children);
                    break;
                /***************************************重复，结束符************************************************/
                case SLBracketBlock:
                    //遇到(，递归进入
                    r_children=parseLetBracket(r_children,blocks,block);
                    break;
                case IdBlock:
                    String content = block.getContent();
                    if (content.indexOf('.') < 0) {
                        r_children=Node.extend(new LetIDExp(block, content),r_children);
                    } else if (content.length() > 3 && content.startsWith("...") && content.indexOf('.',3)<0) {
                        r_children=Node.extend(new LetRestIDExp(block, content.substring(3)),r_children);
                    } else {
                        throw block.exception("不是合法的id类型:");
                    }
                    break;
                default:
                    throw block.exception("let-key-bracket解析不合法");
            }
        }
        return exp;
    }
    public static FunctionExp run(List<Token> blocks) throws RangePathsException {
        return parseFunctionBody(new Token(TokenType.LLBracketBlock,0,"{"),blocks,true);
    }
    private static  void check_function(Node<Exp> r_children) {
        if (r_children!=null){
            Node<Exp> tmp=r_children.Rest();
            while (tmp!=null){
                Exp exp=tmp.First();
                if (!(exp instanceof LetExp || exp instanceof CallExp)){
                    if (exp instanceof ListExp){
                        exp.warn("函数中的List表达式，节点会被计算，确定？"+exp.toString());
                    }else {
                        exp.warn("函数中有无意义的表达式，将不会执行" + exp.toString());
                    }
                }
                tmp=tmp.Rest();
            }
        }
    }
    private static Node<Exp> parseString(Node<Exp> r_children, Token block) {
        String value=block.getContent();
        value=value.substring(1,value.length()-1);
        return Node.extend(new StringExp(block,value),r_children);
    }
    private static Node<Exp> parseInt(Node<Exp> r_children, Token block) {
        return Node.extend(new IntExp(block,Integer.parseInt(block.getContent())),r_children);
    }
    private static Node<Exp> parseTrue(Node<Exp> r_children, Token block) {
        return Node.extend(new BooleanExp(block,true),r_children);
    }
    private static Node<Exp> parseFalse(Node<Exp> r_children, Token block) {
        return Node.extend(new BooleanExp(block,false),r_children);
    }
    private static Node<Object> getIds(Token block, String id_path) throws RangePathsException {
        if (id_path.charAt(0)=='.'||id_path.charAt(id_path.length()-1)=='.'||id_path.indexOf("..")!=-1){
            throw block.exception("不是合法的id类型");
        }else {
            String[] ids=id_path.split("\\.");
            return Node.list(ids);
        }
    }
    private static Node<Exp> parseId(Node<Exp> r_children, Token block, boolean asStr) throws RangePathsException {
        String id_path=block.getContent();
        if (asStr) {
            return Node.extend(new StringExp(block,id_path),r_children);
        }else {
            Node<Object> ids = getIds(block, id_path);
            return Node.extend(new IdExp(block, ids),r_children);
        }
    }
    private static Node<Exp> parseId(Node<Exp> r_children, Token block) throws RangePathsException {
        return parseId(r_children,block,false);
    }
    private static Node<Exp> parseTrans(Node<Exp> r_children, Token block, boolean asStr) throws RangePathsException {
        String value=block.getContent().substring(1);
        if (asStr){
            return Node.extend(new StringExp(block,value),r_children);
        }else{
            Node<Object> ids=getIds(block,value);
            return Node.extend(new IdExp(block,ids),r_children);
        }
    }
    private static Node<Exp> parseTrans(Node<Exp> r_children, Token block) throws RangePathsException {
        return parseTrans(r_children,block,false);
    }
}
