package st1.bean;

import s.Function;
import s.Node;
import st1.Exp;

public class FunctionType extends Type {

    Node<Type> params;
    Type ret;
    /*函数调用，返回类型*/
    public Type match(Node<Type> children) {
        return null;
    }
    /*函数类型下转*/
    public Type downType(Node<Type> children) {
        return null;
    }

    @Override
    public boolean equals(Type type) {
        if (type instanceof FunctionType){
            FunctionType functionType= (FunctionType) type;
            if (functionType.ret.equals(ret)){
                if (params==null && functionType.params==null){
                    return true;
                }
                if (params!=null && functionType.params!=null){
                    if (params.Length()==functionType.params.Length()){
                        boolean eq=true;
                        Node<Type> p1=params;
                        Node<Type> p2=functionType.params;
                        while (p1!=null && eq){
                            eq=(p1.First().equals(p2.First()));
                            p1=p1.Rest();
                            p2=p2.Rest();
                        }
                        return eq;
                    }
                }
            }
        }
        return false;
    }
}
