package rainbow.core.platform;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import rainbow.core.bundle.Bundle;
import rainbow.core.bundle.BundleClassLoader;
import rainbow.core.bundle.BundleData;
import rainbow.core.util.Utils;
import rainbow.core.util.json.JSON;

public class ProjectBundleLoader extends JarBundleLoader {

	public ProjectBundleLoader() {
		super(Paths.get("bundle"));
	}

	@Override
	public List<Bundle> loadBundle(Set<String> bundles) throws IOException {
		List<Bundle> result = super.loadBundle(bundles);
		Path dir = Paths.get("../");
		bundles.addAll(Utils.transform(result, b -> b.getId()));

		Iterator<Path> i = Files.list(dir).filter(f -> Files.isDirectory(f)).filter(f -> !f.startsWith(".")).iterator();
		while (i.hasNext()) {
			Path root = i.next().resolve("bin");
			Path dataFile = root.resolve("bundle.json");
			if (Files.exists(dataFile)) {
				try (InputStream is = Files.newInputStream(dataFile)) {
					BundleData data = JSON.parseObject(is, BundleData.class);
					if (bundles.contains(data.getId())) {
						logger.warn("duplicated bundle id: {}", data.getId());
					} else if (Objects.equals(data.getId(), root.getParent().getFileName().toString())) {
						BundleClassLoader classLoader = new ProjectClassLoader(root);
						result.add(new Bundle(data, classLoader));
						bundles.add(data.getId());
						logger.debug("find new bundle: {}", data.getId());
					} else
						logger.error("project name not match with bundle id: {}", data.getId());
				}
			}
		}
		return result;
	}

}
