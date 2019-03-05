package rainbow.core.util;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 根据配置信息创建 jmx serverice url的工具类
 * 
 * @author wangzheng
 * 
 */
public class JMXServiceURLBuilder {

	private Logger logger = LoggerFactory.getLogger(JMXServiceURLBuilder.class);

	private int port;

	private String serverName;

	public int getPort() {
		return port;
	}

	public JMXServiceURLBuilder(int port, String serverName) {
		this.port = port;
		this.serverName = serverName;
		Registry reg = null;

		// 每台物理机器上启动一个rmi registry
		try {
			reg = LocateRegistry.getRegistry(port);
			reg.list();
		} catch (RemoteException e) {
			reg = null; // local registry is not started
		}
		if (reg == null) {
			try {
				logger.info("Create RMI registry on port [" + port + "]");
				reg = LocateRegistry.createRegistry(port);
			} catch (RemoteException e) {
				logger.error("can not create rmi registry on the server", e);
				throw new RuntimeException(e);
			}
		}
	}

	public JMXServiceURL getJMXServiceURL() {
		StringBuilder sb = new StringBuilder("service:jmx:rmi:///jndi/rmi://127.0.0.1:");
		sb.append(port).append("/").append(serverName);
		try {
			JMXServiceURL serviceURL = new JMXServiceURL(sb.toString());
			logger.debug("local jmx service url is [{}]", serviceURL.toString());
			return serviceURL;
		} catch (MalformedURLException e) {
			logger.error("create JMX Service URL failed ", e);
			throw new RuntimeException(e);
		}
	}
}
