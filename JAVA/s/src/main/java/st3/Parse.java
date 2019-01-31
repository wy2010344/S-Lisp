package st3;

import mb.RangePathsException;
import s.Node;
import st3.function.*;
import st3.macro.GetValue;
import st3.macro.IsList;
import st3.macro.Lambda;
import st3.macro.MacroDef;

import java.io.IOException;

public class Parse {
    static Node<Object> buildScope(){
        Node<Object> scope=null;
        /*宏*/
        scope=Node.kvs_extend("macro-def",new MacroDef(),scope);
        scope=Node.kvs_extend("string",new GetValue(),scope);
        scope=Node.kvs_extend("isList",new IsList(),scope);
        scope=Node.kvs_extend("fn",new Lambda(false,false),scope);
        scope=Node.kvs_extend("fn-n",new Lambda(true,false),scope);
        scope=Node.kvs_extend("fn-n-x",new Lambda(true,true),scope);
        scope=Node.kvs_extend("fn-x",new Lambda(false,true),scope);
        scope=Node.kvs_extend("let-kvs",new LetMarco(),scope);
        /*函数*/
        scope=Node.kvs_extend("kvs-find1st",new KvsFind1st(),scope);
        scope=Node.kvs_extend("apply",new Apply(),scope);
        scope=Node.kvs_extend("log",new Log(),scope);
        return scope;
    }
    public static Object run(String path){
        try {
            String txt=mb.Util.readTxt(path,"\n","UTF-8");
            Node<Token> tokens=Token.run(txt);
            Node<Exp> exps=Exp.parse(tokens);
            Node<Object> scope=buildScope();
            return UserReadMacro.run(exps,scope);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (RangePathsException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args){
        Parse.run(mb.Util.resource("./st3/a.st",Parse.class));
    }
}
