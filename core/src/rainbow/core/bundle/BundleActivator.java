package rainbow.core.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.extension.Extension;
import rainbow.core.extension.ExtensionRegistry;
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
		registerExtension(extConfigs);
		doStart();
	}

	/**
	 * 注册所有的扩展点
	 */
	protected void registerExtensionPoint() {
	}

	/**
	 * 注册配置的扩展
	 */
	private void registerExtension(List<ExtensionConfig> extConfigs) throws BundleException {
		for (ExtensionConfig extConfig : extConfigs) {
			rainbow.core.bundle.Extension extDef = extConfig.getAnnotation();
			Class<?> point = extDef.point();
			if (point == Object.class) {
				Class<?>[] interfaces = extConfig.getClazz().getInterfaces();
				if (interfaces.length == 0) {
					throw new BundleException("{} should implements an extension point",
							extConfig.getClazz().getName());
				}
				point = interfaces[0];
			}
			Object object = null;
			if (Utils.isNullOrEmpty(extConfig.getBeanName())) {
				try {
					object = extConfig.getClazz().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new BundleException("create extension object of[{}] failed", extConfig.getClazz(), e);
				}
			} else {
				object = getBean(extConfig.getBeanName());
			}
			Extension extension = ExtensionRegistry.registerExtension(bundleId, point, extDef.name(), extDef.order(),
					object);
			if (extensions == null)
				extensions = new LinkedList<Extension>();
			extensions.add(extension);
		}
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
	final protected void readClassConfig(Map<String, Bean> contextConfig, List<ExtensionConfig> extConfigs) {
		getClassLoader().procClass(clazz -> {
			// 读扩展配置
			rainbow.core.bundle.Extension extDef = clazz.getAnnotation(rainbow.core.bundle.Extension.class);
			ExtensionConfig extConfig = null;
			if (extDef != null) {
				extConfig = new ExtensionConfig(extDef, clazz);
				extConfigs.add(extConfig);
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
		Optional.ofNullable(points).ifPresent(p -> p.forEach(ExtensionRegistry::unregisterExtensionPoint));
		Optional.ofNullable(extensions).ifPresent(e -> e.forEach(ExtensionRegistry::unregisterExtension));
		if (mBeanNames != null)
			unregisterMBean();
		Optional.ofNullable(context).ifPresent(Context::close);
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
