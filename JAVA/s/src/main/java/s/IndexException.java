package s;

import s.exp.CallExp;

public class IndexException extends Exception {
    public IndexException(int begin,int end,String message){
        super(message);
        this.begin=begin;
        this.end=end;
    }
    private int begin;
    private int end;
    public int getBegin(){return begin;}
    public int getEnd(){return end;}
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        Node<Stack> tmp=stacks;
        while (tmp!=null){
            CallExp exp=tmp.First().exp;
            sb.append(tmp.First().path).append("(")
                    .append(exp.getLeft().getBegin())
                    .append(":")
                    .append(exp.getRight().getBegin())
                    .append(")=>")
                    .append(exp.toString())
                    .append("\r\n");
        }
        sb.append("("+begin+":"+end+")"+super.toString());
        return sb.toString();
    }

    class Stack{
        public String path;
        public CallExp exp;
    }
    Node<Stack> stacks=null;
    public void addStack(String path, CallExp exp) {
        stacks=Node.extend(new Stack(),null);
        stacks.First().path=path;
        stacks.First().exp=exp;
    }
}
