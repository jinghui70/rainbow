package rainbow.service;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.bundle.BundleClassLoader;
import rainbow.core.bundle.BundleException;
import rainbow.core.bundle.Resource;
import rainbow.core.bundle.ResourceProcessor;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.NoSuchBeanDefinitionException;
import rainbow.service.internal.Service;

public abstract class ServiceBundleActivator extends BundleActivator {

	private static final Logger logger = LoggerFactory.getLogger(ServiceBundleActivator.class);

	private List<String> services = null;
	
	
	@Override
	protected void doStart() throws BundleException {
		services = new LinkedList<String>();
		getClassLoader().procResource(new ResourceProcessor() {
			@Override
			public void processResource(BundleClassLoader classLoader, Resource resource) throws BundleException {
				if (resource.getName().endsWith("Service.class")) {
					String className = resource.getName().replace('/', '.').replace(".class", Utils.NULL_STR);
					try {
						Class<?> serviceClass = classLoader.loadClass(className);
						String serviceId = Activator.getServiceRegistry().registerService(serviceClass);
						services.add(serviceId);
					} catch (ClassNotFoundException e) {
						throw new BundleException(e.toString());
					}
				}
			}
		});
		for (String serviceId : services) {
			Service service = Activator.getServiceRegistry().getService(serviceId);
			String bean = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, service.getServiceClass().getSimpleName());
			try {
				Object impl = getBean(bean);
				service.setServiceImpl(impl);
			} catch (NoSuchBeanDefinitionException e) {
				logger.error("service implement bean 【{}】not found", bean);
			}
		}
	}

	@Override
	public void stop() {
		if (services != null) {
			for (String serviceId : services) {
				Activator.getServiceRegistry().unregisterService(serviceId);
			}
		}
		super.stop();
	}

}
