package meta.macro;

import meta.*;

public class IfRun extends LibReadMarco {
    @Override
    protected Object run(ScopeNode scope, Node<Exp> args) throws Throwable {
        Object o=If.run_if(scope,args);
        if (o!=null && o instanceof Function){
            return ((Function) o).run(null);
        }else{
            return null;
        }
    }
}
