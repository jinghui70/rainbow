package rainbow.core.extension;

import static rainbow.core.util.Preconditions.checkNotNull;
import static rainbow.core.util.Preconditions.checkState;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

import rainbow.core.util.Utils;

/**
 * 扩展点及扩展注册表
 * 
 * @author lijinghui
 * 
 */
public abstract class ExtensionRegistry {

	private static Logger logger = LoggerFactory.getLogger(ExtensionRegistry.class);

	private static Map<Class<?>, ExtensionPoint> pointMap = new MapMaker().concurrencyLevel(1).makeMap();

	/**
	 * 注册一个扩展点
	 * 
	 * @param bundle
	 * @param clazz
	 */
	public static void registerExtensionPoint(String bundle, Class<?> clazz) {
		checkState(!pointMap.containsKey(clazz), "duplicated extension point {} ", clazz.getName());
		logger.info("register extension point [{}]", clazz.getSimpleName());
		ExtensionPoint point = new ExtensionPoint(bundle, clazz);
		pointMap.put(clazz, point);
	}

	/**
	 * 注销一个扩展点
	 * 
	 * @param clazz
	 */
	public static void unregisterExtensionPoint(Class<?> clazz) {
		logger.info("try to unregister extension point [{}]", clazz.getName());
		if (pointMap.remove(clazz) == null)
			logger.warn("extension point [{}] not exist", clazz.getName());
		else
			logger.info("extension point [{}] unregistered", clazz.getName());
	}

	/**
	 * 注册一个扩展
	 * 
	 * @param bundle
	 * @param clazz  扩展点类
	 * @param name   扩展名，可以为空，自动取名
	 * @param object 扩展对象
	 * @return
	 */
	public static Extension registerExtension(String bundle, Class<?> clazz, String name, Object object) {
		ExtensionPoint point = getExtensionPoint(clazz);
		Extension extension = point.addExtension(bundle, name, object);
		logger.info("register extension [{}]:  [{}]", clazz.getSimpleName(), extension.getName());
		return extension;
	}

	/**
	 * 注册一个扩展
	 * 
	 * @param bundle
	 * @param clazz
	 * @param object
	 * @return
	 */
	public static Extension registerExtension(String bundle, Class<?> clazz, Object object) {
		return registerExtension(bundle, clazz, null, object);
	}

	/**
	 * 注销一个扩展
	 * 
	 * @param extension
	 */
	public static void unregisterExtension(Extension extension) {
		ExtensionPoint point = extension.getExtensionPoint();
		point.removeExtension(extension);
		logger.info("unregister extension object {}", extension.getName());
	}

	/**
	 * 判断是否有指定扩展点
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean hasExtensionPoint(Class<?> clazz) {
		return pointMap.containsKey(clazz);
	}

	/**
	 * 获取一个扩展点
	 * 
	 * @param clazz 扩展点接口
	 * @return
	 * @throws NullPointerException 不存在该接口的扩展点时
	 */
	public static ExtensionPoint getExtensionPoint(Class<?> clazz) {
		ExtensionPoint point = pointMap.get(clazz);
		checkNotNull(point, "Extension Point [{}] not registered", clazz.getName());
		return point;
	}

	/**
	 * 获得一个扩展点的所有扩展
	 * 
	 * @param clazz
	 * @return
	 */
	public static Collection<Extension> getExtensions(Class<?> clazz) {
		ExtensionPoint point = getExtensionPoint(clazz);
		return point.getExtensions();
	}

	/**
	 * 获得一个扩展点的所有扩展名
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<String> getExtensionNames(Class<?> clazz) {
		ExtensionPoint point = getExtensionPoint(clazz);
		return Utils.transform(point.getExtensions(), e -> e.getName());
	}

	/**
	 * 获得一个扩展点的所有扩展对象
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getExtensionObjects(Class<T> clazz) {
		ExtensionPoint point = getExtensionPoint(clazz);
		Collection<Extension> extensions = point.getExtensions();
		return extensions.stream().map(e->(T) e.getObject()).collect(Collectors.toList());
	}

	/**
	 * 获取一个扩展点的指定名字的扩展实例对象
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getExtensionObject(Class<T> clazz, String name) {
		ExtensionPoint point = getExtensionPoint(clazz);
		return (T) point.getExtensionObject(name);
	}
}
