package meta.macro;

import meta.Exp;
import meta.LibReadMarco;
import meta.Node;
import meta.ScopeNode;

/**
 * 相当于点语法，路径访问，仅支持ID类型，
 * (kvs-path a b c d)
 * 允许空
 */
public class KvsPath extends LibReadMarco {

    @Override
    protected Object run(ScopeNode scope, Node<Exp> args) throws Throwable {
        Exp scope_exp=args.first;
        Object scope_object=run_read_exp(scope,scope_exp);
        if (scope_object instanceof ScopeNode){
            ScopeNode scopeNode=(ScopeNode)scope_object;
            args=args.rest;
            while (args!=null){
                Exp exp=args.first;
                args=args.rest;
                if (exp.isIDExp()){
                    try {
                        scope_object = ScopeNode.find_1st(scopeNode, exp.asIDExp().value);
                    }catch (Exception e){
                        System.out.println("未找到");
                        scope_object=null;
                    }
                    if (args!=null){
                        if (scope_object==null || scope_object instanceof ScopeNode){
                            scopeNode=(ScopeNode)scope_object;
                        }else{
                            throw exp.exception("需要仍然是kvs类型");
                        }
                    }
                }else{
                    throw exp.exception("只允许ID类型");
                }
            }
            return scope_object;
        }else{
            throw scope_exp.exception("参数1的结果应该是作kvs类型");
        }
    }
}
