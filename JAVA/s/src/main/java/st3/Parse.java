package st3;

import mb.RangePathsException;
import st3.macro.*;

import java.io.IOException;

public class Parse {
    public static Object run(String path){
        try {
            ScopeNode scope=Library.buildScope();
            Object o=Load.run(scope,path);
            if (o!=null){
                if (o instanceof UserFunction){
                    System.out.println(((UserFunction) o).run(null));
                }else{
                    System.out.println(o.toString());
                }
            }else{
                System.out.println("没有返回值");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (RangePathsException e) {
            e.printStackTrace();
        }catch (Throwable e){
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args){
        Parse.run("D:/usr/web/app/S-Meta/test/test.txt");
    }
}
