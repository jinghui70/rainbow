package rainbow.core.util.ioc;

import rainbow.core.bundle.BundleActivator;

/**
 * Interface to be implemented by any object that wishes to be notified of the
 * Activator that it runs in.
 * 
 * @author lijinghui
 * 
 */
public class ActivatorAwareObject implements ActivatorAware {

	protected BundleActivator activator;

	public void setActivator(BundleActivator activator) {
		this.activator = activator;
	}

}
