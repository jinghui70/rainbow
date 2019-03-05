package rainbow.core.bundle;



/**
 * 资源处理器
 * 
 * @author lijinghui
 * 
 */
public interface ResourceProcessor {

	/**
	 * 处理一个资源
	 * 
	 * @param classLoader
	 * @param resource
	 *            要处理的抽象资源
	 */
	void processResource(BundleClassLoader classLoader, Resource resource) throws BundleException;

}

