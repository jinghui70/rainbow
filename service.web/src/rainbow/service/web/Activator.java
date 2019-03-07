package rainbow.service.web;

import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

import com.google.common.collect.ImmutableList;

import rainbow.core.bundle.BundleActivator;
import rainbow.core.bundle.BundleException;
import rainbow.core.platform.ConfigData;

public class Activator extends BundleActivator {

	private Server server;

	@Override
	public List<String> getParentContextId() {
		return ImmutableList.<String>of("service");
	}

	@Override
	protected void doStart() throws BundleException {
		super.doStart();
		ConfigData config = getConfig();
		int port = config.getInt("server");
		if (port == 0)
			return;
		Server server = new Server(port);
		server.setHandler(getBean("serviceHandler", Handler.class));
		try {
			server.start();
			logger.info("start service jetty server at port {}", port);
		} catch (Throwable e) {
			server = null;
			throw new BundleException("启动服务的WebServer失败", e);
		}
	}

	@Override
	public void stop() {
		if (server != null) {
			logger.info("stop service jetty server");
			try {
				server.stop();
			} catch (Throwable e) {
			}
			server = null;
		}
		super.stop();
	}
}
