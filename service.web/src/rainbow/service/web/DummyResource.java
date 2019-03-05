package rainbow.service.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;

import org.eclipse.jetty.util.resource.Resource;

public class DummyResource extends Resource {

	private String name;
	
	public DummyResource(String name) {
	}

	@Override
	public boolean isContainedIn(Resource r) throws MalformedURLException {
		return false;
	}

	@Override
	public void close() {
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public boolean isDirectory() {
		return name.endsWith("/");
	}

	@Override
	public long lastModified() {
		return 0;
	}

	@Override
	public long length() {
		return 0;
	}

	@Override
	public URL getURL() {
		try {
			return new URL("http://localhost/" + name);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public File getFile() throws IOException {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public ReadableByteChannel getReadableByteChannel() throws IOException {
		return null;
	}

	@Override
	public boolean delete() throws SecurityException {
		return false;
	}

	@Override
	public boolean renameTo(Resource dest) throws SecurityException {
		return false;
	}

	@Override
	public String[] list() {
		return new String[0];
	}

	@Override
	public Resource addPath(String path) throws IOException, MalformedURLException {
		return new DummyResource(name + '/' + path);
	}

}
