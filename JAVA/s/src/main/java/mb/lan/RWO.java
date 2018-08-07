package mb.lan;

import java.util.ArrayList;
import java.util.List;

/***
 * 监听值改变
 * @author miki
 *
 * @param <T>
 */
public class RWO<T> extends RW<T> {
	public RWO(T v) {
		super(v);
		// TODO Auto-generated constructor stub
	}
	public void set(T v){
		super.set(v);
		//改变通知
		for(RWOListener<T> l:listeners){
			l.notify(v);
		}
	}
	
	protected List<RWOListener<T>> listeners=new ArrayList<RWOListener<T>>();
	public void bind(RWOListener<T> l){
		if(this.listeners.indexOf(l)>-1){
			return;
		}else{
			this.listeners.add(l);
			//惰性通知
			l.notify(this.get());
		}
	}
	public boolean unbind(RWOListener<T> l){
		return this.listeners.remove(l);
	}
}
