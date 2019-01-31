package st3.function;

import s.Node;
import st3.Function;

/**
 * 应用特定宏
 * 宏，后续参数
 */
public class Apply extends Function {
    @Override
    public Object run(Node<Object> args) throws Throwable {
        if (args==null ||args.Length()>2){
            throw new Exception("需要1~2个参数");
        }else{
            Object fun=args.First();
            if (fun instanceof Function){
                args=args.Rest();
                Object the_args=null;
                if (args!=null) {
                    the_args = args.First();
                    if (!(the_args instanceof Node)){
                        throw new Exception("参数3应该是列表类型");
                    }
                }
                return ((Function) fun).run((Node<Object>)the_args);
            }else{
                throw new Exception("参数2不是合法的函数");
            }
        }
    }
}
