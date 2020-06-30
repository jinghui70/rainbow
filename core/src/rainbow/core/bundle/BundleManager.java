package rainbow.core.bundle;

import java.util.List;
import java.util.Optional;

/**
 * bundle管理接口
 * 
 * @author lijinghui
 * 
 */
public interface BundleManager {

	/**
	 * 返回Bundle列表
	 * 
	 * @param filter 过滤条件
	 * @return
	 */
	List<Bundle> getBundles();

	/**
	 * 返回指定Bundle
	 * 
	 * @param id
	 * @return
	 */
	Optional<Bundle> get(String id);

	/**
	 * 刷新Bundle及状态列表
	 */
	void refresh();

	/**
	 * 初始化启动的bundle
	 * 
	 */
	void initStart();

	/**
	 * 停止所有的bundle
	 */
	void stopAll();

	/**
	 * 启动指定Bundle
	 * 
	 * @param id
	 * @return
	 * @throws BundleException
	 */
	boolean startBundle(String id) throws BundleException;

	/**
	 * 停止指定Bundle
	 * 
	 * @param id
	 */
	void stopBundle(String id) throws BundleException;

	/**
	 * 卸载指定bundle
	 * 
	 * @param id
	 */
	void uninstallBundle(String id);

}
