package meta2.typecheck;

import meta.Node;

public class ClsField {
    public final ClsFieldType type;

    /**
     * ID类型的字段名
     */
    public final String id_name;

    public ClsField(String id_name){
        this.type=ClsFieldType.ID;
        this.id_name=id_name;
        this.r_args=null;
        this.args=null;
    }

    /*函数，联合或其它*/
    public final Node<ClsField> r_args;
    public final Node<ClsField> args;
    public ClsField(ClsFieldType type, Node<ClsField> r_params){
        this.type=type;
        this.id_name="";
        this.r_args=r_params;
        this.args=Node.reverse(r_args);
    }
}
