package meta;

import mb.RangePathsException;

public class BracketExp extends Exp {
    public final Token left;
    public final Token right;
    public final Node<Exp> children;

    /*后续参数的倒置*/
    public final Node<Exp> r_children;

    public BracketExp(Token left,Node<Exp> children, Token right){
        this.left=left;
        this.children=children;
        this.r_children=Node.reverse(children);
        this.right=right;
    }
    @Override
    public RangePathsException exception(String msg) {
        return new RangePathsException(left.begin,right.begin+right.value.length(),toString()+":"+msg);
    }

    public void toString(StringBuilder sb){
        sb.append("(");
        for (Node<Exp> tmp=children;tmp!=null;tmp=tmp.rest){
            tmp.first.toString(sb);
            sb.append(" ");
        }
        sb.append(")");
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        toString(sb);
        return sb.toString();
    }
}
