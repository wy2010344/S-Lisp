package meta.macro;

import meta.*;

/**
 * (lambda () () () ())
 */
public class Lambda extends LibReadMarco {
    private final boolean with_this;
    private final boolean with_args;
    public Lambda(boolean with_args,boolean with_this){
        this.with_args=with_args;
        this.with_this=with_this;
    }


    @Override
    protected Object run(ScopeNode scope, Node<Exp> rest) throws Throwable {
        int length=0;
        if (with_args){
            length++;
        }
        if (with_this){
            length++;
        }
        if (0<length && rest.length<length){
            throw new Exception("至少需要"+length+"个参数");
        }else{
            Exp params=null;
            if (with_args){
                params=rest.first;
                rest=rest.rest;
                if (!(params instanceof BracketExp)){
                    throw params.exception("参数绑定是列表");
                }
            }
            Exp this_name=null;
            if (with_this){
                this_name=rest.first;
                rest=rest.rest;
                if (!(this_name instanceof IDExp)){
                    throw params.exception("this_name绑定不是D类型");
                }
            }

            return new UserFunction(
                scope,
                (BracketExp)params,
                (IDExp)this_name,
                rest
            );
        }
    }

}
