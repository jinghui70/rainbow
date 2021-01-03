package rainbow.core.ant;

import java.util.Collections;
import java.util.List;

import rainbow.core.bundle.Jar;

/**
 * Jar依赖描述文件
 * 
 * @author lijinghui
 *
 */
public class LibraryFile {

	public String repository;

	private List<Jar> dev;

	private List<Jar> runtime;

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public List<Jar> getDev() {
		if (dev == null)
			return Collections.emptyList();
		return dev;
	}

	public void setDev(List<Jar> dev) {
		this.dev = dev;
	}

	public List<Jar> getRuntime() {
		if (runtime == null)
			return Collections.emptyList();
		return runtime;
	}

	public void setRuntime(List<Jar> runtime) {
		this.runtime = runtime;
	}

}
