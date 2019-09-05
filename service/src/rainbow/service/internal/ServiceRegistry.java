package rainbow.service.internal;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import rainbow.core.bundle.Bean;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.ActivatorAwareObject;
import rainbow.core.util.ioc.InitializingBean;

/**
 * 服务注册表
 * 
 * @author lijinghui
 * 
 */
@Bean
public final class ServiceRegistry extends ActivatorAwareObject implements InitializingBean {

	/** 本地服务 */
	private ConcurrentMap<String, Service> serviceMap = new ConcurrentHashMap<String, Service>();

	@Override
	public void afterPropertiesSet() {
	}

	/**
	 * 返回指定名称的本地服务
	 * 
	 * @param id 服务的ID
	 * @return 服务对象
	 */
	public Service getService(String id) {
		return checkNotNull(serviceMap.get(id), "Service [{}] not exist", id);
	}

	public Method getMethod(String id, String methodName) {
		Service service = getService(id);
		return checkNotNull(service.getMethod(methodName), "Service [{}-{}] not exist", id, methodName);
	}

	/**
	 * 注册一组服务
	 * 
	 * @param services
	 */
	public void registerServices(List<Service> services) {
		if (Utils.isNullOrEmpty(services))
			return;
		for (Service service : services) {
			checkArgument(!serviceMap.containsKey(service.getId()), "duplicated service id of [{}]", service.getId());
			serviceMap.put(service.getId(), service);
		}
	}

	/**
	 * 注销一组服务定义
	 * 
	 * @param serviceId
	 */
	public void unregisterServices(List<Service> services) {
		if (Utils.isNullOrEmpty(services))
			return;
		for (Service service : services)
			serviceMap.remove(service.getId());
	}

	public List<Service> getServices(final String prefix) {
		return serviceMap.values().stream().filter(service -> service.getId().startsWith(prefix))
				.collect(Collectors.toList()); // TODO 排序
	}

}
