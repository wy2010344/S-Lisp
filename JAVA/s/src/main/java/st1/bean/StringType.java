package st1.bean;

public class StringType extends Type {
    private static StringType instance=new StringType();
    private StringType(){}
    public static StringType getInstance() {
        return instance;
    }
}
