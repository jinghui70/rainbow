package rainbow.core.platform;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import rainbow.core.bundle.Bundle;

public class BundleAncestor {

	private Map<Bundle, Integer> map = new HashMap<Bundle, Integer>();

	public boolean unaware(Bundle bundle) {
		return !map.containsKey(bundle);
	}

	public void addParent(Bundle bundle) {
		addParent(bundle, 0);
	}

	private void addParent(Bundle bundle, int level) {
		Integer old = map.get(bundle);
		if (old == null)
			map.put(bundle, level);
		else if (old > level)
			return;
		else
			map.put(bundle, level);
		level++;
		for (Bundle parent : bundle.getParents()) {
			addParent(parent, level);
		}
	}

	/**
	 * 返回直接父辈
	 * 
	 * @return
	 */
	public List<Bundle> getParents() {
		return map.entrySet().stream().filter(e -> e.getValue() == 0).map(Entry::getKey).collect(Collectors.toList());
	}

	/**
	 * 返回所有祖辈
	 * 
	 * @return
	 */
	public List<Bundle> getAncestors() {
		Comparator<Bundle> comparator = new Comparator<Bundle>() {
			@Override
			public int compare(Bundle o1, Bundle o2) {
				// o2在前表示从大到小排序
				return map.get(o2).compareTo(map.get(o1));
			}
		};
		return map.keySet().stream().sorted(comparator).collect(Collectors.toList());
	}
}
