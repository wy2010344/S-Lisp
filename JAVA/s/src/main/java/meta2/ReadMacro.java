package meta2;

import mb.RangePathsException;
import meta.Node;
import meta.ScopeNode;

public abstract class ReadMacro {
    public abstract Object exec(ScopeNode scope, Node<Exp> args)throws Throwable;

    public static Object run(ScopeNode scope,Exp exp) throws RangePathsException {
        Object o=null;
        if (exp.type==ExpType.BracketExp){
            Exp first_exp= exp.children.first;
            Object first=run(scope,first_exp);
            if (first instanceof ReadMacro){
                try {
                    o=((ReadMacro) first).exec(scope, exp.children.rest);
                }catch (RangePathsException e){
                    throw e;
                }catch (Throwable e){
                    throw exp.exception(e.getMessage());
                }
            }
        }else if(exp.type==ExpType.StringExp){
            o=exp.value;
        }else if(exp.type==ExpType.IDExp){
            o=ScopeNode.find_1st(scope,exp.value);
        }
        return o;
    }
}
