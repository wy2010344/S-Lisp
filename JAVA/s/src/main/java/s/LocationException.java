package s;

import s.util.Location;

public class LocationException extends Exception {
	public LocationException(String msg,Location loc) {
		super(msg);
		this.loc=loc;
		this.path="";
	}
	public void setFile(String path) {
		this.path=path;
	}
	String path;
	Location loc;
	public Location Loc() {
		return loc;
	}
	@Override
	public String toString() {
		return path+":"+this.getMessage()+",在"+loc.toString();
	}
}