package st3.macro;

import mb.RangePathsException;
import s.Node;
import st3.*;

/**
 * (lambda () () () ())
 */
public class Lambda extends ReadMacro {
    private final boolean with_this;
    private final boolean with_args;
    public Lambda(boolean with_args,boolean with_this){
        this.with_args=with_args;
        this.with_this=with_this;
    }

    @Override
    public Object exec(Node<Object> scope, BracketExp bracketExp) throws RangePathsException {
        Node<Exp> args=bracketExp.children.Rest();
        int length=1;
        if (with_args){
            length++;
        }
        if (with_this){
            length++;
        }
        if (args==null || args.Length()<length){
            throw bracketExp.exception("至少需要"+length+"个参数");
        }else{
            Exp params=null;
            if (with_args){
                params=args.First();
                args=args.Rest();
                if (!(params instanceof BracketExp)){
                    throw params.exception("参数绑定是列表");
                }
            }
            Exp this_name=null;
            if (with_this){
                this_name=args.First();
                args=args.Rest();
                if (!(this_name instanceof IDExp)){
                    throw params.exception("this_name绑定不是D类型");
                }
            }

            return new UserFunction(
                scope,
                (BracketExp)params,
                (IDExp)this_name,
                args
            );
        }
    }
}
