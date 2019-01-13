package s.exp;

import s.Token;

public class BooleanExp extends AtomExp {
    private boolean value;

    public BooleanExp(Token block, boolean value) {
        super(block);
        this.value=value;
    }
    public boolean getValue(){return value;}
}
