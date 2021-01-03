package rainbow.core.ant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rainbow.core.bundle.Bundle;
import rainbow.core.platform.ProjectClassLoader;
import rainbow.core.util.dag.Dag;

public class ClassPathMaker {

	private void make(Bundle bundle, Set<Bundle> ancestors, boolean core) {
		System.out.println("generating classpath of " + bundle.getId());
		Path root = Paths.get("../").resolve(bundle.getId());
		List<Bundle> p = new ArrayList<>();
		List<Bundle> j = new ArrayList<>();
		for (Bundle b : ancestors) {
			if (b.getClassLoader() instanceof ProjectClassLoader)
				p.add(b);
			else
				j.add(b);
		}
		List<String> lines = new ArrayList<>();
		lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		lines.add("<classpath>");
		lines.add("\t<classpathentry kind=\"src\" path=\"src\"/>");
		if (Files.exists(root.resolve("test"))) {
			lines.add("\t<classpathentry kind=\"src\" path=\"test\"/>");
		}
		if (core)
			lines.add("\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/core\"/>");
		for (Bundle b : p) {
			lines.add(String.format("\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/%s\"/>",
					b.getId()));
		}
		lines.add("\t<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>");
		lines.add("\t<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/Rainbow Library\"/>");
		for (Bundle b : j) {
			lines.add(String.format("\t<classpathentry kind=\"lib\" path=\"/rainbow/bundle/%s.jar\"/>", b.getId()));
		}
		lines.add("\t<classpathentry kind=\"output\" path=\"bin\"/>");
		lines.add("</classpath>");
		Path file = root.resolve(".classpath");
		try {
			Files.write(file, lines);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void makeAll(boolean core) {
		Dag<Bundle> dag = BundleAware.loadBundleDag();
		dag.dfsList().forEach(bundle -> {
			if (bundle.getClassLoader() instanceof ProjectClassLoader)
				make(bundle, dag.getAncestors(bundle), core);
		});
	}

	public static void main(String[] args) {
		ClassPathMaker maker = new ClassPathMaker();
		maker.makeAll(args.length > 0);
	}
}
