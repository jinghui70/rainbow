package rainbow.core.platform;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import rainbow.core.bundle.Bundle;

public interface BundleLoader {

	/**
	 * 加载新的bundle
	 * 
	 * @param bundles 已发现的bundle列表
	 * @return 新发现的bundle列表
	 * @throws IOException
	 */
	public List<Bundle> loadBundle(Set<String> bundles) throws IOException;

}
