package rainbow.db;

import java.util.Arrays;
import java.util.List;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.bundle.FatherBundle;
import rainbow.db.database.Dialect;
import rainbow.db.refinery.Refinery;

public class Activator extends BundleActivator implements FatherBundle {

	@Override
	protected List<Class<?>> extensionPoints() {
		return Arrays.asList(Dialect.class, Refinery.class);
	}

	@Override
	public void afterSonLoaded() throws Exception {
	}

}
