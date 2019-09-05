package rainbow.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.bundle.BundleException;
import rainbow.core.platform.ConfigData;
import rainbow.web.internal.Gate;
import rainbow.web.internal.RequestErrorHandler;

/**
 * Web服务模块
 * 
 * 配置说明:
 * 
 * port: 端口，0表示不启动
 * 
 * @author lijinghui
 *
 */
public class Activator extends BundleActivator {

	private Server server;

	@Override
	protected void doStart() throws BundleException {
		super.doStart();
		registerExtensionPoint(RequestHandler.class);
		ConfigData config = getConfig();
		int port = config.getInt("port");
		if (port == 0)
			return;

		server = new Server(port);
		SessionHandler sessionHandler = new SessionHandler();
		// GzipHandler gzipHandler = new GzipHandler();
		sessionHandler.setHandler(getBean("gate", Gate.class));

		server.setHandler(sessionHandler);
		server.setErrorHandler(new RequestErrorHandler());
		try {
			server.start();
			server.setStopAtShutdown(true);
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
