package rainbow.core.bundle;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BundleClassLoader extends ClassLoader {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected Bundle bundle;
	
	public BundleClassLoader() throws IOException {
		super(Thread.currentThread().getContextClassLoader());
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (name.startsWith(bundle.getId())) {
			if (name.contains("internal") || name.endsWith(".Activator"))
				return loadLocalClass(name);
		}
		for (Bundle parent : bundle.getAncestors()) {
			try {
				return parent.getClassLoader().loadLocalClass(name);
			} catch (ClassNotFoundException e) {
				// not found in parent
			}
		}
		try {
			return loadLocalClass(name);
		} catch (ClassNotFoundException e) {
		}
		throw new ClassNotFoundException(name);
	}

	public void destroy() {
	}

	private Class<?> loadLocalClass(String name) throws ClassNotFoundException {
		return super.loadClass(name);
	}

	public abstract Resource getLocalResource(String name);

	/**
	 * 对所有非目录的资源进行一个特定的处理
	 * 
	 * @param processor
	 */
	public abstract void procResource(ResourceProcessor processor);

	protected Class<?> defineClass(String name, Resource res) throws ClassNotFoundException {
		int i = name.lastIndexOf('.');
		if (i != -1) {
			String pkgname = name.substring(0, i);
			// Check if package already loaded.
			Package pkg = getPackage(pkgname);
			if (pkg == null) {
				definePackage(pkgname, null, null, null, null, null, null, null);
			}
		}
		byte[] buf = null;
		try(InputStream is = res.getInputStream()) {
			buf = new byte[(int) res.getSize()];
			new DataInputStream(is).readFully(buf);
		} catch (IOException e) {
			logger.error("find class [{}] failed", name, e);
			throw new ClassNotFoundException(name, e);
		}
		return defineClass(name, buf, 0, buf.length);
	}

}