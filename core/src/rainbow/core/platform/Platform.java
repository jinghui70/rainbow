package rainbow.core.platform;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.util.Objects;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import rainbow.core.bundle.BundleConfig;
import rainbow.core.bundle.BundleManager;
import rainbow.core.bundle.BundleManagerImpl;
import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.util.encrypt.Encryption;
import rainbow.core.util.ioc.Bean;
import rainbow.core.util.ioc.Context;

/**
 * Rainbow 系统平台
 * 
 * @author lijinghui
 * 
 */
public final class Platform {

	private final static Logger logger = LoggerFactory.getLogger(Platform.class);

	private Platform() {
	}

	private static Platform platform;
	public static PlatformState state = PlatformState.READY;

	/**
	 * 根目录
	 */
	private Path home;

	/**
	 * 平台id
	 */
	private int id;

	/**
	 * 是否开发环境
	 */
	private boolean dev = false;

	private JMXConnectorServer cs;

	private Context context = new Context(ImmutableMap.of( //
			"mBeanServer", Bean.singleton(ManagementFactory.getPlatformMBeanServer(), MBeanServer.class), //
			"bundleLoader", Bean.singleton(BundleLoader.class), //
			"bundleManager", Bean.singleton(BundleManagerImpl.class) //
	));

	/**
	 * 启动平台
	 * 
	 * @param startLocalJmxServer 是否启动本地的JMX Server
	 */
	private void doStart(boolean startLocalJmxServer) {
		String homeStr = Objects.requireNonNull(System.getProperty("RAINBOW_HOME"), "RAINBOW_HOME must be set");
		home = Paths.get(homeStr);
		logger.info("RAINBOW_HOME = {}", home.toString());
		logger.info("loading config param from core.json...");

		BundleConfig bundleConfig = new BundleConfig("core", true);
		id = bundleConfig.getInt("id");
		logger.info("Rainbow ID = {}", id);

		setBundleLoader();

		if (startLocalJmxServer) {
			startLocalJmxServer(bundleConfig.getInt("jmxPort", 1109));
		}

		// 注册扩展点
		ExtensionRegistry.registerExtensionPoint(null, Encryption.class);

		// 加密
		String encryptionClass = bundleConfig.getString("encryption");
		if (encryptionClass != null) {
			try {
				Class.forName(encryptionClass);
			} catch (ClassNotFoundException e) {
				logger.warn("Encryption class not found: {}", encryptionClass);
			}
		}

		BundleManager bundleManager = context.getBean(BundleManager.class);
		bundleManager.refresh();
		bundleManager.initStart();
	}

	/**
	 * 设定BundleLoader
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 * @throws Exception
	 */
	private void setBundleLoader() {
		BundleLoader bundleLoader = null;
		try {
			// 开发环境
			Class<?> clazz = Class.forName("rainbow.core.platform.ProjectBundleLoader");
			bundleLoader = (BundleLoader) clazz.newInstance();
			dev = true;
		} catch (ClassNotFoundException e) {
			bundleLoader = new JarBundleLoader(); // 生产环境
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("create bundleloader failed", e);
		}
		context.setBean("bundleLoader", bundleLoader);
	}

	private void startLocalJmxServer(int jmxPort) {
		logger.info("starting jmx server on port {}", jmxPort);
		// MBeanServer

		MBeanServer mBeanServer = context.getBean("mBeanServer", MBeanServer.class);
		try {
			mBeanServer.registerMBean(new PlatformManager(), PlatformManager.getName());
		} catch (Exception e) {
			logger.error("register PlatformManager failed", e);
			throw new RuntimeException(e);
		}

		try {
			LocateRegistry.createRegistry(jmxPort);
			String url = String.format("service:jmx:rmi:///jndi/rmi://localhost:%d/rainbow", jmxPort);
			JMXServiceURL jmxurl = new JMXServiceURL(url);
			cs = JMXConnectorServerFactory.newJMXConnectorServer(jmxurl, null, mBeanServer);
			cs.start();
			logger.info("jmx server started");
		} catch (IOException e) {
			logger.error("start jmx server failed", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 关闭Rainbow平台
	 */
	public void doShutdown() {
		context.close();
		if (cs != null)
			try {
				cs.stop();
			} catch (IOException e) {
				logger.error("Stop JMX connect server failed", e);
			}
	}

	public static Path getHome() {
		return platform.home;
	}

	public static int getId() {
		return platform.id;
	}

	public static boolean isDev() {
		return platform.dev;
	}

	/**
	 * Rainbow 平台的启动入口
	 */
	public static void startup() {
		startup(true);
	}

	/**
	 * 启动Rainbow平台 @throws
	 */
	public static void startup(boolean startLocalJmxServer) {
		if (state != PlatformState.READY)
			return;
		state = PlatformState.STARTING;
		platform = new Platform();
		try {
			platform.doStart(startLocalJmxServer);
			state = PlatformState.STARTED;
		} catch (RuntimeException | Error e) {
			platform = null;
			state = PlatformState.READY;
			logger.error("start rainbow failed", e);
		}
	}

	/**
	 * 关闭Rainbow平台
	 */
	public static void shutdown() {
		if (state != PlatformState.STARTED)
			return;
		state = PlatformState.STOPPING;
		platform.doShutdown();
		state = PlatformState.READY;
		logger.info("Rainbow platform shutted down!");
	}

}
