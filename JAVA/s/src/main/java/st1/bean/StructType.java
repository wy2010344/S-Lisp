package st1.bean;

import java.util.Map;
import java.util.TreeMap;

public class StructType extends Type{
    private Map<String,Type> map=new TreeMap<String, Type>();
    public Type get(String value) {
        return map.get(value);
    }

    @Override
    public boolean equals(Type type) {
        return false;
    }
}
