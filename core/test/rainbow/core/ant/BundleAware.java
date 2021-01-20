package rainbow.core.ant;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import rainbow.core.util.dag.Dag;
import rainbow.core.util.dag.DagImpl;

public class BundleAware {

	public static List<BundleDataX> loadBundle() throws IOException {
		List<BundleDataX> result = new ArrayList<>();
		Path bundleDir = Paths.get("bundle").toAbsolutePath().normalize();
		System.out.println("loading bundles under " + bundleDir);
		if (Files.exists(bundleDir) && Files.isDirectory(bundleDir)) {
			Files.list(bundleDir)//
					.filter(f -> f.getFileName().toString().endsWith(".jar")) //
					.map(BundleDataX::parseJar)//
					.forEach(result::add);
		}

		Path projectDir = Paths.get("..").toAbsolutePath().normalize();
		System.out.println("loading projects under " + projectDir);
		Files.list(projectDir) //
				.filter(Files::isDirectory) //
				.map(BundleDataX::loadProject) //
				.filter(Objects::nonNull) //
				.forEach(result::add);
		return result;
	}

	private static Dag<BundleDataX> loadBundleDag(List<BundleDataX> bundles) {
		Map<String, BundleDataX> map = bundles.stream()
				.collect(Collectors.toMap(BundleDataX::getId, Function.identity()));
		Dag<BundleDataX> dag = new DagImpl<>();
		for (BundleDataX bundle : bundles) {
			String father = bundle.getFather();
			if (father != null) {
				dag.addEdge(map.get(father), bundle);
			}
			for (String pid : bundle.getRequires()) {
				BundleDataX parent = checkNotNull(map.get(pid), "{} requires {} not exist", bundle.getId(), pid);
				dag.addEdge(parent, bundle);
			}
		}
		return dag;
	}

	public static Dag<BundleDataX> loadBundleDag() throws IOException {
		return loadBundleDag(loadBundle());
	}

	public static void main(String[] args) throws IOException {
		String bundles = loadBundleDag().dfsList().stream() //
				.filter(b -> b.isDev()) //
				.map(BundleDataX::getId) //
				.collect(Collectors.joining(","));
		Properties p = new Properties();
		p.put("BUNDLES", bundles);
		Path file = Paths.get("..", "dist").resolve("bundles.properties");
		try (BufferedWriter writer = Files.newBufferedWriter(file)) {
			p.store(writer, "");
		}
	}
}
