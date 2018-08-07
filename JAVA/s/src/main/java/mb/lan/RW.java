package mb.lan;

/**
 * 可读写，去=
 * @author miki
 *
 * @param <T>
 */
public class RW<T> extends RO<T>{
	public RW(T v){
		super(v);
	}
	public void set(T value) {
		this.value = value;
	}
}
