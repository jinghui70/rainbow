package rainbow.service.internal;

import static rainbow.core.util.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import rainbow.core.bundle.Bean;
import rainbow.core.util.Utils;
import rainbow.service.Service;
import rainbow.service.ServiceRegistry;
import rainbow.service.exception.InvalidServiceException;

/**
 * 服务注册表
 * 
 * @author lijinghui
 * 
 */
@Bean
public final class ServiceRegistryImpl implements ServiceRegistry {

	/** 本地服务 */
	private ConcurrentMap<String, Service> serviceMap = new ConcurrentHashMap<String, Service>();
	
	/**
	 * 
	 */
	private Map<String, List<Service>> bundleServices = new HashMap<String, List<Service>>();

	/**
	 * 返回指定名称的本地服务
	 * 
	 * @param id 服务的ID
	 * @return 服务对象
	 * @throws InvalidServiceException
	 */
	public Service getService(String id) throws InvalidServiceException {
		Service service = serviceMap.get(id);
		if (service == null)
			throw new InvalidServiceException(id);
		return service;
	}

	/**
	 * 注册一组服务
	 * 
	 * @param services
	 */
	public void registerServices(String bundleId, List<Service> services) {
		if (Utils.isNullOrEmpty(services))
			return;
		bundleServices.put(bundleId, services);
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
	public void unregisterServices(String bundleId) {
		List<Service> services = bundleServices.get(bundleId);
		if (Utils.isNullOrEmpty(services))
			return;
		for (Service service : services)
			serviceMap.remove(service.getId());
	}

	public Map<String, List<Service>> getServices() {
		return bundleServices;
	}

}
