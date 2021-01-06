package rainbow.core.ant;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

import rainbow.core.bundle.Bundle;
import rainbow.core.bundle.Jar;
import rainbow.core.util.Utils;
import rainbow.core.util.dag.Dag;
import rainbow.core.util.json.JSON;

public class LibraryFileMaker {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private List<Jar> libs;
	private Map<String, Jar> oldMap;
	private Map<String, Jar> libMap;

	public LibraryFile make(Dag<Bundle> dag) {
		libMap = new HashMap<>();
		libs = new ArrayList<>();
		logger.info("read library.json");
		LibraryFile libraryFile = readLibraryFile();
		oldMap = new HashMap<>();

		libraryFile.getDev().stream().forEach(j -> oldMap.put(j.id(), j));
		libraryFile.getRuntime().stream().forEach(j -> oldMap.put(j.id(), j));

		readCoreLib();
		for (Bundle bundle : dag.dfsList()) {
			List<Jar> bundlelibs = bundle.getData().getLibs();
			if (Utils.isNullOrEmpty(bundlelibs))
				continue;
			logger.info("read libs of {}", bundle.getId());
			bundlelibs.forEach(this::addJar);
		}
		libraryFile.setDev(libs.stream().filter(Jar::isDev).collect(Collectors.toList()));
		libraryFile.setRuntime(libs.stream().filter(j -> !j.isDev()).collect(Collectors.toList()));
		Path file = Paths.get("library.json");
		JSON.toJSON(libraryFile, file, true);
		return libraryFile;
	}

	private LibraryFile readLibraryFile() {
		Path file = Paths.get("library.json");
		if (!Files.exists(file)) {
			LibraryFile result = new LibraryFile();
			result.setRepository("https://maven.aliyun.com/repository/public");
			return result;
		}
		return JSON.parseObject(file, LibraryFile.class);
	}

	/**
	 * 添加一个jar
	 * 
	 * @param jarNew
	 */
	private void addJar(Jar jarNew) {
		logger.info("add : {}", jarNew.toString());
		String id = jarNew.id();
		Jar old = oldMap.get(id);
		if (old != null) {
			// 手工配置了source
			if (old.isSource())
				jarNew.setSource(true);
			// 手工配置了repository
			if (!Objects.equal(old.getRepository(), jarNew.getRepository())) {
				jarNew.setRepository(old.getRepository());
			}
		}
		Jar j = libMap.get(id);
		if (j == null) {
			libMap.put(id, jarNew);
			libs.add(jarNew);
		} else {
			if (compareVersion(j.getVersion(), jarNew.getVersion()) < 0) {
				j.setVersion(jarNew.getVersion());
			}
			if (!jarNew.isDev())
				j.setDev(false);
		}
	}

	/**
	 * 版本比较
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	private int compareVersion(String v1, String v2) {
		checkNotNull(v1);
		checkNotNull(v2);
		return v1.compareTo(v2);
	}

	private void readCoreLib() {
		logger.info("read corelib.xml");
		Path file = Paths.get("corelib.xml");
		System.out.print("BBBBBBBBBBBBBBB ");
		System.out.println(file.toAbsolutePath().toString());

		try {
			Files.lines(file).map(String::trim).filter(s -> s.startsWith("<getjar")).forEach(s -> {
				Jar jar = new Jar();
				jar.setGroup(Utils.substringBetween(s, "<getjar group=\"", "\""));
				jar.setName(Utils.substringBetween(s, "name=\"", "\""));
				jar.setVersion(Utils.substringBetween(s, "version=\"", "\""));
				boolean dev = s.indexOf("flag=\"dev\"") > 0;
				jar.setDev(dev);
				addJar(jar);
			});
		} catch (IOException e) {
			throw new RuntimeException("read corelib.xml failed", e);
		}
	}

}
