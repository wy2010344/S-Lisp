package st.exp;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 *绑定，函数类型可省，从协议过来。
 * a:KM={
 *     x:98,
 *     y:fn(a,b){
 *
 *     }
 * }
 * 绑定
 * b=898
 * 函数声明，入参类型和变量不可省
 * c=fn(x:Int,y:Boolean):String{
 *
 * }
 * 调用
 * (w,y)=c(9,true)
 * 协议，类型不可省，变量可省
 * {
 *     x:Int,
 *     y:fn(Int,Boolean):String
 * }
 *
 * interface下，可值覆盖，可函数实现。
 */
public class InterfaceExp {
    private Map<String,TypeExp> kvs=new TreeMap<String,TypeExp>();
    public Map<String, TypeExp> getKvs() {
        return kvs;
    }

    public static class TypeExp{

    }
    public static class IdTypeExp extends TypeExp{

    }
    public static class FnTypeExp{

    }
}
