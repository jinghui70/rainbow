package rainbow.core.platform;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

import rainbow.core.bundle.BundleClassLoader;
import rainbow.core.bundle.BundleException;
import rainbow.core.bundle.Resource;
import rainbow.core.bundle.ResourceProcessor;

public class ProjectClassLoader extends BundleClassLoader {

	private Path root;

	public ProjectClassLoader(Path file) throws IOException {
		super();
		root = file;
	}

	@Override
	public String getFileName() {
		return root.getFileName().toString();
	}

	@Override
	public Resource getLocalResource(String resourceName) {
		Path file = root.resolve(resourceName);
		if (!Files.exists(file))
			return null;
		return new FileResource(resourceName, file);
	}

	@Override
	public void procResource(ResourceProcessor processor) throws BundleException {
		procDirResource(root, processor);
	}

	private void procDirResource(Path r, ResourceProcessor processor) throws BundleException {
		try {
			Iterator<Path> i = Files.list(r).iterator();
			while (i.hasNext()) {
				Path p = i.next();
				if (Files.isDirectory(p)) {
					procDirResource(p, processor);
				} else {
					FileResource res = new FileResource(root.relativize(p).toString(), p);
					processor.processResource(this, res);
				}
			}
		} catch (IOException e) {
			throw new BundleException("procDirResource error", e);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String path = name.replace('.', '/').concat(".class");
		Path entry = root.resolve(path);
		if (Files.exists(entry) && Files.isRegularFile(entry))
			return defineClass(name, new FileResource(path, entry));
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
