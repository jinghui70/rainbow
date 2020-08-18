package rainbow.core.bundle;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import rainbow.core.util.ioc.ApplicationContext;
import rainbow.core.util.ioc.Bean;
import rainbow.core.util.ioc.Context;
import rainbow.core.util.ioc.NoSuchBeanDefinitionException;

public class BundleContext extends ApplicationContext {

	private BundleActivator activator;

	private BundleConfig bundleConfig;

	public BundleContext(BundleActivator activator, Map<String, Bean> beans, List<Context> parents) {
		super(beans, parents);
		this.activator = activator;
	}

	@Override
	public void dependInject(Object object) throws IllegalArgumentException, NoSuchBeanDefinitionException,
			IllegalAccessException, InvocationTargetException {
		super.dependInject(object);
		if (object instanceof ConfigAware) {
			if (bundleConfig == null)
				bundleConfig = new BundleConfig(activator.getBundleId(), false);
			((ConfigAware) object).setBundleConfig(bundleConfig);
		}
	}

}
