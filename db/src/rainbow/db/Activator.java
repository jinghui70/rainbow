package rainbow.db;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.bundle.BundleException;
import rainbow.db.object.ObjectType;

public class Activator extends BundleActivator {

	@Override
	protected void registerExtensionPoint() throws BundleException {
		registerExtensionPoint(ObjectType.class);
	}

}
