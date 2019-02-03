package meta.macro;

import meta.Exp;
import meta.LibReadMarco;
import meta.Node;
import meta.ScopeNode;

/**
 * 使用ScopeNode作为存储结构
 * (kvs id value id value)
 * 其实路径访问，倒也可以用eval
 */
public class Kvs extends LibReadMarco {

    @Override
    protected Object run(ScopeNode scope, Node<Exp> args) throws Throwable {
        ScopeNode node=null;
        while (args!=null){
            Exp key_exp=args.first;
            args=args.rest;
            Exp value_exp=args.first;
            args=args.rest;
            if (key_exp.isIDExp()){
                node=ScopeNode.extend(key_exp.asIDExp().value,run_read_exp(scope,value_exp),node);
            }else{
                throw key_exp.exception("目前key部分只支持ID");
            }
        }
        return node;
    }
}
