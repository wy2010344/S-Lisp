package s;

import s.util.Location;

public class Token {
    public Token(
            String _value,
            Location _loc,
            Type _type){
        value=_value;
        loc=_loc;
        type=_type;
        original_type=_type;
    }
    //未转义前原始类型
    private Type original_type;
    public Type Original_type() {
    	return original_type;
    }
    private Type type;
    public Type Type(){
        return type;
    }
    //外部修改
    public void Type(Type _type){
        type=_type;
    }
    private String value;
    public String Value(){
        return value;
    }
    private Location loc;
    public Location Loc(){
        return loc;
    }
    
    @Override
    public String toString() {
    	return value;
    }
    
    public static enum Type{
        Id,//
        Quote,//'
        BraL,//([{
        BraR,//)]}
        Comment,//``
        Str,//"  "
        Int,
        Float,
    }
}
