package rainbow.core.bundle;

/**
 * Bundle 生命周期监视扩展点
 * 
 * @author lijinghui
 * 
 */
public interface BundleListener {

    /**
     * 某个Bundle开始工作
     * 
     * @param id bundle的ID
     */
    public void bundleStarted(String id);

    /**
     * 某个bundle准备停止工作
     * 
     * @param id bundle的ID
     */
    public void bundleStopping(String id);

    /**
     * 某个bundle停止了工作
     * 
     * @param id bundle的ID
     */
    public void bundleStop(String id);
}
