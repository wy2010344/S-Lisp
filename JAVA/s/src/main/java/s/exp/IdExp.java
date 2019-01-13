package s.exp;

import s.Token;
import s.Node;

public class IdExp extends AtomExp {
    public IdExp(Token block, Node<Object> ids) {
        super(block);
        this.ids=ids;
    }
    private final Node<Object> ids;
    public Node<Object> getIds(){
        return ids;
    }
}
