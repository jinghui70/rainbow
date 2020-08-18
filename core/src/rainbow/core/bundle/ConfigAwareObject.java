package rainbow.core.bundle;

/**
 * 需要访问配置文件的对象基类
 * 
 * @author lijinghui
 * 
 */
public class ConfigAwareObject implements ConfigAware {

	protected BundleConfig bundleConfig;

	@Override
	public void setBundleConfig(BundleConfig bundleConfig) {
		this.bundleConfig = bundleConfig;
	}
}
