package rainbow.core.ant;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import rainbow.core.bundle.BundleData;
import rainbow.core.util.Utils;
import rainbow.core.util.json.JSON;

public class BundleDataX extends BundleData implements Comparable<BundleDataX> {

	private boolean dev;

	public boolean isDev() {
		return dev;
	}

	public void setDev(boolean dev) {
		this.dev = dev;
	}

	@Override
	public int compareTo(BundleDataX o) {
		return getId().compareTo(o.getId());
	}

	public static BundleDataX parseJar(Path file) {
		System.out.println("parse jar: " + file);
		try (JarFile jar = new JarFile(file.toFile())) {
			ZipEntry entry = jar.getEntry("bundle.json");
			try (InputStream is = jar.getInputStream(entry)) {
				return JSON.parseObject(is, BundleDataX.class);
			}
		} catch (IOException e) {
			throw new RuntimeException(Utils.format("read {} failed", file.toString()), e);
		}
	}

	public static BundleDataX loadProject(Path root) {
		String projectName = root.getFileName().toString();
		if (projectName.startsWith("."))
			return null;
		switch (projectName) {
		case "core":
		case "bootstrap":
		case "dist":
		case "rainbow":
			return null;
		}
		Path file = root.resolve("src").resolve("bundle.json");
		if (!Files.exists(file))
			return null;
		BundleDataX result = JSON.parseObject(file, BundleDataX.class);
		result.setDev(true);
		if (Objects.equals(projectName, result.getId())) {
			System.out.println("found bundle project " + projectName);
			return result;
		}
		throw new RuntimeException(Utils.format("project name not match with bundle id:{}", result.getId()));
	}

}
