package rainbow.service.http;

import rainbow.core.bundle.BundleActivator;

public class Activator extends BundleActivator {

	@Override
	protected void registerExtensionPoint() {
		registerExtensionPoint(HttpServiceInterceptor.class);
	}

}
