package rainbow.core.bundle;

import java.util.stream.Stream;

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
     * @param filter
     *            过滤条件
     * @return
     */
    public Stream<Bundle> getBundles();

    /**
     * 返回指定Bundle
     * 
     * @param id
     * @return
     */
    public Bundle get(String id);

    /**
     * 刷新Bundle及状态列表
     */
    public void refresh();

    /**
     * 初始化启动的bundle
     * 
     */
    public void initStart();

    /**
     * 停止所有的bundle
     */
    public void stopAll();

    /**
     * 启动指定Bundle
     * 
     * @param id
     * @return
     * @throws BundleException
     */
    public boolean startBundle(String id) throws BundleException;

    /**
     * 停止指定Bundle
     * 
     * @param id
     */
    public void stopBundle(String id) throws BundleException;

    /**
     * 卸载指定bundle
     * 
     * @param id
     */
    public void uninstallBundle(String id);

}
