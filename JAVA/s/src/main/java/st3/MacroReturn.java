package st3;

import s.Node;

public class MacroReturn {
    public final Node<Object> scope;
    public final Object value;

    public MacroReturn(Node<Object> scope, Object value){
        this.scope=scope;
        this.value=value;
    }
}
