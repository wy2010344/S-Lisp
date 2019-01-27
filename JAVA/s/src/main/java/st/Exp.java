package st;

import mb.RangePathsException;
import st.exp.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 不应该考虑复杂的继承关系，这只用动态转换。
 * 按解析的过程动态按需发展，需要一个什么类型。
 */
public class Exp {

    public static Exp run(List<Token> tokens){
        while (tokens.size()!=0){
            Token token=tokens.remove(0);
            if (token.type== Token.TokenType.IdToken){
                if ("lib".equals(token.value))
                {
                    //解析导入
                }else if("out".equals(token.value)){
                    /*
                      解析导出
                      导出可能是函数，可能是协议。
                     */
                }else if("type".equals(token.value)){
                    /**
                     协议，泛型Type<T>
                     函数（形参表），泛型Fn<T1,T2>(String,Int)Boolean
                     单例类型——是否需要类似静态的标识？
                        Type{
                            a:List<String>,
                            b:fn<T1>(Int,Bool)String
                        }
                        <>()Boolean
                     delay的函数，返回单例类型
                     delay的函数，返回现有类型
                     */
                    tokens.remove(0);
                    token=tokens.remove(0);

                }
            }else{
                //错误的
            }
        }
        return null;
    }

    public static RangePathsException token_unexpected(Token token){
        return token.error("不是期待的类型");
    }
    private static RangePathsException token_end(){
        return new RangePathsException(0,0,"提前结束问题");
    }
    /**
     * 类似于将JSON转成JAVA-POJO类
     * @param tokens
     * @return
     */
    public static Exp parseMap(List<Token> tokens) throws RangePathsException {
        Token left=tokens.remove(0);//第一个{
        Token right=null;
        Map<Token,Exp> children=new TreeMap<Token,Exp>();
        boolean will=true;
        while (tokens.size()>0 && will){
            Token key=tokens.remove(0);
            switch (key.type){
                case IdToken:
                case StringToken:
                    //key部分
                    break;
                default:
                    throw key.error("不正确的token");
            }
            if (tokens.size() > 2) {
                Token token=tokens.remove(0);
                if (token.type!=Token.TokenType.冒号){
                    throw token.error("期待一个冒号");
                }
                //解析value部分
                Exp value=parseNormal(tokens);
                token = tokens.remove(0);
                switch (token.type){
                    case LRBracketToken:
                        right=token;
                        will=false;
                        break;
                    case 逗号:
                        break;
                    default:
                        throw token_unexpected(token);
                }
            } else {
                throw key.error("提前结束，不允许");
            }
        }
        if (will){
            throw left.error("未正确结束");
        }else {
            return new MapExp(left, children, right);
        }
    }

    /**
     * 泛型参数，其实应该区分声明和调用时
     * @param tokens
     * @return
     * @throws RangePathsException
     */
    public static List<Exp> parseMarcos(List<Token> tokens) throws RangePathsException {
        List<Exp> ms=new ArrayList<Exp>();
        boolean will=true;
        while (will){
            if (tokens.size()>2) {
                Token token = tokens.remove(0);
                if (token.type == Token.TokenType.IdToken) {
                    ms.add(new IDExp(token, token.value));
                }
                token=tokens.remove(0);
                if (token.type== Token.TokenType.ARBracketToken){
                    will=false;
                }else {
                    if (token.type != Token.TokenType.逗号) {
                        throw token_unexpected(token);
                    }
                }
            }else {
                throw token_end();
            }
        }
        return ms;
    }

    private static List<Exp> parseParams(List<Token> tokens) {
        return null;
    }

    public static Exp parseFunction(List<Token> tokens) throws RangePathsException {
        Token token=tokens.remove(0);
        FunctionExp fun=null;
        List<Exp> ms=null;
        List<Exp> as=null;
        switch (token.type){
            case ALBracketToken:
                /*有泛型*/
                ms=parseMarcos(tokens);
                as=parseParams(tokens);

                break;
            case SLBracketToken:
                /*有参数*/
                as=parseParams(tokens);
                break;
            case LLBracketToken:
                /*无参数*/
                break;
            default:
                throw token.error("不是期待的类型");
        }
        return fun;
    }


    public static Exp parseCall(List<Token> tokens){
        return null;
    }
    public static Exp parseNormal(List<Token> tokens) throws RangePathsException {
        Token token=tokens.remove(0);
        Exp exp=null;
        switch (token.type){
            case StringToken:
                exp=new StringExp(token,token.value.substring(1,token.value.length()-1));
                break;
            case IdToken:
                /*函数调用或函数定义，变量*/
                if ("fn".equals(token.value))
                {
                    exp=parseFunction(tokens);
                }else{
                    exp=parseCall(tokens);
                }
                break;
            case IntToken:
                exp=new IntExp(token,Integer.parseInt(token.value));
                break;
            case TrueToken:
                exp=new BoolExp(token,true);
                break;
            case FalseToken:
                exp=new BoolExp(token,false);
                break;
            case LLBracketToken:
                exp=parseMap(tokens);
                break;
            case MLBracketToken:
                exp=parseList(tokens);
                break;
            default:
                throw token.error("不正确的token");
        }
        return exp;
    }

    private static Exp parseList(List<Token> tokens) throws RangePathsException {
        Token left=tokens.remove(0);//[
        Token right=null;
        ListExp exp=null;
        List<Exp> children=new ArrayList<Exp>();
        boolean will=true;
        while (tokens.size()>0 && will){
            children.add(parseNormal(tokens));
            Token token=tokens.remove(0);
            switch (token.type){
                case 逗号:
                    break;
                case MRBracketToken:
                    right=token;
                    will=false;
                    break;
                default:
                    throw token.error("不正确的token");
            }
        }
        if (will){
            throw left.error("未正确结束");
        }else {
            return new ListExp(left, children, right);
        }
    }
}
