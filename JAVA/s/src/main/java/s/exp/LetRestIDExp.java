package s.exp;

import s.Token;

public class LetRestIDExp extends AtomExp {

    private String value;

    public LetRestIDExp(Token block, String value) {
        super(block);
        this.value=value;
    }
    public String getValue(){
        return value;
    }
}
