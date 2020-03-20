package rainbow.core.bundle;

import static rainbow.core.util.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import rainbow.core.extension.Extension;
import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.platform.ConfigData;
import rainbow.core.platform.Platform;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.Bean;
import rainbow.core.util.ioc.BundleContext;
import rainbow.core.util.ioc.Context;

/**
 * Bundle启动器
 * 
 * @author lijinghui
 * 
 */
public abstract class BundleActivator {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected MBeanServer mBeanServer;

	private String bundleId;

	protected Context context;

	private List<Class<?>> points;
	private List<Extension> extensions;
	private List<ObjectName> mBeanNames;

	public BundleClassLoader getClassLoader() {
		return (BundleClassLoader) getClass().getClassLoader();
	}

	public String getBundleId() {
		return bundleId;
	}

	public final void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public Context getContext() {
		return context;
	}

	/**
	 * Bundle启动
	 * 
	 * @param mBeanServer
	 * @param bundleId
	 * @throws BundleException
	 */
	public final void start(MBeanServer mBeanServer, List<Context> parentContexts) throws BundleException {
		this.mBeanServer = mBeanServer;
		registerExtensionPoint();

		Map<String, Bean> contextConfig = new HashMap<String, Bean>();
		List<ExtensionConfig> extConfigs = new LinkedList<ExtensionConfig>();

		// 读取@Bean配置
		readClassConfig(contextConfig, extConfigs);
		// 加载Context
		if (!contextConfig.isEmpty()) {
			context = createContext(contextConfig, parentContexts);
			context.loadAll();
		}

		// 注册配置的扩展
		extConfigs.stream().forEach(extConfig -> {
			if (Utils.isNullOrEmpty(extConfig.getBeanName())) {
				try {
					Object eo = extConfig.getExtClass().newInstance();
					registerExtension(extConfig.getPoint(), extConfig.getName(), eo);
				} catch (InstantiationException | IllegalAccessException e) {
					logger.error("register extension {}[{}] failed", extConfig.getName(), extConfig.getExtClass(), e);
				}
			} else {
				registerExtension(extConfig.getPoint(), extConfig.getName(), getBean(extConfig.getBeanName()));
			}
		});
		registerExtension();
		doStart();
	}

	/**
	 * 注册所有的扩展点
	 */
	protected void registerExtensionPoint() throws BundleException {
	}

	/**
	 * 注册未配置的扩展，一般用不到
	 */
	protected void registerExtension() throws BundleException {
	}

	/**
	 * 其它初始化代码写在这里
	 * 
	 * @throws BundleException
	 */
	protected void doStart() throws BundleException {
	}

	/**
	 * 创建context
	 * 
	 * @param contextConfig
	 * @param parent
	 */
	protected Context createContext(Map<String, Bean> contextConfig, List<Context> parent) {
		return new BundleContext(this, contextConfig, parent);
	}

	/**
	 * 读取类上的配置
	 * 
	 * @param contextConfig
	 * @param extBeans
	 */
	final protected void readClassConfig(Map<String, Bean> contextConfig, List<ExtensionConfig> extBeans) {
		getClassLoader().procClass(clazz -> {
			// 读扩展配置
			rainbow.core.bundle.Extension extDef = clazz.getAnnotation(rainbow.core.bundle.Extension.class);
			ExtensionConfig extConfig = null;
			if (extDef != null) {
				extConfig = new ExtensionConfig();
				extConfig.setName(extDef.name());
				extConfig.setExtClass(clazz);
				Class<?> point = extDef.point();
				if (point == Object.class) {
					Class<?>[] interfaces = clazz.getInterfaces();
					checkState(interfaces.length > 0, "{} should implements an extension point", clazz.getName());
					point = interfaces[0];
				}
				extConfig.setPoint(point);
				extBeans.add(extConfig);
			}

			// 读Bean配置
			rainbow.core.bundle.Bean beandef = clazz.getAnnotation(rainbow.core.bundle.Bean.class);
			if (beandef == null)
				return;
			String beanName = beandef.name();
			if (beanName.isEmpty()) {
				beanName = Utils.lowerFirstChar(clazz.getSimpleName());
				if (beanName.endsWith("Impl"))
					beanName = Utils.substringBefore(beanName, "Impl");
			}
			if (beandef.singleton()) {
				contextConfig.put(beanName, Bean.singleton(clazz));
			} else
				contextConfig.put(beanName, Bean.prototype(clazz));
			if (extConfig != null)
				extConfig.setBeanName(beanName);
		});
	}

	/**
	 * Bundle停止
	 */
	public void stop() {
		if (points != null)
			for (Class<?> point : points)
				ExtensionRegistry.unregisterExtensionPoint(point);
		if (extensions != null)
			for (Extension extension : extensions)
				ExtensionRegistry.unregisterExtension(extension);
		if (mBeanNames != null)
			unregisterMBean();
		if (context != null)
			context.close();
	}

	/**
	 * 打开bundle中的一个资源
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	protected InputStream getResource(String resource) throws IOException {
		Resource r = getClassLoader().getLocalResource(resource);
		return r.getInputStream();
	}

	/**
	 * 返回Bundle的配置目录
	 * 
	 * @return
	 */
	private Path getConfigurePath() {
		return Platform.getHome().resolve("conf").resolve(bundleId);
	}

	/**
	 * 返回Bundle的配置目录下文件
	 * 
	 * @return
	 */
	public Path getConfigureFile(String fileName) {
		return getConfigurePath().resolve(fileName);
	}

	/**
	 * 返回Bundle的配置目录下指定后缀的所有文件
	 * 
	 * @param suffix
	 * @return
	 */
	public final List<Path> getConfigureFiles(final String suffix) {
		try {
			return Files.list(getConfigurePath()).filter(f -> f.getFileName().toString().endsWith(suffix))
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 返回配置
	 * 
	 * @param key
	 * @return
	 */
	public final ConfigData getConfig() {
		return new ConfigData(bundleId);
	}

	/**
	 * 如果配置文件是一个json文件，直接解析为一个对象
	 * 
	 * @param fileName
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public final <T> T parseConfigFile(final String fileName, Class<T> clazz) {
		Path file = getConfigureFile(fileName);
		try (InputStream is = Files.newInputStream(file)) {
			return JSON.parseObject(is, StandardCharsets.UTF_8, clazz);
		} catch (IOException e) {
			throw new RuntimeException(String.format("parse file %s error", fileName), e);
		}
	}

	/**
	 * 如果配置文件是一个json文件，直接解析为一个对象
	 * 
	 * @param fileName
	 * @param tr
	 * @return
	 * @throws IOException
	 */
	public <T> T parseConfigFile(final String fileName, TypeReference<T> tr) {
		Path file = getConfigureFile(fileName);
		try (InputStream is = Files.newInputStream(file)) {
			return JSON.parseObject(is, StandardCharsets.UTF_8, tr.getType());
		} catch (IOException e) {
			throw new RuntimeException(String.format("parse file %s error", fileName), e);
		}
	}

	/**
	 * 注册扩展点
	 * 
	 * @param clazz
	 */
	protected final void registerExtensionPoint(Class<?> clazz) {
		ExtensionRegistry.registerExtensionPoint(bundleId, clazz);
		if (points == null)
			points = new LinkedList<Class<?>>();
		points.add(clazz);
	}

	/**
	 * 注册一个扩展
	 * 
	 * @param clazz  扩展点
	 * @param name   扩展名
	 * @param object 扩展对象
	 */
	protected final void registerExtension(Class<?> clazz, String name, Object object) {
		Extension extension = ExtensionRegistry.registerExtension(bundleId, clazz, name, object);
		if (extensions == null)
			extensions = new LinkedList<Extension>();
		extensions.add(extension);
	}

	protected final void registerMBean(Object mbean, String name) {
		try {
			ObjectName objName = new ObjectName("rainbow:name=" + name);
			mBeanServer.registerMBean(mbean, objName);
			if (mBeanNames == null)
				mBeanNames = new LinkedList<ObjectName>();
			mBeanNames.add(objName);
		} catch (Exception e) {
			logger.error("registerMBean {} failed", name, e);
		}
	}

	protected final void unregisterMBean() {
		if (mBeanNames == null)
			return;
		for (ObjectName objName : mBeanNames)
			try {
				mBeanServer.unregisterMBean(objName);
			} catch (Exception e) {
				logger.error("unregisterMBean {} failed", objName.getCanonicalName(), e);
			}
	}

	public final <T> T getBean(String name, Class<T> clazz) {
		if (context == null)
			return null;
		return context.getLocalBean(name, clazz);
	}

	public final Object getBean(String name) {
		if (context == null)
			return null;
		return context.getLocalBean(name);
	}

}
