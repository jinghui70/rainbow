package rainbow.service;

import static rainbow.core.util.Preconditions.checkNotNull;
import static rainbow.core.util.Preconditions.checkState;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import rainbow.core.model.object.IIdObject;
import rainbow.service.exception.InvalidServiceException;

public class Service implements IIdObject {

	private String id;

	/**
	 * 服务定义接口类
	 */
	private Class<?> serviceClass;

	/**
	 * 方法对象缓存
	 */
	private Map<String, ServiceMethod> methods;

	/**
	 * 服务实现对象
	 */
	private Object serviceImpl;

	@Override
	public String getId() {
		return this.id;
	}

	public Class<?> getServiceClass() {
		return serviceClass;
	}

	public Object getServiceImpl() {
		checkNotNull(serviceImpl, "[{}] serviceImpl not registered", getId());
		return serviceImpl;
	}

	public void setServiceImpl(Object serviceImpl) {
		if (serviceImpl == null) {
			this.serviceImpl = null;
		} else {
			checkState(getServiceClass().isAssignableFrom(serviceImpl.getClass()),
					"wrong service implement [{}] of service [{}]", serviceImpl.getClass().getName(),
					serviceClass.getName());
			this.serviceImpl = serviceImpl;
		}
	}

	public Service(String id, Class<?> serviceClass) {
		this.serviceClass = serviceClass;
		this.id = id;
		cacheMethods(serviceClass);
	}

	public ServiceMethod getMethod(String methodName) throws InvalidServiceException {
		ServiceMethod method = methods.get(methodName);
		if (method == null)
			throw new InvalidServiceException(String.format("%s.%s", id, methodName));
		return method;
	}

	private void cacheMethods(Class<?> serviceClass) {
		methods = new HashMap<String, ServiceMethod>();
		Method[] allMethod = serviceClass.getMethods();
		for (Method method : allMethod) {
			if (method.getDeclaringClass() == serviceClass) { // 不支持服务接口层级集成关系
				String name = method.getName();
				checkState(!methods.containsKey(name), "service[{}] has duplicated method[{}]", serviceClass.getName(),
						name);
				methods.put(name, new ServiceMethod(this, method));
			}
		}
	}

	@Override
	public String toString() {
		return "Service:" + getId();
	}

	List<ServiceMethod> getMethods() {
		return methods.values().stream()
				.sorted((m1, m2) -> m1.getMethod().getName().compareTo(m2.getMethod().getName()))
				.collect(Collectors.toList());
	}

}
