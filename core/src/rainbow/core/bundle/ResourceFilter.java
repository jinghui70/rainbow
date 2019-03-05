package rainbow.core.bundle;

/**
 * 资源过滤器
 * 
 * @author lijinghui
 * 
 */
public interface ResourceFilter {

	/**
	 * 测试指定抽象资源是否应该包含在某个资源列表中
	 * 
	 * @param resource
	 *            要测试的抽象资源
	 * @return
	 */
	boolean accept(Resource resource);

}
