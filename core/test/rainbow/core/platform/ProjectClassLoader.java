package rainbow.core.platform;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import rainbow.core.bundle.BundleClassLoader;
import rainbow.core.bundle.Resource;
import rainbow.core.bundle.ResourceProcessor;

public class ProjectClassLoader extends BundleClassLoader {

	private Path root;

	public ProjectClassLoader(Path file) throws IOException {
		super();
		root = file;
	}

	@Override
	public Resource getLocalResource(String resourceName) {
		Path file = root.resolve(resourceName);
		if (!Files.exists(file))
			return null;
		return new FileResource(file, resourceName);
	}

	@Override
	public void procResource(ResourceProcessor processor) {
		try {
			Files.walk(root).forEach(path->processor.processResource(this, new FileResource(path, root)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String pathName = name.replace('.', '/').concat(".class");
		Path entry = root.resolve(pathName);
		if (Files.exists(entry) && Files.isRegularFile(entry))
			return defineClass(name, new FileResource(entry, pathName));
		throw new ClassNotFoundException(name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		Resource res = Objects.requireNonNull(getLocalResource(name));
		try {
			return res.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
}
