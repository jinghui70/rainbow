package rainbow.core.bundle;

import rainbow.core.platform.BundleConfig;

/**
 * 需要访问配置文件的对象基类
 * 
 * @author lijinghui
 * 
 */
public class ConfigAwareObject implements ConfigAware {

	protected BundleConfig bundleConfig;

	@Override
	public void setbundleConfig(BundleConfig bundleConfig) {
		this.bundleConfig = bundleConfig;
	}
}
