package rainbow.core.ant;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import rainbow.core.bundle.Bundle;
import rainbow.core.platform.BundleLoader;
import rainbow.core.platform.ProjectBundleLoader;
import rainbow.core.platform.ProjectClassLoader;
import rainbow.core.util.dag.Dag;
import rainbow.core.util.dag.DagImpl;

public class BundleAware {

	public static List<Bundle> loadBundle() {
		BundleLoader loader = new ProjectBundleLoader();
		try {
			return loader.loadBundle(new HashSet<String>());
		} catch (IOException e) {
			throw new RuntimeException("load bundle failed", e);
		}

	}

	public static Dag<Bundle> loadBundleDag(List<Bundle> bundles) {
		Map<String, Bundle> map = bundles.stream().collect(Collectors.toMap(Bundle::getId, Function.identity()));
		Dag<Bundle> dag = new DagImpl<Bundle>();
		for (Bundle bundle : bundles) {
			String father = bundle.getData().getFather();
			if (father != null) {
				dag.addEdge(map.get(father), bundle);
			}
			for (String pid : bundle.getData().getRequires()) {
				Bundle parent = checkNotNull(map.get(pid), "{} requires {} not exist", bundle.getId(), pid);
				dag.addEdge(parent, bundle);
			}
		}
		return dag;
	}

	public static Dag<Bundle> loadBundleDag() {
		return loadBundleDag(loadBundle());
	}

	public static void main(String[] args) {
		String bundles = loadBundleDag(loadBundle()).dfsList().stream() //
				.filter(b -> b.getClassLoader() instanceof ProjectClassLoader) //
				.map(Bundle::getId) //
				.collect(Collectors.joining(","));
		System.out.println(bundles);
	}
}
