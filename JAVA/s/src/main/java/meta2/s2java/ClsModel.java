package meta2.s2java;

import java.util.Map;
import java.util.TreeMap;

public class ClsModel {
    public Map<String,ClsField> map=new TreeMap<String,ClsField>();

    public static ClsModel StringModel;
    static {
        StringModel=new ClsModel();

    }
}
