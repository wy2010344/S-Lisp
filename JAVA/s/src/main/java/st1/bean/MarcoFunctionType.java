package st1.bean;

import s.Node;

public class MarcoFunctionType extends Type {
    @Override
    public boolean equals(Type type) {
        return false;
    }
    public FunctionType downType(Node<Type> evalListFromName){
        return new FunctionType();
    }
}
