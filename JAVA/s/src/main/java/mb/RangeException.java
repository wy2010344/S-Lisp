package mb;

public class RangeException extends Exception{
    public RangeException(int begin,int end,String msg){
        super(msg);
        this.begin=begin;
        this.end=end;
    }
    public final int begin;
    public final int end;
}
