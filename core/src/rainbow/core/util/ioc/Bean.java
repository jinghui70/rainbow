package rainbow.core.util.ioc;

/**
 * 这个对象封装了一个IOC Bean 的配置定义
 * 
 * @author lijinghui
 * @see Context
 */
public final class Bean {

	/**
	 * Bean 的类型
	 */
	private Class<?> clazz;

	/**
	 * 是否是原型Bean
	 */
	boolean prototype;

	/**
	 * 如果是单例Bean，这就是保存的单例对象
	 */
	private volatile Object object;

	/**
	 * 工厂模式下，这是生产用的bean类
	 */
	private Class<?> targetClass;

	/**
	 * 工厂模式下的单例Bean对象
	 */
	private volatile Object targetObject;

	private Bean() {
	}

	public boolean isFactory() {
		return Factory.class.isAssignableFrom(clazz);
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

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	public Object getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
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
	 * @param clazz bean类型
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
	 * @param object 对象
	 * @param clazz  超类
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
	 * @param object 对象
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
