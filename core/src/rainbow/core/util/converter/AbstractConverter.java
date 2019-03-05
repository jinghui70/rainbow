package rainbow.core.util.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractConverter<F, T> implements Converter<F, T> {

	protected Class<?> fromClass;
	protected Class<?> toClass;

	protected AbstractConverter() {
		ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
		Type[] types = pt.getActualTypeArguments();
		fromClass = (Class<?>) types[0];
		toClass = (Class<?>) types[1];
	}

	public Class<?> getFromClass() {
		return fromClass;
	}

	public Class<?> getToClass() {
		return toClass;
	}

}
