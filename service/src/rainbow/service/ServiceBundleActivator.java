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

	@Override
	protected void doStart() throws BundleException {
		List<Service> services = new LinkedList<Service>();
		getClassLoader().procClass(clazz -> {
			if (clazz.isInterface() && clazz.getName().endsWith("Service")) {
				Service service = new Service(clazz);
				String name = Utils.lowerFirstChar(clazz.getSimpleName());
				Object bean = checkNotNull(getBean(name), "service bean not found: {}", name);
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
