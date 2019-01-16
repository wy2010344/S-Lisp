package mb;

import mb.RangeException;
import s.Node;
import s.exp.CallExp;

public class RangePathsException extends RangeException {
    public RangePathsException(int begin, int end, String msg) {
        super(begin, end, msg);
    }
    public RangePathsException(RangeException r){
        super(r.begin,r.end,r.getMessage());
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        Node<Stack> tmp=stacks;
        while (tmp!=null){
            Stack stack=tmp.First();
            sb.append(tmp.First().path).append("(")
                    .append(stack.begin)
                    .append(":")
                    .append(stack.end)
                    .append(")=>")
                    .append(stack.exp)
                    .append("\r\n");
        }
        sb.append("("+begin+":"+end+")"+super.toString());
        return sb.toString();
    }

    class Stack{
        public String path;
        public int begin;
        public int end;
        public String exp;
    }
    Node<Stack> stacks=null;
    public void addStack(String path, int begin,int end,String exp) {
        Stack stack=new Stack();
        stack.path=path;
        stack.begin=begin;
        stack.end=end;
        stack.exp=exp;
        stacks=Node.extend(stack,stacks);
    }
}
