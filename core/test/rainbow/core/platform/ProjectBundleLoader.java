package rainbow.core.platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import rainbow.core.bundle.Bundle;
import rainbow.core.bundle.BundleClassLoader;
import rainbow.core.bundle.BundleData;
import rainbow.core.util.Utils;

public class ProjectBundleLoader extends JarBundleLoader {

	@Override
	public List<Bundle> loadBundle(List<Bundle> bundles) throws IOException {
		List<Bundle> result = super.loadBundle(bundles);
		Path dir = Paths.get("../");

		Set<String> idSet = bundles.stream().map(b -> b.getId()).collect(Collectors.toSet());
		idSet.addAll(Utils.transform(result, b->b.getId()));

		Iterator<Path> i = Files.list(dir).filter(f -> Files.isDirectory(f)).filter(f -> !f.startsWith(".")).iterator();
		while (i.hasNext()) {
			Path root = i.next().resolve("bin");
			Path dataFile = root.resolve("bundle.xml");
			if (Files.exists(dataFile)) {
				try {
					BundleData data = binder.unmarshal(dataFile);
					if (idSet.contains(data.getId())) {
						logger.warn("duplicated bundle id: {}", data.getId());
					} else if (Objects.equals(data.getId(), root.getParent().getFileName().toString())) {
						BundleClassLoader classLoader = new ProjectClassLoader(root);
						result.add(new Bundle(data, classLoader));
						idSet.add(data.getId());
						logger.debug("find new bundle: {}", data.getId());
					} else 
						logger.error("project name not match with bundle id: {}", data.getId());
				} catch (JAXBException e) {
					logger.error("bad bundle.xml in {}", root);
				}
			}
		}
		return result;
	}

}
