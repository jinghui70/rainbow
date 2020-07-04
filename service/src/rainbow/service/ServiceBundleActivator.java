package rainbow.service;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.bundle.BundleException;
import rainbow.core.util.Utils;

public abstract class ServiceBundleActivator extends BundleActivator {

	private static final Logger logger = LoggerFactory.getLogger(ServiceBundleActivator.class);

	/**
	 * 返回服务前缀名用以区分不同插件中的服务
	 * 
	 * @return
	 */
	protected String getPrefix() {
		return Utils.NULL_STR;
	}

	@Override
	protected void doStart() throws BundleException {
		List<Service> services = new LinkedList<Service>();
		getClassLoader().procClass(clazz -> {
			if (clazz.isInterface() && clazz.getName().endsWith("Service")) {
				String id = Utils.lowerFirstChar(Utils.substringBefore(clazz.getSimpleName(), "Service"));

				Service service = new Service(id, clazz);
				String name = Utils.lowerFirstChar(clazz.getSimpleName());
				Object bean = checkNotNull(getBean(name, clazz), "service bean not found: {}", name);
				service.setServiceImpl(bean);
				services.add(service);
				logger.debug("service found: {}", name);
			}
		});
		Activator.getServiceRegistry().registerServices(getBundleId(), services);
	}

	@Override
	public void stop() {
		Activator.getServiceRegistry().unregisterServices(getBundleId());
		super.stop();
	}

}
