package rainbow.core.platform;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.ServiceUnavailableException;

import rainbow.core.util.Utils;

public class Shutdown {

	public static void main(String[] args) {
		String homeStr = System.getProperty("RAINBOW_HOME");
		if (homeStr == null) {
			homeStr = new File(System.getProperty("user.dir")).getParent();
			System.setProperty("RAINBOW_HOME", homeStr);
		}
		Path configFile = Paths.get(homeStr, "conf", "core.json");
		int jmxPort = 1109;
		try {
			Optional<String> portStr = Files.lines(configFile).filter(s -> s.contains("jmxPort"))
					.map(s -> Utils.substringBefore(s, "//")).findFirst();
			if (portStr.isPresent()) {
				jmxPort = Integer.parseInt(Utils.substringAfter(portStr.get(), ":").trim());
			}
		} catch (Throwable e) {
			System.out.println("load core.json failed:" + e.getMessage());
			return;
		}
		System.out.println("Shutting down rainbow platform ...");
		System.out.println("-----------------------------------------------------------------------------"); // NOPMD
		PlatformManagerMBean pmm = getMBean(jmxPort);
		if (pmm != null) {
			try {
				pmm.shutdown();
			} catch (Exception e) {
			}
			System.out.println("Rainbow platform is shutted down!");
		}
	}

	private static PlatformManagerMBean getMBean(int port) {
		try {
			StringBuilder sb = new StringBuilder("service:jmx:rmi:///jndi/rmi://127.0.0.1:");
			sb.append(port).append("/").append("rainbow");
			JMXServiceURL url = new JMXServiceURL(sb.toString());
			JMXConnector connector = JMXConnectorFactory.connect(url);
			MBeanServerConnection conn = connector.getMBeanServerConnection();
			return MBeanServerInvocationHandler.newProxyInstance(conn, PlatformManager.getName(),
					PlatformManagerMBean.class, false);
		} catch (Exception e) {
			if (e.getCause() instanceof ServiceUnavailableException) {
				System.out.println("rainbow platform not found!");
			} else {
				System.out.println("get PlatformManager failed");
				e.printStackTrace();
			}
			return null;
		}
	}

}