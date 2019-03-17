package rainbow.db.dao;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.util.Utils;

public class ClassInfo<T> {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Class<T> clazz;

	private Map<String, Property> properties = new HashMap<String, Property>();

	public ClassInfo(Class<T> clazz) {
		this.clazz = clazz;
		properties = getPropertyMap(clazz);
	}

	private Map<String, Property> getPropertyMap(Class<?> clazz) {
		Map<String, Property> result = new HashMap<String, Property>();
		Method[] methods = clazz.getMethods();
		Method writeMethod = null;
		for (Method method : methods) {
			if (method.getParameterTypes().length != 0)
				continue;
			Class<?> type = method.getReturnType();
			// 必须有返回类型
			if (type == null)
				continue;
			String propertyName = method.getName();
			if (propertyName.startsWith("get")) {
				propertyName = propertyName.substring(3, propertyName.length());
			} else if (propertyName.startsWith("is") && (type == Boolean.class || type == boolean.class)) {
				propertyName = propertyName.substring(2, propertyName.length());
			} else
				continue;
			try {
				writeMethod = clazz.getMethod("set" + propertyName, type);
			} catch (Exception e) {
				if (!type.isArray())
					continue;
			}
			propertyName = Utils.lowerFirstChar(propertyName);
			Property p = new Property(type);
			p.setReadMethod(method);
			p.setWriteMethod(writeMethod);
			if (type.isArray())
				p.setSubType(type.getComponentType());
			result.put(propertyName, p);
		}
		return result;
	}

	public T makeInstance() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public Property getProperty(String name) {
		Property result = properties.get(name);
		if (result != null)
			return result;
		String[] part = Utils.split(name, '.');
		if (part.length != 2)
			return null;
		result = properties.get(part[0]);
		if (result == null)
			return null;
		if (result.hasSubType()) {
			try {
				int index = Integer.valueOf(part[1]);
				result = new Property(result, index);
				properties.put(name, result);
				return result;
			} catch (NumberFormatException e) {
				logger.error("bad property name [{}]", name);
				return null;
			}
		} else {
			Map<String, Property> subMap = getPropertyMap(result.getType());
			if (subMap.containsKey(part[1])) {
				for (Entry<String, Property> entry : subMap.entrySet()) {
					String key = part[0] + '.' + entry.getKey();
					Property p = entry.getValue();
					p.setParent(result);
					properties.put(key, p);
				}
			}
			return properties.get(name);
		}
	}

}