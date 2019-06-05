package rainbow.core.util.converter;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import rainbow.core.util.converter.impl.Boolean2Number;
import rainbow.core.util.converter.impl.Date2Long;
import rainbow.core.util.converter.impl.Date2SqlDate;
import rainbow.core.util.converter.impl.Date2Timestamp;
import rainbow.core.util.converter.impl.Enum2Number;
import rainbow.core.util.converter.impl.Enum2String;
import rainbow.core.util.converter.impl.LocalDate2SqlDate;
import rainbow.core.util.converter.impl.LocalDate2Timestamp;
import rainbow.core.util.converter.impl.LocalDateTime2Timestamp;
import rainbow.core.util.converter.impl.Number2BigDecimal;
import rainbow.core.util.converter.impl.Number2Boolean;
import rainbow.core.util.converter.impl.Number2Date;
import rainbow.core.util.converter.impl.Number2Double;
import rainbow.core.util.converter.impl.Number2Enum;
import rainbow.core.util.converter.impl.Number2Int;
import rainbow.core.util.converter.impl.Number2Long;
import rainbow.core.util.converter.impl.Number2Short;
import rainbow.core.util.converter.impl.Object2String;
import rainbow.core.util.converter.impl.SqlDate2LocalDate;
import rainbow.core.util.converter.impl.String2BigDecimal;
import rainbow.core.util.converter.impl.String2Boolean;
import rainbow.core.util.converter.impl.String2Date;
import rainbow.core.util.converter.impl.String2Double;
import rainbow.core.util.converter.impl.String2Enum;
import rainbow.core.util.converter.impl.String2Int;
import rainbow.core.util.converter.impl.String2LocalDate;
import rainbow.core.util.converter.impl.String2LocalDateTime;
import rainbow.core.util.converter.impl.String2Long;
import rainbow.core.util.converter.impl.String2Short;
import rainbow.core.util.converter.impl.String2SqlDate;
import rainbow.core.util.converter.impl.String2Timestamp;
import rainbow.core.util.converter.impl.Timestamp2Date;
import rainbow.core.util.converter.impl.Timestamp2LocalDate;
import rainbow.core.util.converter.impl.Timestamp2LocalDateTime;

public class Converters {

	private static Table<Class<?>, Class<?>, Converter<?, ?>> table = HashBasedTable.create();

	@SuppressWarnings("unchecked")
	public static <F, T> T convert(F from, Class<T> toClass) {
		if (from == null) {
			if (toClass.isPrimitive()) {
				if (toClass == int.class)
					return (T) Integer.valueOf(0);
				else if (toClass == long.class)
					return (T) Long.valueOf(0L);
				else if (toClass == short.class)
					return (T) Short.valueOf((short) 0);
				else if (toClass == byte.class)
					return (T) Byte.valueOf((byte) 0);
				else if (toClass == float.class)
					return (T) Float.valueOf(.0f);
				else if (toClass == double.class)
					return (T) Double.valueOf(.0);
				else if (toClass == boolean.class)
					return (T) Boolean.FALSE;
				else if (toClass == char.class)
					return (T) Character.valueOf(' ');
			}
			return null;
		}
		Class<?> fromClass = from.getClass();
		if (fromClass == toClass || toClass.isAssignableFrom(fromClass))
			return (T) from;

		Class<?> toClass2 = toClass;
		// 处理Primitive类型
		if (toClass.isPrimitive()) {
			if (toClass == int.class)
				toClass2 = Integer.class;
			else if (toClass == long.class)
				toClass2 = Long.class;
			else if (toClass == short.class)
				toClass2 = Short.class;
			else if (toClass == byte.class)
				toClass2 = Byte.class;
			else if (toClass == double.class)
				toClass2 = Double.class;
			else if (toClass == float.class)
				toClass2 = Float.class;
			else if (toClass == boolean.class)
				toClass2 = Boolean.class;
			else if (toClass == char.class)
				toClass2 = Character.class;
		}
		// 处理 Enum
		if (toClass.isEnum()) {
			toClass2 = Enum.class;
		}
		if (fromClass.isEnum()) {
			fromClass = Enum.class;
		}
		Converter<F, T> c = (Converter<F, T>) getConverter(fromClass, toClass2);
		if (c != null)
			return c.convert(from, toClass);

		if (Number.class.isAssignableFrom(toClass)) {
			Converter<Number, T> c2 = (Converter<Number, T>) table.get(Number.class, toClass2);
			if (c2 != null) {
				Converter<F, Number> c1 = (Converter<F, Number>) getConverter(fromClass, Number.class);
				if (c1 != null) {
					Number n = c1.convert(from, Number.class);
					return c2.convert(n, toClass);
				}
			}
		}
		throw new ConvertException(fromClass, toClass);
	}

	private static Converter<?, ?> getConverter(Class<?> fromClass, Class<?> toClass) {
		Converter<?, ?> c = null;
		Map<Class<?>, Converter<?, ?>> map = table.column(toClass);
		if (!map.isEmpty()) {
			Class<?> fromClass2 = fromClass;
			while (c == null && fromClass2 != null) {
				c = map.get(fromClass2);
				fromClass2 = fromClass2.getSuperclass();
			}
		}
		return c;
	}

	private static void addDefault(Class<? extends AbstractConverter<?, ?>> convertClass) {
		AbstractConverter<?, ?> c;
		try {
			c = convertClass.newInstance();
			table.put(c.getFromClass(), c.getToClass(), c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		addDefault(Boolean2Number.class);
		addDefault(Date2Long.class);
		addDefault(Date2SqlDate.class);
		addDefault(Date2Timestamp.class);
		addDefault(Enum2Number.class);
		addDefault(Enum2String.class);
		addDefault(LocalDate2SqlDate.class);
		addDefault(LocalDate2Timestamp.class);
		addDefault(LocalDateTime2Timestamp.class);
		addDefault(Number2BigDecimal.class);
		addDefault(Number2Boolean.class);
		addDefault(Number2Date.class);
		addDefault(Number2Enum.class);
		addDefault(Number2Double.class);
		addDefault(Number2Int.class);
		addDefault(Number2Long.class);
		addDefault(Number2Short.class);
		addDefault(Object2String.class);
		addDefault(SqlDate2LocalDate.class);
		addDefault(String2BigDecimal.class);
		addDefault(String2Boolean.class);
		addDefault(String2Date.class);
		addDefault(String2Double.class);
		addDefault(String2Enum.class);
		addDefault(String2Int.class);
		addDefault(String2Long.class);
		addDefault(String2Short.class);
		addDefault(String2SqlDate.class);
		addDefault(String2Timestamp.class);
		addDefault(String2LocalDate.class);
		addDefault(String2LocalDateTime.class);
		addDefault(Timestamp2Date.class);
		addDefault(Timestamp2LocalDate.class);
		addDefault(Timestamp2LocalDateTime.class);
	}

	/**
	 * 转换一个JavaBean为Map
	 * 
	 * @param object
	 * @return
	 */
	public static <T> Map<String, Object> object2Map(T object) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Object.class);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				String key = pd.getName();
				Method getter = pd.getReadMethod();
				Object value = getter.invoke(object);
				result.put(key, value);
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 转换一个Map为JavaBean
	 * 
	 * @param map
	 * @param clazz
	 * @return
	 */
	public static <T> T map2Object(Map<String, Object> map, Class<T> clazz) {
		try {
			T object = clazz.newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				String key = pd.getName();
				Method setter = pd.getWriteMethod();
				Object value = map.get(key);
				if (value != null)
					value = Converters.convert(value, pd.getPropertyType());
				setter.invoke(object, value);
			}
			return object;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 转换一个Map为JavaBean
	 * 
	 * @param map
	 * @param clazz
	 * @return
	 */
	public static <T> T map2Object(Map<String, Object> map, BeanInfo beanInfo, Class<T> clazz) {
		try {
			T object = clazz.newInstance();
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				String key = pd.getName();
				Method setter = pd.getWriteMethod();
				Object value = map.get(key);
				if (value != null)
					value = Converters.convert(value, pd.getPropertyType());
				setter.invoke(object, value);
			}
			return object;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
