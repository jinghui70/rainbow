package rainbow.core.platform;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import rainbow.core.bundle.Bundle;

public class BundleAncestor {

	private Map<Bundle, Integer> map = Maps.newHashMap();

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
	public ImmutableList<Bundle> getParents() {
		ImmutableList.Builder<Bundle> builder = ImmutableList.builder();
		for (Entry<Bundle, Integer> entry : map.entrySet()) {
			if (entry.getValue() == 0)
				builder.add(entry.getKey());
		}
		return builder.build();
	}

	/**
	 * 返回所有祖辈
	 * 
	 * @return
	 */
	public ImmutableList<Bundle> getAncestors() {
		Comparator<Bundle> comparator = new Comparator<Bundle>() {
			@Override
			public int compare(Bundle o1, Bundle o2) {
				// o2在前表示从大到小排序
				return map.get(o2).compareTo(map.get(o1));
			}
		};
		return Ordering.from(comparator).immutableSortedCopy(map.keySet());
	}
}
