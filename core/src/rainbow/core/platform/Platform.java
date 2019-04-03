package rainbow.core.platform;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import rainbow.core.bundle.BundleListener;
import rainbow.core.bundle.BundleManager;
import rainbow.core.bundle.BundleManagerImpl;
import rainbow.core.console.CommandProvider;
import rainbow.core.console.Console;
import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.util.JMXServiceURLBuilder;
import rainbow.core.util.encrypt.Encryption;
import rainbow.core.util.ioc.Bean;
import rainbow.core.util.ioc.Context;
import rainbow.core.web.UploadHandler;
import rainbow.core.web.UrlHandler;

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
			"mBeanServer", Bean.singleton(MBeanServerFactory.createMBeanServer(), MBeanServer.class), //
			"bundleLoader", Bean.singleton(BundleLoader.class), //
			"bundleManager", Bean.singleton(BundleManagerImpl.class), //
			"bundleCommandProvider", Bean.singleton(BundleCommandProvider.class) //
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

		ConfigData configData = new ConfigData("core", true);
		id = configData.getInt("id");
		logger.info("Rainbow ID = {}", id);

		setBundleLoader();

		if (startLocalJmxServer) {
			startLocalJmxServer(configData.getInt("jmxPort", 1109));
		}

		// 注册扩展点
		ExtensionRegistry.registerExtensionPoint(null, BundleListener.class);
		ExtensionRegistry.registerExtensionPoint(null, Encryption.class);
		ExtensionRegistry.registerExtensionPoint(null, UrlHandler.class);
		ExtensionRegistry.registerExtensionPoint(null, UploadHandler.class);

		// 加密
		String encryptionClass = configData.getString("encryption");
		if (encryptionClass != null) {
			try {
				Class.forName(encryptionClass);
			} catch (ClassNotFoundException e) {
				logger.warn("Encryption class [{}] not found!", encryptionClass);
			}
		}
		// 控制台
		if (configData.getBool("console")) {
			ExtensionRegistry.registerExtensionPoint(null, CommandProvider.class);
			ExtensionRegistry.registerExtension(null, CommandProvider.class,
					context.getBean(BundleCommandProvider.class));
			Console console = new Console();
			Thread t = new Thread(console, "Rainbow Console");
			t.setDaemon(true);
			t.start();
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
		logger.error("start jmx server on port {}", jmxPort);
		// MBeanServer
		MBeanServer mBeanServer = context.getBean("mBeanServer", MBeanServer.class);
		try {
			JMXServiceURL url = new JMXServiceURLBuilder(jmxPort, "rainbow").getJMXServiceURL();
			cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
			cs.start();
		} catch (IOException e) {
			logger.error("start jmx server failed", e);
			throw new RuntimeException(e);
		}
		try {
			mBeanServer.registerMBean(new PlatformManager(), PlatformManager.getName());
		} catch (Exception e) {
			logger.error("register PlatformManager failed", e);
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
