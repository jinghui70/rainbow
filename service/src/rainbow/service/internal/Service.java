package rainbow.service.internal;

import static rainbow.core.util.Preconditions.checkNotNull;
import static rainbow.core.util.Preconditions.checkState;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import rainbow.core.model.object.IdObject;
import rainbow.service.InvalidServiceMethodException;

public class Service extends IdObject {

	/**
	 * 服务定义接口类
	 */
	private Class<?> serviceClass;

	/**
	 * 方法对象缓存
	 */
	private Map<String, Method> methods;

	/**
	 * 服务实现对象
	 */
	private Object serviceImpl;

	public Class<?> getServiceClass() {
		return serviceClass;
	}

	public Object getServiceImpl() {
		checkNotNull(serviceImpl, "[{}] serviceImpl not registered", id);
		return serviceImpl;
	}

	public void setServiceImpl(Object serviceImpl) {
		if (serviceImpl == null) {
			this.serviceImpl = null;
		} else {
			checkState(getServiceClass().isAssignableFrom(serviceImpl.getClass()),
					"wrong service implement [{}] of service [{}]", serviceImpl.getClass().getName(), id);
			this.serviceImpl = serviceImpl;
		}
	}

	public Service(String id, Class<?> serviceClass) {
		this.setId(id);
		this.serviceClass = serviceClass;
		methods = cacheMethods(serviceClass);
	}

	public Method getMethod(String methodName) throws InvalidServiceMethodException {
		Method method = methods.get(methodName);
		if (method == null)
			throw new InvalidServiceMethodException(id, methodName);
		return method;
	}

	private Map<String, Method> cacheMethods(Class<?> serviceClass) {
		Method[] allMethod = serviceClass.getMethods();
		Map<String, Method> map = new HashMap<String, Method>();
		for (Method method : allMethod) {
			if (method.getDeclaringClass() == serviceClass) { // 不支持服务接口层级集成关系
				String name = method.getName();
				checkState(!map.containsKey(name), "service[{}] has duplicated method[{}]", id, name);
				map.put(name, method);
			}
		}
		return map;
	}

	@Override
	public String toString() {
		return "Service:" + id;
	}

}
