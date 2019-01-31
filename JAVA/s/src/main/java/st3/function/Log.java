package st3.function;

import s.Node;
import st3.Function;

public class Log extends Function {
    @Override
    public Object run(Node<Object> args) throws Throwable {
        while (args!=null){
            if (args.First()==null){
                System.out.print("[]");
            }else{
                System.out.print(args.First().toString());
            }
            System.out.print(" ");
            args=args.Rest();
        }
        System.out.println();
        return null;
    }
}
