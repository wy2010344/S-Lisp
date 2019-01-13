package s.exp;

import s.Token;

public class LetIDExp extends AtomExp{
    private String value;

    public LetIDExp(Token block, String value) {
        super(block);
        this.value=value;
    }
    public String getValue(){
        return value;
    }
}
