package mb.lan;

/**
 * 只读
 * @author miki
 *
 * @param <T>
 */
public class RO<T>{
	public RO(T v){
		this.value=v;
	}
	protected T value;
	public T get(){
		return this.value;
	}
}
