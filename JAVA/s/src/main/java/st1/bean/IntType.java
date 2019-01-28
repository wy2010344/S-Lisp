package st1.bean;

public class IntType extends Type {
    private static IntType instance=new IntType();
    private IntType(){

    }
    public static IntType getInstance() {
        return instance;
    }
}
