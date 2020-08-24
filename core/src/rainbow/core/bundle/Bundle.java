package rainbow.core.bundle;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rainbow.core.util.Utils;

public class Bundle {

	/**
	 * Bundle的配置信息
	 */
	private BundleData data;

	private BundleState state = BundleState.FOUND;

	private BundleClassLoader classLoader;

	/**
	 * 所有的前辈
	 */
	private List<Bundle> ancestors = Collections.emptyList();

	/**
	 * 父辈
	 */
	private List<Bundle> parents = Collections.emptyList();

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

	public List<Bundle> getAncestors() {
		if (ancestors == null)
			return Collections.emptyList();
		return ancestors;
	}

	void setAncestors(List<Bundle> ancestors) {
		this.ancestors = ancestors;
	}

	public List<Bundle> getParents() {
		return parents;
	}

	void setParents(List<Bundle> parents) {
		if (parents == null)
			this.parents = Collections.emptyList();
		else
			this.parents = parents;
	}

	void setActivator(BundleActivator activator) {
		this.activator = activator;
	}

	public BundleActivator getActivator() {
		return activator;
	}

	public void destroy() {
		setParents(null);
		setAncestors(null);
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
		if (!Utils.isNullOrEmpty(data.getRequires()))
			data.getRequires().forEach(result::add);
		return result;
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
}
