package rainbow.core.ant;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import rainbow.core.bundle.Jar;

public class LibraryDownloader {

	private Path destPath;

	private Path libPath;

	private String repository;

	public LibraryDownloader(Path libPath) {
		this.libPath = libPath;
		ensurePath(libPath);
		destPath = libPath;
	}

	private void ensurePath(Path path) {
		try {
			if (Files.notExists(path))
				Files.createDirectories(path);
		} catch (IOException e) {
			throw new RuntimeException("can not create dir:" + path);
		}
		if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
			throw new RuntimeException(path + " is not a directory");
	}

	public void setDest(String dest) {
		destPath = libPath.resolve(dest);
		ensurePath(destPath);
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public void downloadJar(Jar j) {
		String repo = j.getRepository();
		if (repo == null)
			repo = this.repository;

		Path file = destPath.resolve(j.fileName());
		doGet(j.url(repo), file);

		if (j.isSource()) {
			Path sourcePath = libPath.resolve("src");
			ensurePath(sourcePath);

			file = sourcePath.resolve(j.sourceFileName());
			doGet(j.sourceUrl(repo), file);
		}
	}

	private void doGet(String url, Path file) {
		if (Files.exists(file)) {
			System.out.println("skipping exist file: " + file);
			return;
		}
		try {
			System.out.println("downloading " + url);
			URL source = new URL(url);
			ReadableByteChannel readableByteChannel = Channels.newChannel(source.openStream());
			try (FileOutputStream fileOutputStream = new FileOutputStream(file.toFile())) {
				fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
