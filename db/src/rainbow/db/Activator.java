package rainbow.db;

import rainbow.core.bundle.BundleActivator;
import rainbow.db.refinery.Refinery;

public class Activator extends BundleActivator {

	@Override
	protected void registerExtensionPoint() {
		registerExtensionPoint(Refinery.class);
	}

}
