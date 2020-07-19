package rainbow.core.bundle;

import rainbow.core.platform.BundleConfig;

/**
 * 需要访问配置文件的对象接口
 * 
 * @author lijinghui
 * 
 */
public interface ConfigAware {

	void setbundleConfig(BundleConfig bundleConfig);

}
