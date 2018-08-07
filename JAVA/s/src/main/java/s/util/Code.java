package s.util;

import s.LocationException;

public class Code {
    private final String code;
    private final int maxLength;
    private final char split;
    public Code(String code,char linesplit){
    	this.code=code;
        maxLength=code.length()-1;
        split=linesplit;
        shift();
    }
    private int index=-1;
    private int row=0,col=0;
    private Character c;
    public void shift(){
        if(index<maxLength){
            index++;
            c=code.charAt(index);
            if(c==split){
                col=0;
                row++;
            }else{
                col++;
            }
        }else{
            c=null;
        }
    }
    public Character current(){
        return c;
    }
    public Location currentLoc(){
        return new Location(row,col,index);
    }
    public LocationException msgThrow(){
        return msgThrow(null);
    }
    public LocationException msgThrow(Character w){
        Character c=current();
        StringBuilder msg = new StringBuilder();;
        if(w!=null){
            msg.append("期待字符{").append(w).append("},但得到");
        }
        if(c!=null){ 
            msg.append("字符{").append(c).append("}在位置").append(currentLoc().toString());
        }else{
            msg.append("文章结尾");
        }
        return new LocationException(msg.toString(),currentLoc());
    }
}
