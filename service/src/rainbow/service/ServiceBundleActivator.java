package rainbow.service;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.bundle.BundleException;
import rainbow.core.util.Utils;
import rainbow.service.internal.Service;

public abstract class ServiceBundleActivator extends BundleActivator {

	private static final Logger logger = LoggerFactory.getLogger(ServiceBundleActivator.class);

	private List<Service> services = null;

	private String serviceId(String className) {
		String[] parts = Utils.split(className, '.');
		int len = parts.length;
		String last = Utils.lowerFirstChar(parts[len - 1]);
		last = Utils.substringBefore(last, "Service");
		if (last.equals(parts[len - 2])) {
			len--;
		} else {
			parts[len - 1] = last;
		}
		return Arrays.stream(parts).limit(len).collect(Collectors.joining("."));
	}

	@Override
	protected void doStart() throws BundleException {
		services = new LinkedList<Service>();
		getClassLoader().procClass(clazz -> {
			if (clazz.isInterface() && clazz.getName().endsWith("Service")) {
				String id = serviceId(clazz.getName());
				if (id.startsWith("rainbow."))
					id = id.substring(8);
				Service service = new Service(id, clazz);
				String name = Utils.lowerFirstChar(clazz.getSimpleName());
				Object bean = checkNotNull(getBean(name), "service bean not found: {}", name);
				service.setServiceImpl(bean);
				services.add(service);
				logger.debug("service found: {}", id);
			}
		});
		Activator.getServiceRegistry().registerServices(services);
	}

	@Override
	public void stop() {
		if (services != null)
			Activator.getServiceRegistry().unregisterServices(services);
		super.stop();
	}

}
