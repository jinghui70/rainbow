package rainbow.db;

import java.util.Arrays;
import java.util.List;

import rainbow.core.bundle.BundleActivator;
import rainbow.db.refinery.Refinery;

public class Activator extends BundleActivator {

	@Override
	protected List<Class<?>> extensionPoints() {
		return Arrays.asList(Refinery.class);
	}

}
