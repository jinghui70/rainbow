package rainbow.service;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.bundle.BundleException;
import rainbow.service.internal.ServiceRegistry;

public class Activator extends BundleActivator {
	
	private static Activator activator;
	
	@Override
	protected void doStart() throws BundleException {
		activator = this;
	}

	@Override
	protected void registerExtensionPoint() throws BundleException {
		registerExtensionPoint(ServiceInterceptor.class);
	}

	public static ServiceRegistry getServiceRegistry() {
		return activator.context.getBean(ServiceRegistry.class);
	}
}
