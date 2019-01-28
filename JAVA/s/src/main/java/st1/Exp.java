package st1;

import mb.RangePathsException;

public class Exp {

    public RangePathsException exception(String msg){
        return new RangePathsException(0,0,msg);
    }
}
