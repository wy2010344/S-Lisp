package meta.macro;

import meta.*;

public class LambdaMap extends LibReadMarco {
    public final boolean with_this;
    public LambdaMap(boolean with_this) {
        this.with_this = with_this;
    }

    @Override
    protected Object run(ScopeNode scope, Node<Exp> args) throws Throwable {
        int length=1;
        if (with_this){
            length++;
        }
        if (args==null || args.length<length){
            throw new Exception("至少需要"+length+"个参数");
        }else{
            Exp map_id=args.first;
            if (map_id.isIDExp()){
                args=args.rest;
                if (with_this){
                    Exp this_id=args.first;
                    if (this_id.isIDExp()){
                        args=args.rest;
                        return new UserMapFunction(
                                scope,
                                map_id.asIDExp(),
                                this_id.asIDExp(),
                                args
                        );
                    }else{
                        throw this_id.exception("需要id类型绑定this");
                    }
                }else{
                    return new UserMapFunction(
                            scope,
                            map_id.asIDExp(),
                            null,
                            args
                    );
                }
            }else{
                throw map_id.exception("需要id类型绑定map");
            }
        }
    }

    public static class UserMapFunction extends LibReadMarco{
        private final ScopeNode parentScope;
        private final IDExp map_id;
        private final IDExp this_id;
        private final Node<Exp> exps;

        public UserMapFunction(
                ScopeNode parentScope,
                IDExp map_id,
                IDExp this_id,
                Node<Exp> exps){
            this.parentScope=parentScope;
            this.map_id=map_id;
            this.this_id=this_id;
            this.exps=exps;
        }

        @Override
        protected Object run(ScopeNode scope, Node<Exp> args) throws Throwable {
            if (args!=null && args.length%2==1){
                throw new Exception("需要偶数个参数");
            }
            ScopeNode param=null;
            while (args!=null){
                Exp key_exp=args.first;
                String key=null;
                if (key_exp.isIDExp()){
                    key=key_exp.asIDExp().value;
                }else if(key_exp.isStringExp()){
                    key=key_exp.asStringExp().value;
                }else{
                    throw key_exp.exception("需要id或字符串类型");
                }
                args=args.rest;
                Exp value_exp=args.first;
                Object value=run_read_exp(scope,value_exp);
                param=ScopeNode.extend(key,value,param);
                args=args.rest;
            }
            ScopeNode this_scope=ScopeNode.extend(map_id.value,param,parentScope);
            if (this_id!=null){
                this_scope=ScopeNode.extend(this_id.value,this,this_scope);
            }
            return UserReadMacro.run(scope,exps);
        }
    }
}
