package st3.function;

import s.Node;
import st3.Function;

public class KvsFind1st extends Function {
    @Override
    public Object run(Node<Object> args) throws Exception {
        if (args==null || args.Length()!=2){
            throw new Exception("需要两个参数");
        }else {
            Object kvs= args.First();
            if (kvs instanceof Node){
                args=args.Rest();
                Object key=args.First();
                if (key instanceof String){
                    return Node.kvs_find1st((Node)kvs,(String)key);
                }else{
                    throw new Exception("参数2的结果必须是字符串");
                }
            }else{
                throw new Exception("参数1的结果必须是kvs链表");
            }
        }
    }
}
