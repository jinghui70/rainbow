package rainbow.core.util.converter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectMaker<T> implements DataMaker<T> {

	private static Logger logger = LoggerFactory.getLogger(ObjectMaker.class);

	private Class<T> clazz;

	private Map<String, PropertyDescriptor> map;

	public ObjectMaker(Class<T> clazz) {
		this.clazz = clazz;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
			map = Arrays.stream(beanInfo.getPropertyDescriptors()).filter(p -> p.getWriteMethod() != null)
					.collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity()));
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T makeInstance() {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			logger.error("create instance of {} failed", clazz.getName(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setValue(T object, String key, Object value) {
		PropertyDescriptor p = map.get(key);
		if (p != null && p.getWriteMethod() != null) {
			value = Converters.convert(value, p.getPropertyType());
			try {
				p.getWriteMethod().invoke(object, value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error("set value {} to {}.{} failed", value, clazz.getSimpleName(), key, e);
				throw new RuntimeException(e);
			}
		}
	}

}
