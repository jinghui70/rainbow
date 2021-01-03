package rainbow.core.util.ioc;

public interface Factory<T> {

	T create();

	Class<T> targetClass();

}
