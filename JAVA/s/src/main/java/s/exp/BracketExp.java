package s.exp;

import s.Exp;
import s.Node;
import s.Token;
import s.IndexException;

import java.util.ArrayList;
import java.util.List;

public abstract class BracketExp extends Exp {
    Token left;
    private Node<Exp> children;
    private Node<Exp> r_children;
    Token right;
    public BracketExp(Token left, Node<Exp> children, Token right,Node<Exp> r_children){
        this.left=left;
        this.children=children;
        this.right=right;
        this.r_children=r_children;
    }

    public Token getLeft(){
        return left;
    }

    public Token getRight() {
        return right;
    }

    public Node<Exp> getChildren() {
        return children;
    }
    public Node<Exp> getR_children() {
        return r_children;
    }

    protected void buildStringBody(StringBuilder sb){
        Node<Exp> tmp=children;
        while (tmp!=null){
            sb.append(" ");
            tmp.First().buildString(sb);
            tmp=tmp.Rest();
        }
    }
    @Override
    public void buildString(StringBuilder sb){
        sb.append(left.getContent());
        buildStringBody(sb);
        if (right!=null){
            sb.append(right.getContent());
        }
    }

    @Override
    public IndexException exception(String msg) {
        return new IndexException(
                left.getBegin(),
                right.getBegin()+right.getContent().length(),
                "("+left.getBegin()+":"+right.getBegin()+")"+toString()+"(=>"+msg
        );
    }

    @Override
    public void warn(String msg) {
        System.out.println(
                "("+left.getBegin()+":"+ right.getBegin()+right.getContent().length()+")"+
                        toString()+"(=>"+msg
        );
    }
}
