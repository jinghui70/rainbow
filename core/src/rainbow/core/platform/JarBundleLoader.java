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

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.bundle.Bundle;
import rainbow.core.bundle.BundleData;
import rainbow.core.bundle.Resource;
import rainbow.core.util.Utils;

public class JarBundleLoader implements BundleLoader {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public List<Bundle> loadBundle(List<Bundle> bundles) throws IOException {
		Path bundleDir = getBundleDir();
		if (!Files.exists(bundleDir) || !Files.isDirectory(bundleDir)) {
			logger.error("bundle directory [{}] not exists", bundleDir.toAbsolutePath().toString());
			return new ArrayList<Bundle>();
		}
		Set<String> names = bundles.stream().map(b -> b.getFileName()).collect(Collectors.toSet());
		Set<String> idSet = bundles.stream().map(b -> b.getId()).collect(Collectors.toSet());
		List<Path> bundleFiles = Files.list(bundleDir).filter(f -> f.endsWith(".jar"))
				.filter(f -> !names.contains(f.getFileName().toString())).collect(Collectors.toList());
		if (bundleFiles.isEmpty())
			return new ArrayList<Bundle>();

		return Utils.transform(bundleFiles, new Function<Path, Bundle>() {
			@Override
			public Bundle apply(Path input) {
				JarClassLoader classLoader = null;
				try {
					try {
						classLoader = new JarClassLoader(input);
					} catch (IOException e) {
						logger.warn("load file [{}] failed", input, e);
						return null;
					}
					Resource r = classLoader.getLocalResource("bundle.xml");
					if (r == null) {
						logger.info("[{}] is not a bundle", input);
						throw new RuntimeException();
					}
					BundleData data = null;
					try (InputStream is = r.getInputStream()) {
						data = binder.unmarshal(is);
					} catch (JAXBException | IOException e) {
						logger.error("read bundle.xml of [{}] faild", input, e);
						throw new RuntimeException();
					}
					if (idSet.contains(data.getId())) {
						logger.warn("duplicated bundle [{}] found: {}", data.getId(), input);
						throw new RuntimeException();
					}
					idSet.add(data.getId());
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