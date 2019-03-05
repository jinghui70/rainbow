package rainbow.core.util;

public abstract class Consumer<T> {

	public boolean accept(T t) {
		return true;
	}
	
	public abstract void consume(T t);
	
}
