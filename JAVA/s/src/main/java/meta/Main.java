package meta;

import mb.RangePathsException;
import meta.macro.*;

import java.io.IOException;

public class Main {
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
        Main.run("D:/usr/web/app/S-Meta/test/test.txt");
    }
}
