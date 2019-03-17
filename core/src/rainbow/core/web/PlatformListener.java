package rainbow.core.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import rainbow.core.platform.Platform;
import rainbow.core.platform.PlatformState;
import rainbow.core.util.Utils;

public class PlatformListener implements ServletContextListener {

	private boolean startPlatform = false;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		if (Platform.state == PlatformState.READY) {
			String rainbow = System.getProperty("RAINBOW_HOME");
			if (Utils.isNullOrEmpty(rainbow)) {
				rainbow = event.getServletContext().getInitParameter("rainbow");
				if (Utils.isNullOrEmpty(rainbow)) {
					rainbow = "../rainbow";
				}
				System.setProperty("RAINBOW_HOME", rainbow);
			}
			Platform.startup(false);
			startPlatform = true;
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (startPlatform)
			Platform.shutdown();
	}

}
