package rainbow.core.util.ioc;

import java.util.List;
import java.util.Map;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.platform.BundleConfig;

public class BundleContext extends ApplicationContext {

	private BundleActivator activator;

	private BundleConfig bundleConfig;

	public BundleContext(BundleActivator activator, Map<String, Bean> beans, List<Context> parents) {
		super(beans, parents);
		this.activator = activator;
	}

	@Override
	protected Object getInjectBean(String injectName, Class<?> injectType, String destClassName) {
		if (injectType == BundleConfig.class) {
			if (bundleConfig == null)
				bundleConfig = new BundleConfig(activator.getBundleId(), false);
			return bundleConfig;
		}
		Object injectBean = super.getInjectBean(injectName, injectType, destClassName);
		if (injectBean == null) {
			InjectProvider ip = ExtensionRegistry.getExtensionObject(InjectProvider.class, injectType.getName());
			if (ip != null) {
				injectBean = ip.getInjectObject(injectName, destClassName);
			}
		}
		return injectBean;
	}
}
