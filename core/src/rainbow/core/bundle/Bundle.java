package rainbow.core.bundle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import rainbow.core.util.Utils;

public class Bundle implements Comparable<Bundle> {

	/**
	 * Bundle的配置信息
	 */
	private BundleData data;

	private BundleState state = BundleState.FOUND;

	private BundleClassLoader classLoader;

	private Set<Bundle> ancestors;

	/**
	 * Bundle入口类
	 */
	BundleActivator activator;

	public Bundle(BundleData data, BundleClassLoader classLoader) {
		this.data = data;
		this.classLoader = classLoader;
		this.classLoader.setBundle(this);
	}

	public BundleClassLoader getClassLoader() {
		return classLoader;
	}

	public BundleData getData() {
		return data;
	}

	public String getId() {
		return data.getId();
	}

	public String getDesc() {
		return data.getDesc();
	}

	public BundleState getState() {
		return state;
	}

	public void setState(BundleState state) {
		this.state = state;
	}

	void setActivator(BundleActivator activator) {
		this.activator = activator;
	}

	public BundleActivator getActivator() {
		return activator;
	}

	public void destroy() {
		classLoader.destroy();
		classLoader = null;
	}

	@Override
	public String toString() {
		return getId();
	}

	public boolean hasFather() {
		return Utils.hasContent(data.getFather());
	}

	public boolean isFather(String id) {
		return data.getFather().equals(id);
	}

	public Collection<String> getParentIds() {
		Set<String> result = new HashSet<String>();
		if (hasFather())
			result.add(data.getFather());
		if (Utils.hasContent(data.getRequires()))
			result.addAll(data.getRequires());
		return result;
	}

	public Set<Bundle> getAncestors() {
		return ancestors;
	}

	public void setAncestors(Set<Bundle> ancestors) {
		this.ancestors = ancestors;
	}

	@Override
	public int hashCode() {
		return data.getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bundle other = (Bundle) obj;
		return getId().equals(other.getId());
	}

	@Override
	public int compareTo(Bundle o) {
		return getId().compareTo(o.getId());
	}
}
