package rainbow.core.platform;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import rainbow.core.bundle.Resource;

public class FileResource implements Resource {

    private Path file;
    
    private String name;

    public FileResource(String name, Path file) {
    	this.name = name;
        this.file = file;
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
