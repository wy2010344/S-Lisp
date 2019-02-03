package meta2.s2java;

import mb.RangePathsException;
import meta2.ExpType;
import meta.Node;
import meta.Token;
import meta2.Exp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * DSL应该是怎样的？
 * package不说了
 * import相对路径，不允许别名
 *
 * (class
 *      a Int
 *      b String
 *      c (fn a b c d)
 *      e (un X Y)
 * )
 * 平铺直译有点不好
 * 默认值支持，构造的一次化，生成的代码就是默认值。还有动态if分支
 * 相互关联与验证（联合类型的switch）
 * 再做IDE支持。
 * 其实真正困难的在表达式的验证！
 * 表达式是宏，可以自由组合的，因此递归验证。
 * 感觉很多Java表达式写不出来！不一定转到JAVA吧。
 * 获得类型验证，至少脚本语言的正确性保证了，再怎样解释都行，慢就慢吧。
 * 目前是纯面向对象的，String也是类型，不能兼容Java原生的类型，但要保证访问到字段。
 * 默认值与强制实现。
 */
public class S2Java {
    static void log(String msg){
        System.out.println(msg);
    }
    static String joinList(List<String> list,String split){
        StringBuilder sb=new StringBuilder();
        for (String str:list){
            sb.append(str).append(split);
        }
        sb.setLength(sb.length()-split.length());
        return sb.toString();
    }

    /**
     * 生成函数类型
     * @return
     */
    static String buildFunctionType(Exp key_exp, Node<Exp> fn_args) throws RangePathsException {
        List<String> list_in_args=new ArrayList<String>();
        String ret_type="void";
        while (fn_args!=null){
            Exp fn_arg=fn_args.first;
            fn_args=fn_args.rest;
            if (fn_args!=null){
                //入参 a b c d
                if (fn_arg.type== ExpType.IDExp){
                    list_in_args.add(fn_arg.value+" v"+fn_args.length);
                }else{
                    throw fn_arg.exception("尚不支持复合类型");
                }
            }else{
                //返回参数
                if (fn_arg.type==ExpType.IDExp){
                    ret_type=fn_arg.value;
                }else{
                    throw fn_arg.exception("尚不支持复合类型");
                }
            }
        }
        return ret_type+" "+key_exp.value+"("+joinList(list_in_args,",")+")";
    }

    /**
     * 一次性表达式
     * @param exp
     * @return
     */
    static String buildReadExp(Exp exp){
        String value="";
        switch (exp.type){
            case IDExp:
                value= exp.value;
                break;
            case StringExp:
                value=exp.quoteValueOneLine;
                break;
            case BracketExp:
                value= "尚未实现好！";
                break;
        }
        return value;
    }
    static void circle(final String root,final String target,final String root_pkg,File folder){
        File[] cs=folder.listFiles();
        for(File file:cs){
            if (file.isDirectory()){
                circle(root,target,root_pkg,file);
            }else{
                String suffix=".s2java";
                if (file.getName().endsWith(suffix)){
                    try {
                        String txt=mb.Util.readTxt(file,"\n","UTF-8");
                        Node<Token> tokens=Token.run(txt);
                        Node<Exp> exps=Exp.parse(tokens);
                        if (exps==null){
                            log("文件无内容！"+file.getAbsolutePath());
                        }else{
                            Node<Exp> tmp=exps;
                            List<String> list=new ArrayList<String>();
                            String file_path=file.getAbsolutePath();
                            String fileName=file.getName();
                            String className=fileName.substring(0,fileName.length()-suffix.length());//类名
                            String relative_name=file_path.substring(root.length(),file_path.length()-suffix.length());//根路径到类全名
                            String pkgName="";
                            if(relative_name.equals(className)){
                                pkgName=root_pkg;
                            }else{
                                pkgName=root_pkg+"."+relative_name.replace(File.separatorChar,'.');
                                pkgName=pkgName.substring(0,pkgName.length()-className.length());//包名
                            }
                            list.add("package "+pkgName+";");
                            while (tmp!=null){
                                Exp exp=tmp.first;
                                tmp=tmp.rest;
                                if (tmp!=null){
                                    //导入
                                }else{
                                    //最后一个，作为导出的对象
                                    if (exp.type==ExpType.BracketExp){
                                        if (exp.children==null){
                                            throw exp.exception("不允许空的()");
                                        }else {
                                            if (exp.children.first.type!=ExpType.IDExp){
                                                throw exp.children.first.exception("目前仅支持ID类型");
                                            }else{
                                                Exp type=exp.children.first;
                                                if (type.value.equals("class")){
                                                    list.add("public abstract class "+className+" {");
                                                    Node<Exp> args=exp.children.rest;
                                                    if (args.length%2!=0){
                                                        throw type.exception("需要偶数个元素");
                                                    }else{
                                                        Set<String> keys=new TreeSet<String>();
                                                        while (args!=null){
                                                            Exp key_exp=args.first;
                                                            args=args.rest;
                                                            Exp value_exp=args.first;
                                                            args=args.rest;
                                                            if (key_exp.type!=ExpType.IDExp){
                                                                throw key_exp.exception("不是合法的ID类型");
                                                            }else{
                                                                if (keys.contains(key_exp.value)){
                                                                    throw key_exp.exception("该key已经被定义过了，禁止重复定义");
                                                                }else{
                                                                    keys.add(key_exp.value);
                                                                    switch (value_exp.type){
                                                                        case IDExp:
                                                                            /*普通字段*/
                                                                            list.add(value_exp.value+" "+key_exp.value+";");
                                                                            break;
                                                                        case StringExp:
                                                                            throw value_exp.exception("目前尚不支持字符串");
                                                                        case BracketExp:
                                                                            if (value_exp.children==null){
                                                                                throw value_exp.exception("禁止空()");
                                                                            }else{
                                                                                Exp first=value_exp.children.first;
                                                                                if (first.type==ExpType.IDExp){
                                                                                    /**
                                                                                     声明的都是public的，内联的是lambda的？再用关键字吧
                                                                                     */
                                                                                    Node<Exp> list_args=value_exp.children.rest;
                                                                                    if ("fn".equals(first.value)){
                                                                                        /**
                                                                                         抽象函数
                                                                                         */
                                                                                        list.add("public abstract "+buildFunctionType(key_exp,list_args)+";");
                                                                                    }else if("un".equals(first.value)){
                                                                                        /**
                                                                                         联合字段
                                                                                         目前连空与非空都不能判断
                                                                                         */

                                                                                    }else if("default".equals(first.value)){
                                                                                        /*
                                                                                        默认值、字段、函数
                                                                                        (default String a)
                                                                                        (default (fn a b c d e) () () ())
                                                                                         */
                                                                                        if (list_args==null || list_args.length<2){
                                                                                            throw value_exp.exception("需要至少两个参数");
                                                                                        }else {
                                                                                            Exp exp_type = list_args.first;
                                                                                            list_args=list_args.rest;
                                                                                            switch (exp_type.type) {
                                                                                                case BracketExp:
                                                                                                    break;
                                                                                                case StringExp:
                                                                                                    throw exp.exception("尚不支持字符串类型");
                                                                                                case IDExp:
                                                                                                /*
                                                                                                字段类型
                                                                                                字段的默认值可能是计算出来的。
                                                                                                * */
                                                                                                    if (list_args.length != 2) {
                                                                                                        Exp value_part=list_args.first;
                                                                                                        if(value_part.type==ExpType.StringExp){
                                                                                                            if (!"String".equals(exp_type.value)){
                                                                                                                throw value_part.exception("字符串只能初始化字符串类型");
                                                                                                            }
                                                                                                        }
                                                                                                        list.add("public " + exp_type.value + " " + key_exp.value + "=" +buildReadExp(value_part)+";");
                                                                                                    } else {
                                                                                                        throw value_exp.exception("此时仅需要两个参数");
                                                                                                    }
                                                                                                    break;
                                                                                            }
                                                                                        }
                                                                                    }else if("static".equals(first.value)){
                                                                                        /**
                                                                                         静态方法、字段
                                                                                         */
                                                                                    }else{
                                                                                        throw first.exception("目前不支持的类型");
                                                                                    }
                                                                                }else{
                                                                                    throw first.exception("目前只支持ID类型");
                                                                                }
                                                                            }
                                                                            break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    list.add("}");
                                                }else{
                                                    throw type.exception("不是目前支持的符号");
                                                }
                                            }
                                        }
                                    }else{
                                        throw exp.exception("尚不支持的导出类型");
                                    }
                                }
                            }
                            mb.Util.saveTxt(target+relative_name+".java",joinList(list,"\n"),"UTF-8");
                        }
                    } catch (IOException e) {
                        log("读取文件出错！："+file.getAbsolutePath());
                        e.printStackTrace();
                    } catch (RangePathsException e) {
                        log("解析出错："+file.getAbsolutePath());
                        e.printStackTrace();
                    }
                }else{
                    log("不是s2java文件"+file.getAbsolutePath());
                }
            }
        }
    }
    public static void run(String folder_path,String target_path,String pkg_name) throws Exception {
        File folder=new File(folder_path);
        if (folder.exists() && folder.isDirectory()){
            circle(folder_path,target_path,pkg_name,folder);
        }else{
            throw new Exception(folder_path+":不是有效目录！");
        }
    }

    public static void main(String[] args){
        try {
            run(
                    "D:/usr/web/app/S-Lisp/JAVA/s/src/main/java/meta2/s2java/src/",
                    "D:/usr/web/app/S-Lisp/JAVA/s/src/main/java/meta2/s2java/bin/",
                    "meta2.s2java.bin"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
