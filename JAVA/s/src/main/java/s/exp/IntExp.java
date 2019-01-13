package s.exp;

import s.Token;

public class IntExp extends AtomExp {
    private int value;
    public IntExp(Token block, int value) {
        super(block);
        this.value=value;
    }
    public int getValue(){
        return value;
    }
}
