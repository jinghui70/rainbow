package rainbow.db.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.util.converter.Converters;

public class ObjectRowMapper<T> extends AbstractRowMapper<T> {

	private static Logger logger = LoggerFactory.getLogger(ObjectRowMapper.class);

	private Class<T> clazz;

	private Map<String, PropertyDescriptor> map;

	public ObjectRowMapper(List<SelectField> fields, Class<T> clazz) {
		super(fields);
		this.clazz = clazz;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
			map = Arrays.stream(beanInfo.getPropertyDescriptors()).filter(p -> p.getWriteMethod() != null)
					.collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity()));
		} catch (IntrospectionException e) {
		}
	}

	@Override
	protected T makeInstance() {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void setValue(T object, String key, Object value) {
		PropertyDescriptor p = map.get(key);
		if (p != null) {
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
