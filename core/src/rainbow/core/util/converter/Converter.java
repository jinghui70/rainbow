package rainbow.core.util.converter;

public interface Converter<F, T> {

	T convert(F from, Class<?> toClass);

}
