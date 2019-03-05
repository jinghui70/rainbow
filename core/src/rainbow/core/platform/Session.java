package rainbow.core.platform;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用服务前，由环境设置的线程上下文
 * 
 * @author lijinghui
 * 
 */
public final class Session {

	private static ThreadLocal<Map<String, Object>> session = new ThreadLocal<Map<String, Object>>();

	/**
	 * 设置上下文内容
	 * 
	 * @param sessionValue
	 */
	public static void set(Map<String, Object> sessionValue) {
		session.set(sessionValue);
	}

	public static void set(String key, Object value) {
		Map<String, Object> map = session.get();
		if (map == null) {
			map = new HashMap<String, Object>();
			session.set(map);
		}
		map.put(key, value);
	}

	/**
	 * 清除上下文内容
	 */
	public static void clear() {
		session.remove();
	}

	/**
	 * 获取一个项目值,如果获取不到，则抛出异常
	 * 
	 * @param key
	 * @return
	 * @throws SessionException
	 */
	public static Object getValue(String key) throws SessionException {
		Map<String, Object> map = session.get();
		if (map == null)
			throw new SessionException();
		Object value = map.get(key);
		if (value == null)
			throw new SessionException(key, "not found");
		return value;
	}

	/**
	 * 返回一个项目的整数值，如果存的是字符串，则尝试转换为整数
	 * 
	 * @param key
	 * @return
	 * @throws SessionException
	 */
	public static int getInt(String key) throws SessionException {
		Object value = getValue(key);
		if (value instanceof Integer)
			return (Integer) value;
		if (value instanceof String) {
			try {
				return Integer.parseInt((String) value);
			} catch (NumberFormatException e) {
			}
		}
		throw new SessionException(key, "is not an integer");
	}

	/**
	 * 返回一个项目值的字符串
	 * 
	 * @param key
	 * @return
	 * @throws SessionException
	 */
	public static String getString(String key) throws SessionException {
		return getValue(key).toString();
	}

	/**
	 * 返回上下文中的所有信息
	 * 
	 * @return
	 */
	public static Map<String, Object> getAll() {
		return session.get();
	}

	/**
	 * 检查上下文中是否有指定的项目
	 * 
	 * @param key
	 * @return
	 */
	public static boolean hasValue(String key) {
		Map<String, Object> map = session.get();
		if (map == null)
			return false;
		return map.containsKey(key);
	}
}
