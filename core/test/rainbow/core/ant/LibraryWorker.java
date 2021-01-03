package rainbow.core.ant;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.bundle.Bundle;
import rainbow.core.bundle.Jar;
import rainbow.core.util.dag.Dag;
import rainbow.core.util.json.JSON;

public class LibraryWorker {

	private Logger logger = LoggerFactory.getLogger(LibraryWorker.class);

	private LibraryFile libraryFile;

	private LibraryFile readLibraryFile() {
		Path file = Paths.get("library.json");
		if (!Files.exists(file)) {
			LibraryFile result = new LibraryFile();
			result.setRepository("https://maven.aliyun.com/repository/public");
			return result;
		}
		try (InputStream is = Files.newInputStream(file)) {
			return JSON.parseObject(is, LibraryFile.class);
		} catch (IOException e) {
			logger.error("read library.json failed");
			throw new RuntimeException(e);
		}
	}

	private LibraryFile getLibraryFile() {
		if (libraryFile == null)
			libraryFile = readLibraryFile();
		return libraryFile;
	}

	/**
	 * 生成library.json
	 * 
	 * @throws IOException
	 */
	public void generateLibraryFile() throws IOException {
		Dag<Bundle> dag = BundleAware.loadBundleDag();
		LibraryFileMaker maker = new LibraryFileMaker();
		maker.make(dag);
	}

	/**
	 * 根据library.json配置下载
	 * 
	 * @throws IOException
	 */
	public void download() {
		logger.info("downloading dependencies...");
		LibraryFile file = getLibraryFile();
		Path libPath = Paths.get("lib");
		LibraryDownloader downloader = new LibraryDownloader(libPath);
		downloader.setRepository(file.getRepository());

		Set<String> runtime = new HashSet<String>();
		Set<String> source = new HashSet<String>();
		Set<String> dev = new HashSet<String>();

		for (Jar j : file.getRuntime()) {
			logger.info(j.toString());
			downloader.downloadJar(j);
			runtime.add(j.fileName());
			if (j.isSource())
				source.add(j.sourceFileName());
		}
		downloader.setDest("dev");
		for (Jar j : file.getDev()) {
			downloader.downloadJar(j);
			dev.add(j.fileName());
			if (j.isSource())
				source.add(j.sourceFileName());
		}
		clear(libPath, runtime);
		clear(libPath.resolve("dev"), dev);
		clear(libPath.resolve("src"), source);
	}

	/**
	 * 清除目录中没有在library.json中配置的文件
	 * 
	 * @param path
	 * @param names
	 */
	private void clear(Path path, Set<String> names) {
		try {
			Files.list(path).filter(Files::isRegularFile).forEach(p -> {
				if (!names.contains(p.getFileName().toString())) {
					System.out.println("deleting unnecessary file:" + p.toString());
					try {
						Files.delete(p);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void eclipseUserLibrary() throws IOException {
		logger.info("setting eclipse user library ...");
		String key = "org.eclipse.jdt.core.userLibrary.Rainbow\\ Library=";
		StringBuilder sb = new StringBuilder(key).append("<?xml version\\=\"1.0\" encoding\\=\"UTF-8\"?>\\n")
				.append("<userlibrary systemlibrary\\=\"false\" version\\=\"2\">\\n");
		LibraryFile file = getLibraryFile();

		file.getRuntime().stream().forEach(j -> {
			sb.append("\\t<archive path\\=\"/rainbow/lib/").append(j.fileName()).append("\"");
			if (j.isSource())
				sb.append(" sourceattachment\\=\"/rainbow/lib/src/").append(j.sourceFileName()).append("\"");
			sb.append("/>\\n");
		});

		file.getDev().stream().forEach(j -> {
			sb.append("\\t<archive path\\=\"/rainbow/lib/dev/").append(j.fileName()).append("\"");
			if (j.isSource())
				sb.append(" sourceattachment\\=\"/rainbow/lib/src/").append(j.sourceFileName()).append("\"");
			sb.append("/>\\n");
		});

		sb.append("</userlibrary>\\n");
		Path path = Paths.get("../.metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.jdt.core.prefs");

		List<String> lines = Files.lines(path).filter(line -> !line.startsWith(key)).collect(Collectors.toList());
		lines.add(sb.toString());
		Files.write(path, lines);
	}

	public static void main(String[] args) throws IOException {
		LibraryWorker worker = new LibraryWorker();
		worker.generateLibraryFile();
		worker.download();
		worker.eclipseUserLibrary();
	}
}
