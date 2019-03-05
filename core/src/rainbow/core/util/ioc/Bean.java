package rainbow.core.util.ioc;

/**
 * 这个对象封装了一个IOC Bean 的配置定义
 * 
 * @author lijinghui
 * @see Context
 */
public final class Bean {

	private Class<?> clazz;

	private Object object;

	boolean prototype;

	private Bean() {
	}

	/**
	 * 返回bean的类型
	 * 
	 * @return bean的类型
	 */
	public Class<?> getClazz() {
		return clazz;
	}

	/**
	 * 返回bean的实例
	 * 
	 * @return 如果是原型bean或者实例没有创建则返回null
	 */
	public Object getObject() {
		return object;
	}

	void setObject(Object object) {
		this.object = object;
	}

	/**
	 * 返回bean是否是原型bean，原型bean在每次获取时都创建一个新的实例
	 * 
	 * @return 是否是原型bean
	 */
	public boolean isPrototype() {
		return prototype;
	}

	/**
	 * 创建一个原型bean定义
	 * 
	 * @param clazz
	 *            bean类型
	 * @return 创建的原型Bean定义
	 */
	public static Bean prototype(Class<?> clazz) {
		Bean bean = new Bean();
		bean.clazz = clazz;
		bean.prototype = true;
		return bean;
	}

	/**
	 * 创建一个单例bean定义
	 * 
	 * @param clazz
	 * @return
	 */
	public static Bean singleton(Class<?> clazz) {
		Bean bean = new Bean();
		bean.clazz = clazz;
		bean.prototype = false;
		return bean;
	}

	/**
	 * 创建一个单例Bean
	 * 
	 * @param object
	 *            对象
	 * @param clazz
	 *            超类
	 * @return
	 */
	public static <T> Bean singleton(T object, Class<T> clazz) {
		Bean bean = new Bean();
		bean.clazz = clazz;
		bean.prototype = false;
		bean.object = object;
		return bean;
	}

	/**
	 * 创建一个单例Bean
	 * 
	 * @param object
	 *            对象
	 * @return
	 */
	public static <T> Bean singleton(T object) {
		Bean bean = new Bean();
		bean.clazz = object.getClass();
		bean.prototype = false;
		bean.object = object;
		return bean;
	}
}
