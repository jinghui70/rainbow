package rainbow.core.platform;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.bundle.Bundle;
import rainbow.core.bundle.BundleData;
import rainbow.core.bundle.Resource;
import rainbow.core.util.Utils;
import rainbow.core.util.json.JSON;

public class JarBundleLoader implements BundleLoader {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private Path bundleDir;

	public JarBundleLoader(Path bundleDir) {
		this.bundleDir = bundleDir;
	}

	@Override
	public List<Bundle> loadBundle(Set<String> bundles) throws IOException {
		if (!Files.exists(bundleDir) || !Files.isDirectory(bundleDir)) {
			logger.error("bundle directory not exists: {}", bundleDir.toAbsolutePath().toString());
			return new ArrayList<Bundle>();
		}
		List<Path> bundleFiles = Files.list(bundleDir).filter(f -> f.getFileName().toString().endsWith(".jar"))
				.collect(Collectors.toList());
		if (bundleFiles.isEmpty())
			return new ArrayList<Bundle>();

		return Utils.transform(bundleFiles, file -> {
			JarClassLoader classLoader = null;
			try {
				try {
					classLoader = new JarClassLoader(file);
				} catch (IOException e) {
					logger.warn("load file failed: {}", file, e);
					return null;
				}
				Resource r = classLoader.getLocalResource("bundle.json");
				if (r == null) {
					logger.info("{} is not a bundle", file);
					throw new RuntimeException();
				}
				BundleData data = null;
				try (InputStream is = r.getInputStream()) {
					data = JSON.parseObject(is, BundleData.class);
				}
				if (bundles.contains(data.getId())) {
					logger.error("duplicated bundle {} found: {}", data.getId(), file);
					throw new RuntimeException();
				}
				bundles.add(data.getId());
				return new Bundle(data, classLoader);
			} catch (Throwable e) {
				classLoader.destroy();
				return null;
			}
		});
	}

}