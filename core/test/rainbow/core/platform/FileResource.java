package rainbow.core.platform;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import rainbow.core.bundle.Resource;

public class FileResource implements Resource {

	private Path file;

	private String name;

	public FileResource(Path file, Path root) {
		this.file = file;
		Path relative = root.relativize(file);
		Iterator<Path> i = relative.iterator();
		Stream.generate(i::next).limit(relative.getNameCount()).map(Object::toString)
				.collect(Collectors.joining("/"));
	}

	public FileResource(Path file, String name) {
		this.file = file;
		this.name = name;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return Files.newInputStream(file);
	}

	public Path getFile() {
		return file;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getSize() throws IOException {
		return Files.size(file);
	}
}
