package st3.macro;

import mb.RangePathsException;
import st3.*;
import st3.macro.util.SingleArg;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * 只支持ID作参数？不支持动态计算路径？
 */
public class Load extends SingleArg {
    private final ScopeNode scope;
    private final String path;
    public Load(String path, ScopeNode scope){
        this.path=path;
        this.scope=scope;
    }
    private Map<String,Object> caches=new TreeMap<String,Object>();
    private boolean onload=false;
    @Override
    protected Object run(ScopeNode scope, Exp exp) throws RangePathsException {
        if (onload){
            throw exp.exception("正在加载，不允许同时加载");
        }else {
            onload=true;
            Object o=null;
            if (exp instanceof BracketExp){
                throw exp.exception("必须为id或字符串类型");
            }else {
                String re_path=null;
                if (exp instanceof IDExp) {
                    re_path= ((IDExp) exp).value;
                } else if (exp instanceof StringExp) {
                    re_path=((StringExp)exp).value;
                }
                String allPath=mb.Util.path_join(path,re_path);
                if (caches.containsKey(allPath)){
                    o=caches.get(allPath);
                }else{
                    try {
                        o = run(scope, allPath);
                    }catch (RangePathsException e){
                        throw e;
                    }catch (Exception e){
                        throw exp.exception(e.getMessage());
                    }
                    caches.put(allPath,o);
                }
            }
            onload=false;
            return o;
        }
    }
    public static Object run(ScopeNode scope,String path) throws IOException, RangePathsException {
        Node<Token> tokens=Token.run(mb.Util.readTxt(path,"\n","UTF-8"));
        Node<Exp> exps=Exp.parse(tokens);
        scope=ScopeNode.extend("load",new Load(path,scope),scope);
        scope=ScopeNode.extend("pathOf",new PathOf(path),scope);
        return UserReadMacro.run(scope,exps);
    }

    static class PathOf extends Function{
        private final String path;
        public PathOf(String path){
            this.path=path;
        }
        @Override
        public Object run(Node<Object> args) throws Throwable {
            if (args==null){
                return path;
            }else{
                if (args.length>1){
                    throw new Exception("参数只能有一个");
                }else{
                    Object o=args.first;
                    if (o instanceof String){
                        return mb.Util.path_join(path,(String)o);
                    }else{
                        throw new Exception("参数1必须是字符串");
                    }
                }
            }
        }
    }
}
