package rainbow.core.platform;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import rainbow.core.bundle.Bundle;
import rainbow.core.bundle.BundleData;
import rainbow.core.bundle.Resource;
import rainbow.core.util.Utils;

public class JarBundleLoader implements BundleLoader {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public List<Bundle> loadBundle(Set<String> bundles) throws IOException {
		Path bundleDir = getBundleDir();
		if (!Files.exists(bundleDir) || !Files.isDirectory(bundleDir)) {
			logger.error("bundle directory not exists: {}", bundleDir.toAbsolutePath().toString());
			return new ArrayList<Bundle>();
		}
		List<Path> bundleFiles = Files.list(bundleDir).filter(f -> f.getFileName().toString().endsWith(".jar"))
				.collect(Collectors.toList());
		if (bundleFiles.isEmpty())
			return new ArrayList<Bundle>();

		Yaml yaml = new Yaml(new Constructor(BundleData.class));
		return Utils.transform(bundleFiles, new Function<Path, Bundle>() {
			@Override
			public Bundle apply(Path input) {
				JarClassLoader classLoader = null;
				try {
					try {
						classLoader = new JarClassLoader(input);
					} catch (IOException e) {
						logger.warn("load file failed: {}", input, e);
						return null;
					}
					Resource r = classLoader.getLocalResource("bundle.yaml");
					if (r == null) {
						logger.info("{} is not a bundle", input);
						throw new RuntimeException();
					}
					BundleData data = null;
					try (InputStream is = r.getInputStream()) {
						data = yaml.load(is);
					}
					if (bundles.contains(data.getId())) {
						logger.error("duplicated bundle {} found: {}", data.getId(), input);
						throw new RuntimeException();
					}
					bundles.add(data.getId());
					return new Bundle(data, classLoader);
				} catch (Throwable e) {
					classLoader.destroy();
					return null;
				}
			}
		});
	}

	protected Path getBundleDir() {
		return Platform.getHome().resolve("bundle");
	}

}