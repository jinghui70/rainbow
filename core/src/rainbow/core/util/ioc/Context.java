package rainbow.core.util.ioc;

import static rainbow.core.util.Preconditions.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.util.Utils;

/**
 * 这是一个简单实现的IOC容器类，IOC配置在名为beans的Map中。注入的顺序是先匹配参数，再匹配Context中配置的Bean
 * 
 * @see Bean
 * @author lijinghui
 * 
 */
public class Context {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, Bean> beans;

	private List<Context> parents;

	public Context() {
		this.beans = new HashMap<String, Bean>();
		this.parents = Collections.emptyList();
	}

	/**
	 * 构造函数
	 * 
	 * @param beans IOC配置信息
	 */
	public Context(Map<String, Bean> beans) {
		this.beans = beans;
		this.parents = Collections.emptyList();
	}

	public Context(Map<String, Bean> beans, List<Context> parents) {
		this.beans = beans;
		this.parents = parents;
	}

	public Context addBean(String name, Bean bean) {
		checkArgument(!beans.containsKey(name), "can't add bean {}, already exist", name);
		beans.put(name, bean);
		return this;
	}

	public Context addBean(String name, Class<?> clazz) {
		return addBean(name, Bean.singleton(clazz));
	}

	public Context addBean(Class<?> clazz) {
		String name = clazz.getSimpleName();
		if (name.endsWith("Impl")) {
			name = Utils.substringBefore(name, "Impl");
		}
		name = Utils.lowerFirstChar(name);
		return addBean(name, clazz);
	}

	/**
	 * 加载所有的单例Bean
	 */
	public void loadAll() {
		beans.entrySet().forEach(entry -> {
			Bean bean = entry.getValue();
			if (!bean.isPrototype())
				getSingletonBean(entry.getKey(), bean);
		});
	}

	/**
	 * 返回指定id的bean定义
	 * 
	 * @param id
	 * @return bean定义对象
	 * @throws NoSuchBeanDefinitionException
	 */
	private Bean getBeanDef(String id) throws NoSuchBeanDefinitionException {
		Bean bean = beans.get(id);
		if (bean != null)
			return bean;
		throw new NoSuchBeanDefinitionException(id);
	}

	/**
	 * 返回指定id和类型的bean定义
	 * 
	 * @param id
	 * @param clazz
	 * @return bean定义对象
	 * @throws BeanNotOfRequiredTypeException
	 */
	private Bean getBeanDef(String id, Class<?> clazz) throws BeanNotOfRequiredTypeException {
		Bean bean = getBeanDef(id);
		if (bean == null)
			throw new NoSuchBeanDefinitionException(id);
		if (clazz.isAssignableFrom(bean.getClazz()))
			return bean;
		throw new BeanNotOfRequiredTypeException(id, clazz, bean.getClazz());
	}

	/**
	 * 只在自己的容器里找，返回指定名字的Bean对象
	 * 
	 * @param id
	 * @return
	 */
	public Object getLocalBean(String id) {
		Bean bean = getBeanDef(id);
		return getBean(id, bean);
	}

	/**
	 * 只在自己的容器里找，返回容器定义的指定的Bean
	 * 
	 * @param id
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getLocalBean(String id, Class<T> clazz) {
		Bean bean = getBeanDef(id, clazz);
		return (T) getBean(id, bean);
	}

	/**
	 * 只在自己的容器里找，返回符合指定定义类型的第一个Bean对象
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getLocalBean(Class<T> clazz) {
		for (Entry<String, Bean> entry : beans.entrySet()) {
			Bean bean = entry.getValue();
			if (clazz.isAssignableFrom(bean.getClazz()))
				return (T) getBean(entry.getKey(), bean);
		}
		throw new NoSuchBeanDefinitionException(clazz.getName());
	}

	/**
	 * 返回指定名字的Bean对象
	 * 
	 * @param id
	 * @return
	 */
	public Object getBean(String id) {
		try {
			return getLocalBean(id);
		} catch (NoSuchBeanDefinitionException e) {
			for (Context parent : parents)
				try {
					return parent.getBean(id);
				} catch (NoSuchBeanDefinitionException pe) {
				}
			throw e;
		}
	}

	/**
	 * 返回符合指定定义类型的第一个Bean对象
	 * 
	 * @param clazz
	 * @return
	 */
	public <T> T getBean(Class<T> clazz) {
		try {
			return getLocalBean(clazz);
		} catch (NoSuchBeanDefinitionException e) {
			for (Context parent : parents)
				try {
					return parent.getBean(clazz);
				} catch (NoSuchBeanDefinitionException pe) {
				}
			throw e;
		}
	}

	/**
	 * 返回容器定义的指定的Bean
	 * 
	 * @param id
	 * @param clazz
	 * @return
	 */
	public <T> T getBean(String id, Class<T> clazz) {
		try {
			return (T) getLocalBean(id, clazz);
		} catch (NoSuchBeanDefinitionException e) {
			for (Context parent : parents)
				try {
					return parent.getBean(id, clazz);
				} catch (NoSuchBeanDefinitionException pe) {
				}
			throw e;
		}
	}

	/**
	 * 返回符合指定定义类型的一组Bean对象
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getBeans(Class<T> clazz) {
		List<T> result = new ArrayList<T>();
		for (Entry<String, Bean> entry : beans.entrySet()) {
			Bean bean = entry.getValue();
			if (!bean.isPrototype() && clazz.isAssignableFrom(bean.getClazz()))
				result.add((T) getSingletonBean(entry.getKey(), bean));
		}
		return result;
	}

	private Object getBean(String id, Bean bean) {
		if (bean.isPrototype())
			return getPrototypeBean(id, bean);
		return getSingletonBean(id, bean);
	}

	private Object getSingletonBean(String id, Bean bean) throws BeanInitializationException {
		synchronized (this) {
			if (bean.getObject() != null)
				return bean.getObject();
			bean.setObject(getPrototypeBean(id, bean));
			return bean.getObject();
		}
	}

	private Object getPrototypeBean(String id, Bean bean) throws BeanInitializationException {
		Object object = null;
		try {
			object = bean.getClazz().newInstance();
			dependInject(object, bean);
			if (object instanceof InitializingBean) {
				((InitializingBean) object).afterPropertiesSet();
			}
		} catch (Throwable e) {
			logger.error("fail to initialize bean: {}", id, e);
			throw new BeanInitializationException(id, e.getMessage(), e);
		}
		return object;
	}

	/**
	 * 给一个对象注入所依赖的容器中的bean
	 * 
	 * @param object 待处理对象
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void dependInject(Object object, Bean bean) throws IllegalArgumentException, NoSuchBeanDefinitionException,
			IllegalAccessException, InvocationTargetException {
		Class<?> clazz = object.getClass();
		String injectName = null;
		for (Field field : clazz.getDeclaredFields()) {
			Inject inject = field.getAnnotation(Inject.class);
			if (inject == null)
				continue;
			Class<?> injectType = field.getType();
			injectName = inject.value();
			if (Utils.isNullOrEmpty(injectName))
				injectName = field.getName();
			// 注入
			Object injectBean = getInjectBean(injectName, injectType, clazz.getName());
			if (inject.obliged())
				checkNotNull(injectBean, "inject bean {}:{} of type {} not found", object.getClass().getName(),
						injectName, injectType.getName());
			field.setAccessible(true);
			field.set(object, injectBean);
		}
		for (Method method : clazz.getMethods()) {
			if (!method.getName().startsWith("set"))
				continue;
			Inject inject = method.getAnnotation(Inject.class);
			if (inject == null)
				continue;
			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes.length != 1)
				continue;
			Class<?> injectType = paramTypes[0];
			// 获取注入名称
			injectName = inject.value();
			if (Utils.isNullOrEmpty(injectName))
				injectName = Utils.lowerFirstChar(method.getName().substring(3));
			// 注入
			Object injectBean = getInjectBean(injectName, injectType, clazz.getName());
			if (inject.obliged())
				checkNotNull(injectBean, "inject bean {}:{} of type {} not found", object.getClass().getName(),
						injectName, injectType.getName());
			method.invoke(object, injectBean);
		}
		if (object instanceof ContextAware)
			((ContextAware) object).setContext(this);
	}

	/**
	 * 取得需要注入的bean
	 * 
	 * @param injectName    注入名
	 * @param injectType    注入类型
	 * @param destClassName 被注入的目标对象类名，用来确定product以便实现根据product名的自动注入
	 * @return
	 */
	protected Object getInjectBean(String injectName, Class<?> injectType, String destClassName) {
		try {
			return getBean(injectName, injectType);
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}

	/**
	 * 给一个Bean定义设置创建好的对象，这个方法用在单例Bean上，一般情况下这个单例bean定义的类是个接口或者抽象类。设置的对象由外部程序创建，
	 * 这个机制是为了解决运行时的动态配置问题。
	 * 
	 * @param id
	 * @param object
	 */
	public void setBean(String id, Object object) throws BeanNotOfRequiredTypeException {
		Bean bean = getBeanDef(id);
		if (bean.getClazz().isAssignableFrom(object.getClass())) {
			bean.setObject(object);
		} else
			throw new BeanNotOfRequiredTypeException(id, bean.getClazz(), object.getClass());
	}

	public void close() {
		logger.info("closing context");
		for (Bean bean : beans.values()) {
			if (!bean.isPrototype()) {
				Object object = bean.getObject();
				if (object instanceof DisposableBean) {
					try {
						((DisposableBean) object).destroy();
					} catch (Exception e) {
						logger.error("destroy bean {} error", bean.getClazz().getName(), e);
					}
				}
			}
		}
	}
}
