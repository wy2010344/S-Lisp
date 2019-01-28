package st1.exp;

import st1.AbstractAtomExp;

public class IdExp extends AbstractAtomExp {
    public final String value;
    public IdExp(String value){
        this.value=value;
    }
}
