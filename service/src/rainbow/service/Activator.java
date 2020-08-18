package rainbow.service;

import java.util.Arrays;
import java.util.List;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.bundle.BundleException;
import rainbow.service.internal.ServiceRegistryImpl;

public class Activator extends BundleActivator {

	private static Activator activator;

	@Override
	protected void doStart() throws BundleException {
		activator = this;
	}

	@Override
	protected List<Class<?>> extensionPoints() {
		return Arrays.asList(ServiceInterceptor.class);
	}

	public static ServiceRegistryImpl getServiceRegistry() {
		return activator.context.getBean(ServiceRegistryImpl.class);
	}
}
