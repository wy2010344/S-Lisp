package st3;

public class Node<T> {
    private Node(T v, Node<T> n) {
        this.first=v;
        this.rest=n;
        if(n!=null) {
            this.length=n.length+1;
        }else {
            this.length=1;
        }
    }
    public final int length;
    public final T first;
    public final Node<T> rest;

    public static <T> Node<T> extend(T v, Node<T> vs) {
        return new Node(v,vs);
    }
    public static <T> Node<T> reverse(Node<T> vs) {
        Node<T> rs=null;
        while (vs!=null){
            rs=extend(vs.first,rs);
            vs=vs.rest;
        }
        return rs;
    }
    public static Node<Object> list(Object ...ids) {
        Node<Object> r=null;
        for(int i=ids.length-1;i>-1;i--){
            r=Node.extend(ids[i],r);
        }
        return r;
    }
}
