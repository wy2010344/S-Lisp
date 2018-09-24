package s;

public class Location{
    public Location(int r,int c,int index){
        this._r=r;
        this._c=c;
        this._i=index;
    }
    private int _r,_c;
    public int row(){
        return _r;
    }
    public int column(){
        return _c;
    }
    private int _i;
    public int index() {
    	return _i;
    }
    
    private int _l;
    public void setLength(int l) {
    	_l=l;
    }
    public int length() {
    	return _l;
    }
    @Override
    public String toString(){
        return "{第"+(_r+1)+"行，第"+(_c+1)+"列}";
    }
}