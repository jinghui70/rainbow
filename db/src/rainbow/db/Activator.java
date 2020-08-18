package rainbow.db;

import java.util.Arrays;
import java.util.List;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.bundle.FatherBundle;
import rainbow.db.internal.DaoManagerImpl;
import rainbow.db.refinery.Refinery;

public class Activator extends BundleActivator implements FatherBundle {

	@Override
	protected List<Class<?>> extensionPoints() {
		return Arrays.asList(Refinery.class);
	}

	@Override
	public void afterSonLoaded() throws Exception {
		DaoManagerImpl daoManager = getBean("daoManager", DaoManagerImpl.class);
		daoManager.init();
	}

}
