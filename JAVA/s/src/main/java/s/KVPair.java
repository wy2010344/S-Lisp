package s;

public class KVPair<K,V> {
    public KVPair(K k,V v){
        this.k=k;
        this.v=v;
    }
    private K k;
    private V v;
    public K getKey(){
        return k;
    }
    public V getValue(){
        return v;
    }
}
