package meta2.typecheck;

import mb.RangePathsException;
import meta2.Exp;
import meta.Node;
import meta.Token;
import meta2.ExpType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * 先将每个文件的类型注入全局
 * 再进行验证（每个文件）
 * 再执行（从指定main文件）
 */
public class Load {
    static void log(String msg){
        System.out.println(msg);
    }

    static Map<String,Object> map=new TreeMap<String,Object>();

    /**
     * 对导入的处理
     * @param path
     * @param imports
     * @param exp
     * @throws RangePathsException
     */
    static void dealImports(String path,Map<String,String> imports,Exp exp) throws RangePathsException {
        String name="";
        String absolute_path="";
        if (exp.type== ExpType.IDExp){
            //直接导入
            String relative_path=exp.value;
            absolute_path=mb.Util.path_join(path,relative_path);
            name=absolute_path.substring(absolute_path.lastIndexOf("/"));
        }else if(exp.type==ExpType.BracketExp){
            //别名化
            if (exp.children!=null && exp.children.length==2){
                Node<Exp> cs=exp.children;
                Exp rename_exp=cs.first;
                cs=cs.rest;
                Exp relative_exp=cs.first;
                if (rename_exp.type!=ExpType.IDExp){
                    throw rename_exp.exception("必须是ID类型");
                }
                if (relative_exp.type!=ExpType.IDExp){
                    throw relative_exp.exception("必须是IDExp");
                }
                name=rename_exp.value;
                absolute_path=mb.Util.path_join(path,relative_exp.value);
            }else{
                throw exp.exception("需要2个参数");
            }
        }else{
            throw exp.exception("暂不支持的表达式类型");
        }
        if (imports.containsKey(name)){
            throw exp.exception("同名已经导入");
        }else {
            imports.put(name, absolute_path);
        }
    }
    private static void dofile(String path,Node<Exp> exps) throws RangePathsException {
        Map<String,String> imports=new TreeMap<String, String>();
        while (exps!=null){
            Exp exp=exps.first;
            exps=exps.rest;
            if (exps!=null){
                //import部分
                dealImports(path,imports,exp);
            }else{
                //export部分，可能是函数，可能是结构体
                if (exp.type==ExpType.BracketExp){
                    Node<Exp> cs=exp.children;
                    if (cs==null){
                        throw exp.exception("不允许空()");
                    }else{
                        Exp type_exp=cs.first;
                        if (type_exp.type==ExpType.IDExp){
                            String type_name=type_exp.value;
                            /**
                             * 还要绑定自身
                             * 有泛型
                             */
                            if ("class".equals(type_name)){
                                //结构体
                                if (cs.rest!=null){
                                    if (cs.rest.length%2!=0){
                                        throw type_exp.exception("需要偶数个参数");
                                    }
                                }
                                doClass(cs.rest,imports);
                            }else if("fn".equals(type_name)){
                                //函数
                            }else{
                                throw type_exp.exception("尚未支持ID类型");
                            }
                        }else{
                            throw type_exp.exception("仅支持ID类型的Exp");
                        }
                    }
                }else{
                    throw exp.exception("尚不支持括号表达式以外的类型");
                }
            }
        }
    }

    private static void doClass(Node<Exp> args,Map<String,String> imports) throws RangePathsException {
        Map<String,ClsField> map=new TreeMap<String, ClsField>();
        while (args!=null){
            Exp key_exp=args.first;
            args=args.rest;
            Exp value_exp=args.first;
            args=args.rest;
            if (key_exp.type==ExpType.IDExp){
                String key=key_exp.value;
                if (map.containsKey(key)){
                    throw key_exp.exception("不允许重复的key");
                }else{
                    if (value_exp.type==ExpType.IDExp){
                        //值属性
                        map.put(
                                key,
                                new ClsField(imports.get(value_exp.value))
                        );
                    }else if(value_exp.type==ExpType.BracketExp){
                        /*是否支持默认值呢？*/
                        Node<Exp> vcs=value_exp.children;
                        if (vcs==null){
                            throw value_exp.exception("不允许空");
                        }else{
                            Exp v_first_exp=vcs.first;
                            if (v_first_exp.type==ExpType.IDExp){
                                if ("fn".equals(v_first_exp.value))
                                {
                                    map.put(key,parseFn(value_exp,imports));
                                }else if("union".equals(v_first_exp.value)){
                                    map.put(key,parseUnion(value_exp,imports));
                                }else if("tuple".equals(v_first_exp.value)){
                                    map.put(key,parseTuple(value_exp,imports));
                                }else if("default".equals(v_first_exp.value)){
                                    throw v_first_exp.exception("尚不支持");
                                }else{
                                    throw v_first_exp.exception("尚不支持");
                                }
                            }else{
                                throw v_first_exp.exception("尚不支持");
                            }
                        }
                    }else{
                        throw value_exp.exception("尚不支持的value类型");
                    }
                }
            }else{
                throw key_exp.exception("目前不支持的类型");
            }
        }
    }

    /**
     * 解析一个列表的参数
     * @param type
     * @param vcs
     * @param imports
     * @return
     * @throws RangePathsException
     */
    private static ClsField parseTypeMulti(ClsFieldType type,Node<Exp> vcs,Map<String,String> imports) throws RangePathsException {
        Node<ClsField> r_params=null;
        while (vcs!=null){
            r_params=Node.extend(parseType(vcs.first,imports),r_params);
            vcs=vcs.rest;
        }
        return new ClsField(type,r_params);
    }

    /**
     * 解析函数
     * @param exp
     * @param imports
     * @return
     * @throws RangePathsException
     */
    private static ClsField parseFn(Exp exp,Map<String,String> imports) throws RangePathsException {
        Node<Exp> vcs=exp.children.rest;
        if (vcs==null){
            throw exp.exception("需要一个参数");
        }else{
            return parseTypeMulti(ClsFieldType.Fn,vcs,imports);
        }
    }
    /**
     * 解析联合
     * @param exp
     * @param imports
     * @return
     * @throws RangePathsException
     */
    private static ClsField parseTuple(Exp exp, Map<String, String> imports) throws RangePathsException {
        Node<Exp> vcs=exp.children.rest;
        if (vcs==null || vcs.length==1){
            throw exp.exception("需要2个参数");
        }else {
            return parseTypeMulti(ClsFieldType.Tuple, vcs, imports);
        }
    }
    /**
     * 解析联合
     * @param exp
     * @param imports
     * @return
     * @throws RangePathsException
     */
    private static ClsField parseUnion(Exp exp, Map<String, String> imports) throws RangePathsException {
        Node<Exp> vcs=exp.children.rest;
        if (vcs==null || vcs.length==1){
            throw exp.exception("需要2个参数");
        }else {
            return parseTypeMulti(ClsFieldType.Union, vcs, imports);
        }
    }
    /**
     * 将表达式解析成类型
     * @param exp
     * @param imports
     * @throws RangePathsException
     */
    private static ClsField parseType(Exp exp, Map<String,String> imports) throws RangePathsException {
        if (exp.type==ExpType.IDExp){
            return new ClsField(imports.get(exp.value));
        }else if (exp.type==ExpType.BracketExp){
            Node<Exp> vcs=exp.children;
            if (vcs==null){
                throw exp.exception("需要至少一个参数");
            }else{
                Exp v_first_exp=vcs.first;
                if (v_first_exp.type==ExpType.IDExp){
                    String v_first=v_first_exp.value;
                    if ("fn".equals(v_first)){
                        //函数
                        return parseFn(exp,imports);
                    }else if("tuple".equals(v_first)){
                        //元组
                        return parseTuple(exp,imports);
                    }else if ("union".equals(v_first)) {
                        //联合类型
                        return parseUnion(exp,imports);
                    }else {
                        throw v_first_exp.exception("尚不支持");
                    }
                }else{
                    throw v_first_exp.exception("尚不支持");
                }
            }
        }else{
            throw exp.exception("尚不支持");
        }
    }

    /**
     * 解析函数参数
     * @param args
     */
    private static void doFnParams(Node<Exp> args, HashMap<String,String> imports) throws RangePathsException {
        while (args!=null){
            Exp exp=args.first;
            args=args.rest;
            if (exp.type==ExpType.IDExp){
                //引入类型

            }else if(exp.type==ExpType.BracketExp){
                //函数类型、
            }else{
                throw exp.exception("尚不支持");
            }
        }
    }

    private static void circle(File folder) {
        File[] cs=folder.listFiles();
        for(File file:cs){
            if (file.isDirectory()){
                circle(file);
            }else {
                String suffix = ".st";
                if (file.getName().endsWith(suffix)) {
                    try {
                        String txt=mb.Util.readTxt(file,"\n","UTF-8");
                        Node<Token> tokens= Token.run(txt);
                        Node<Exp> exps= Exp.parse(tokens);
                        if (exps!=null) {
                            dofile(file.getAbsolutePath(),exps);
                        }else{
                            log("未解析出表达式："+file.getAbsolutePath());
                        }
                    } catch (IOException e) {
                        log("读取文件内容失败:"+file.getAbsolutePath());
                        e.printStackTrace();
                    } catch (RangePathsException e) {
                        log("解析内容失败:"+file.getAbsolutePath());
                        e.printStackTrace();
                    }
                } else {
                    log("不是st文件：" + file.getAbsolutePath());
                }
            }
        }
    }

    public static void run(String folder_path) throws Exception {
        File folder=new File(folder_path);
        if (folder.exists() && folder.isDirectory()){
            circle(folder);
        }else{
            throw new Exception("不是有效目录："+folder_path);
        }
    }


    public static void main(String[] args){
        try {
            run(mb.Util.resource("./src",Load.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
