package meta;

import meta.macro.*;

public class Library {
    public static ScopeNode buildScope(){
        ScopeNode scope=null;
        /*宏*/
        scope=ScopeNode.extend("let",new LetMarco(),scope);
        scope=ScopeNode.extend("macro-def",new MacroDef(),scope);
        scope=ScopeNode.extend("fn",new Lambda(false,false),scope);
        scope=ScopeNode.extend("fn-n",new Lambda(true,false),scope);
        scope=ScopeNode.extend("fn-n-x",new Lambda(true,true),scope);
        scope=ScopeNode.extend("fn-x",new Lambda(false,true),scope);
        scope=ScopeNode.extend("if",new If(),scope);
        scope=ScopeNode.extend("if-run",new IfRun(),scope);
        scope=ScopeNode.extend("string-token",new StringToken(),scope);
        /*函数*/
        scope=ScopeNode.extend("cache",new Cache(),scope);
        scope=ScopeNode.extend("exp-toString",new ExpToString(),scope);
        scope=ScopeNode.extend("exp-isBracket",new ExpIsBracket(),scope);
        scope=ScopeNode.extend("exp-bracketChildren",new ExpBracketChildren(),scope);
        scope=ScopeNode.extend("exp-parse",new ExpParse(),scope);
        scope=ScopeNode.extend("exp-let",new ExpLetMarco(),scope);
        scope=ScopeNode.extend("apply",new Apply(),scope);
        scope=ScopeNode.extend("list",new List(false),scope);
        scope=ScopeNode.extend("xlist",new List(true),scope);
        scope=ScopeNode.extend("map",new Map(false),scope);
        scope=ScopeNode.extend("xmap",new Map(true),scope);
        scope=ScopeNode.extend("quote",new Quote(),scope);
        scope=ScopeNode.extend("default",new Default(),scope);
        scope=ScopeNode.extend("log",new Log(),scope);
        scope=ScopeNode.extend("str-join",new StringJoin(),scope);
        scope=ScopeNode.extend("str-eq",new StringEq(),scope);
        scope=ScopeNode.extend("reduce",new Reduce(),scope);
        scope=ScopeNode.extend("read",new Read(),scope);
        scope=ScopeNode.extend("write",new Write(),scope);
        return scope;
    }
}
class Apply extends Function {
    @Override
    public Object run(Node<Object> args) throws Throwable {
        if (args==null ||args.length>2){
            throw new Exception("需要1~2个参数");
        }else{
            Object fun=args.first;
            if (fun instanceof ReadMacro){
                args=args.rest;
                Object the_args=null;
                if (args!=null) {
                    the_args = args.first;
                    if (!(the_args instanceof Node)){
                        throw new Exception("参数3应该是列表类型");
                    }
                }
                return ((Function) fun).run((Node<Object>)the_args);
            }else{
                throw new Exception("参数2不是合法的函数");
            }
        }
    }
}
/**
 * 获得括号表达式的children;
 */
class ExpBracketChildren extends Function {
    @Override
    public Object run(Node<Object> args) throws Throwable {
        if (args==null || args.length!=1){
            throw new Exception("需要一个参数");
        }
        Object o=args.first;
        if (o instanceof BracketExp){
            return ((BracketExp) o).children;
        }else{
            throw new Exception("参数需要是BracketExp");
        }
    }
}
/**
 * 判断某个参数是否是列表宏。
 * (is-bracket-exp)
 */
class ExpIsBracket extends Function {
    @Override
    public Object run(Node<Object> args) throws Throwable {
        if (args==null || args.length!=1){
            throw new Exception("期待1个参数");
        }else{
            Object o=args.first;
            if (o==null){
                throw new Exception("参数计算结果为null");
            }else{
                return (o instanceof BracketExp);
            }
        }
    }
}
/**
 * 将表达式在作用域中展开
 * (Parse scope exp)
 */
class ExpParse extends Function {
    @Override
    public Object run(Node<Object> args) throws Throwable {
        if (args==null || args.length!=2){
            throw new Exception("需要2个参数");
        }else{
            Object scope=args.first;
            if (scope instanceof ScopeNode){
                args=args.rest;
                Object exp=args.first;
                if (exp instanceof Exp){
                    return run_read_exp((ScopeNode)scope,(Exp)exp);
                }else{
                    throw new Exception("参数2应该是Exp类型");
                }
            }else{
                throw new Exception("参数1应该是作用域类型");
            }
        }
    }
}
/**
 * 将参数表达式转成字符串，如果是列表，则与string-token中处理一样。
 */
class ExpToString extends Function {
    @Override
    public Object run(Node<Object> args) throws Throwable {
        if (args==null){
            throw new Exception("需要1个参数");
        }else if (args.length==1){
            if (args.first instanceof IDExp){
                return ((IDExp) args.first).value;
            }else if(args.first instanceof StringExp){
                return ((StringExp)args.first).value;
            }else{
                if (args.first instanceof BracketExp) {
                    StringBuilder sb = new StringBuilder();
                    StringToken.join_bracket((BracketExp) args.first,sb);
                    return sb.toString();
                }else{
                    throw new Exception("不是合法的Exp类型");
                }
            }
        }else{
            throw new Exception("目前仅支持一个参数");
        }
    }
}
class Log extends Function {
    @Override
    public Object run(Node<Object> args) throws Throwable {
        while (args!=null){
            if (args.first==null){
                System.out.print("[]");
            }else{
                System.out.print(args.first.toString());
            }
            System.out.print(" ");
            args=args.rest;
        }
        System.out.println();
        return null;
    }
}
class Quote extends Function {
    @Override
    public Object run(Node<Object> args) throws Throwable {
        if (args==null || args.length!=1){
            throw new Exception("仅需要一个参数");
        }else{
            return args.first;
        }
    }
}

/**
 * 字符串连接
 */
class StringJoin extends Function {
    @Override
    public Object run(Node<Object> args) throws Exception {
        Object o = args.first;
        if (o instanceof Node) {
            StringBuilder sb = new StringBuilder();
            args = args.rest;
            Node<Object> vs = (Node<Object>) o;
            String split = "";
            if (args != null) {
                o = args.first;
                if (o instanceof String) {
                    split = (String) o;
                } else {
                    throw new Exception("分割符应该是字符串");
                }
            }
            for (Node<Object> tmp = vs; tmp != null; tmp = tmp.rest) {
                o = vs.first;
                if (o instanceof String) {
                    sb.append(o);
                } else {
                    throw new Exception(o.toString() + "不是字符串类型");
                }
                sb.append(split);
            }
            sb.setLength(sb.length() - split.length());
            return sb.toString();
        }
        else
        {
            throw new Exception("参数1需要是列表");
        }
    }
}
class Read extends Function {
    @Override
    public Object run(Node<Object> args) throws Exception {
        if (args == null || args.length != 1) {
            throw new Exception("需要1个参数");
        } else {
            if (args.first instanceof String) {
                return mb.Util.readTxt((String) args.first, "\n", "UTF-8");
            } else {
                throw new Exception("参数需要是字符串");
            }
        }
    }
}
class Write extends Function {
    @Override
    public Object run(Node<Object> args) throws Exception {
        if (args == null || args.length != 2) {
            throw new Exception("参数需要是2个");
        } else {
            Object path = args.first;
            args = args.rest;
            Object content = args.first;
            if (path instanceof String) {
                if (content instanceof String) {
                    mb.Util.saveTxt((String) path, (String) content, "UTF-8");
                    return null;
                } else {
                    throw new Exception("内容不是字符串类型");
                }
            } else {
                throw new Exception("路径不是字符串类型");
            }
        }
    }
}

class Default extends Function{
    @Override
    public Object run(Node<Object> args) throws Throwable {
        if (args==null || args.length!=2){
            throw new Exception("需要2个参数");
        }else{
            if(args.first==null){
                return args.rest.first;
            }else{
                return args.first;
            }
        }
    }
}

class EmptyFn extends Function{
    @Override
    public Object run(Node<Object> args) throws Throwable {
        return null;
    }
}

class StringEq extends Function{
    @Override
    public Object run(Node<Object> args) throws Throwable {
        boolean ret=true;
        Object first=args.first;
        Node<Object> tmp=args.rest;
        if (first instanceof String){
            while (tmp!=null && ret){
                Object next=tmp.first;
                tmp=tmp.rest;
                if (next instanceof String){
                    if (first.equals(next)){
                        first=next;
                    }else{
                        ret=false;
                    }
                }else{
                    ret=false;
                }
            }
        }else{
            ret=false;
        }
        return ret;
    }
}

class Reduce extends Function{
    @Override
    public Object run(Node<Object> args) throws Throwable {
        if (args!=null && args.length>2 && args.length<4){
            Object list_o=args.first;
            args=args.rest;
            Object run_o=args.first;
            args=args.rest;
            Object init=args==null?null:args.first;
            if (list_o instanceof Node && run_o instanceof Function){
                Node<Object> list= (Node<Object>) list_o;
                Function run= (Function) run_o;
                while (list!=null){
                    init=run.run(Node.list(init,list.first));
                    list=list.rest;
                }
                return init;
            }else{
                throw new Exception("参数类型不对");
            }
        }else{
            throw new Exception("需要2~3个参数");
        }
    }
}

class Cache extends Function{
    @Override
    public Object run(Node<Object> args) throws Throwable {
        Object value=null;
        if (args!=null && args.length==1){
            if (args.length==1) {
                value = args.first;
            }else{
                throw new Exception("最多一个参数");
            }
        }
        return new CacheValue(value);
    }
    static class CacheValue extends Function{
        public CacheValue(Object value){
            this.value=value;
        }
        private Object value;
        @Override
        public Object run(Node<Object> args) throws Throwable {
            if (args==null){
                return value;
            }else{
                if (args.length>1){
                    throw new Exception("只需要一个参数");
                }else{
                    value=args.first;
                }
                return null;
            }
        }
    }
}