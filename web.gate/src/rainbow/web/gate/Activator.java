package rainbow.web.gate;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.bundle.BundleException;
import rainbow.core.platform.ConfigData;

public class Activator extends BundleActivator {

	private Server server;

	@Override
	protected void doStart() throws BundleException {
		super.doStart();
		registerExtensionPoint(Handler.class);
		ConfigData config = getConfig();
		int port = config.getInt("port");
		if (port == 0)
			return;
		Server server = new Server(port);
		server.setHandler(getBean("gate", Gate.class));
		try {
			server.start();
			logger.info("start service jetty server at port {}", port);
		} catch (Throwable e) {
			server = null;
			throw new BundleException("starting web gate server failed", e);
		}
	}

	@Override
	public void stop() {
		if (server != null) {
			logger.info("stop web gate server");
			try {
				server.stop();
			} catch (Throwable e) {
			}
			server = null;
		}
		super.stop();
	}
}
