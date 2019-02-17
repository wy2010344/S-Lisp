package meta.macro;

import meta.Exp;
import meta.LibReadMarco;
import meta.Node;
import meta.ScopeNode;

public class Chain extends LibReadMarco {
    @Override
    protected Object run(ScopeNode scope, Node<Exp> args) throws Throwable {
        Object o=run_read_exp(scope,args.first);
        args=args.rest;
        while (args!=null){
            Exp exp=args.first;
            args=args.rest;
            if (exp.isIDExp()){
                //从作用域上寻找方法
                if (args==null){
                    throw new Exception("id类型后面需要参数");
                }else {
                    Exp param = args.first;
                    args=args.rest;
                    if (o instanceof String) {

                    }else if(o instanceof Node){

                    }else if(o instanceof ScopeNode){

                    }
                }
            }else if (exp.isStringExp()){
                //字符串，获得ScopeNode下标
            }else if(exp.isBracketExp()){
                //括号
                if (o instanceof ScopeNode){
                    //字典下标
                }else{
                    //不合法？
                }
            }
        }
        return null;
    }
}
