package rainbow.service.http;

import java.util.Arrays;
import java.util.List;

import rainbow.core.bundle.BundleActivator;

public class Activator extends BundleActivator {

	@Override
	protected List<Class<?>> extensionPoints() {
		return Arrays.asList(HttpServiceInterceptor.class);
	}
}