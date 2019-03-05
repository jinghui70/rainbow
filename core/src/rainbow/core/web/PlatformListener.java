package rainbow.core.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.common.base.Strings;

import rainbow.core.platform.Platform;
import rainbow.core.platform.PlatformState;

public class PlatformListener implements ServletContextListener {

	private boolean startPlatform = false;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		if (Platform.state == PlatformState.READY) {
			String rainbow = System.getProperty("RAINBOW_HOME");
			if (Strings.isNullOrEmpty(rainbow)) {
				rainbow = event.getServletContext().getInitParameter("rainbow");
				if (Strings.isNullOrEmpty(rainbow)) {
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
