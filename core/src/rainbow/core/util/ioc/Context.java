package rainbow.core.util.ioc;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

	private List<Context> parents = Collections.emptyList();

	public Context() {
		this.beans = new HashMap<String, Bean>();
	}

	/**
	 * 构造函数
	 * 
	 * @param beans IOC配置信息
	 */
	public Context(Map<String, Bean> beans) {
		this.beans = beans;
	}

	public Context(Map<String, Bean> beans, List<Context> parents) {
		this.beans = beans;
		if (Utils.hasContent(parents))
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
				getBean(entry.getKey(), bean);
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
		Bean bean = getBeanDef(id);
		Class<?> checkClass = bean.getClazz();
		if (bean.isFactory()) {
			Factory<?> factory = (Factory<?>) getBeanObject(id, bean);
			checkClass = factory.targetClass();
		}
		if (clazz.isAssignableFrom(checkClass)) {
			return (T) getBean(id, bean);
		}
		throw new BeanNotOfRequiredTypeException(id, clazz, checkClass);
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
			String id = entry.getKey();
			Class<?> checkClass = bean.getClazz();
			if (bean.isFactory()) {
				Factory<?> factory = (Factory<?>) getBeanObject(id, bean);
				checkClass = factory.targetClass();
			}
			if (clazz.isAssignableFrom(checkClass))
				return (T) getBean(id, bean);
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
			return getLocalBean(id, clazz);
		} catch (NoSuchBeanDefinitionException e) {
			for (Context parent : parents)
				try {
					return parent.getBean(id, clazz);
				} catch (NoSuchBeanDefinitionException pe) {
				}
			throw e;
		}
	}

	private Object getBean(String id, Bean bean) {
		if (bean.isFactory()) {
			Factory<?> factory = (Factory<?>) getBeanObject(id, bean);
			if (bean.isPrototype())
				return factory.create();
			return getTargetObject(factory, bean);
		} else if (bean.isPrototype())
			return getPrototypeBean(id, bean);
		return getBeanObject(id, bean);
	}

	private Object getTargetObject(Factory<?> factory, Bean bean) {
		if (bean.getTargetObject() == null) {
			synchronized (this) {
				if (bean.getTargetObject() == null)
					bean.setTargetObject(factory.create());
			}
		}
		return bean.getTargetObject();
	}

	private Object getBeanObject(String id, Bean bean) throws BeanInitializationException {
		if (bean.getObject() == null) {
			synchronized (this) {
				if (bean.getObject() == null)
					bean.setObject(getPrototypeBean(id, bean));
			}
		}
		return bean.getObject();
	}

	private Object getPrototypeBean(String id, Bean bean) throws BeanInitializationException {
		Object object = null;
		try {
			object = bean.getClazz().getDeclaredConstructor().newInstance();
			dependInject(object);
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
	public void dependInject(Object object) throws IllegalArgumentException, NoSuchBeanDefinitionException,
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
			if (!bean.isPrototype() || bean.isFactory()) {
				Object object = bean.getObject();
				if (object != null && object instanceof DisposableBean) {
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
