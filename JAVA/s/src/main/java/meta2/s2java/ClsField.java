package meta2.s2java;

public class ClsField {
    public final ClsFieldType type;
    public ClsField(ClsFieldType type){
        this.type=type;
    }

    /*ID类型*/
    public String IDType;//默认类型
    public String IDDefaultValue;//默认值

    /*函数类型*/
    public String FnType;//函数类型
    public String FnDefaultBody;//函数默认体
}
