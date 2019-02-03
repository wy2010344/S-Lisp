package meta;

public class ScopeNode {
    public final ScopeNode rest;
    public final String key;
    public final Object value;
    public ScopeNode(String key, Object value, ScopeNode rest){
        this.key=key;
        this.value=value;
        this.rest=rest;
    }
    public static ScopeNode extend(String key,Object value,ScopeNode rest){
        return new ScopeNode(key,value,rest);
    }
    /**
     * 返回(boolean,value)
     * @param kvs
     * @param key
     * @return
     */
    public static Object find_1st(ScopeNode kvs,String key) throws Exception {
        boolean unfind=true;
        Object value=null;
        while (kvs!=null && unfind){
            if (key.equals(kvs.key)){
                unfind=false;
                value=kvs.value;
            }else{
                kvs=kvs.rest;
            }
        }
        if (unfind){
            throw new Exception("未找到定义"+key);
        }else{
            return value;
        }
    }
}
