package rainbow.service.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import rainbow.service.Internal;
import rainbow.service.ServiceNotReadyException;

public class Service implements InvocationHandler {

	/**
	 * 服务ID
	 */
	private String serviceId;

	/**
	 * 服务定义接口类
	 */
	private Class<?> serviceClass;

	/**
	 * 方法对象缓存
	 */
	private Map<String, Method> methods;

	/**
	 * 注入到其他服务的代理对象
	 */
	private Object serviceProxy;

	/**
	 * 服务实现对象
	 */
	private Object serviceImpl;

	/**
	 * 注册时间
	 */
	private long registerTime;

	/**
	 * 内部服务标识
	 */
	private boolean internal;

	/**
	 * 需要检查的session
	 */
	private List<String> session;

	public String getServiceId() {
		return serviceId;
	}

	public Class<?> getServiceClass() {
		return serviceClass;
	}

	public Object getServiceImpl() {
		checkNotNull(serviceImpl, "[%s] serviceImpl not registered", serviceId);
		return serviceImpl;
	}

	public void setServiceImpl(Object serviceImpl) {
		if (serviceImpl == null) {
			this.serviceImpl = null;
		} else {
			checkState(getServiceClass().isAssignableFrom(serviceImpl.getClass()),
					"wrong service implement [%s] of service [%s]", serviceImpl.getClass().getName(), serviceId);
			this.serviceImpl = serviceImpl;
		}
	}

	public Object getServiceProxy() {
		return serviceProxy;
	}

	public long getRegisterTime() {
		return registerTime;
	}

	public Service(String serviceId, Class<?> serviceClass) {
		this.serviceId = serviceId;
		this.serviceClass = serviceClass;
		this.serviceProxy = Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[] { serviceClass }, this);
		internal = serviceClass.getAnnotation(Internal.class) != null;
		rainbow.service.Session s = serviceClass.getAnnotation(rainbow.service.Session.class);
		if (s != null)
			session = Lists.newArrayList(s.value());
		methods = cacheMethods(serviceClass);
		registerTime = System.currentTimeMillis();
	}

	public boolean isInternal() {
		return internal;
	}

	public List<String> getSession() {
		return session;
	}

	public Method getMethod(String methodName) {
		return methods.get(methodName);
	}

	private Map<String, Method> cacheMethods(Class<?> serviceClass) {
		Method[] allMethod = serviceClass.getMethods();
		if (allMethod.length == 0)
			return ImmutableMap.of();

		Map<String, Method> map = new HashMap<String, Method>(allMethod.length);
		for (Method method : allMethod) {
			if (method.isAnnotationPresent(Internal.class))
				continue;
			String name = method.getName();
			if (!map.containsKey(name))
				map.put(name, method);
			else if (method.getDeclaringClass() == serviceClass) { // 不相等表示上级接口中的函数，忽略之
				Method old = map.get(name);
				checkState(old.getDeclaringClass() != serviceClass, "service[%s] has duplicated method[%s]", serviceId,
						name);
				map.put(name, method);
			}
		}
		return map;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (serviceImpl == null) {
			// 将来可能会访问远程部署的服务, 那时候就要注入远程registry了
			throw new ServiceNotReadyException(serviceId);
		}
		try {
			return method.invoke(serviceImpl, args);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	@Override
	public String toString() {
		return "Service:" + getServiceId();
	}

}
