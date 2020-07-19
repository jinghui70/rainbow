package rainbow.core.util.ioc;

import rainbow.core.platform.BundleConfig;

/**
 * 需要访问配置文件的对象基类
 * 
 * @author lijinghui
 * 
 */
public class ConfigAwareObject {

	protected BundleConfig bundleConfig;

	@Inject
	public void setbundleConfig(BundleConfig bundleConfig) {
		this.bundleConfig = bundleConfig;
	}
}
