package rainbow.core.ant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rainbow.core.util.dag.Dag;

public class ClassPathMaker {

	private void make(BundleDataX bundle, Set<BundleDataX> ancestors, boolean rainbow) {
		System.out.println("generating classpath of " + bundle.getId());
		Path root = Paths.get("../").resolve(bundle.getId());
		List<BundleDataX> p = new ArrayList<>();
		List<BundleDataX> j = new ArrayList<>();
		for (BundleDataX b : ancestors) {
			if (b.isDev())
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
		if (rainbow)
			lines.add("\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/core\"/>");
		for (BundleDataX b : p) {
			lines.add(String.format("\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/%s\"/>",
					b.getId()));
		}
		lines.add("\t<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>");
		lines.add("\t<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/Rainbow Library\"/>");
		for (BundleDataX b : j) {
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

	public void makeAll(boolean rainbow) throws IOException {
		Dag<BundleDataX> dag = BundleAware.loadBundleDag();
		dag.dfsList().forEach(bundle -> {
			if (bundle.isDev())
				make(bundle, dag.getAncestors(bundle), rainbow);
		});
	}

	public static void main(String[] args) throws IOException {
		ClassPathMaker maker = new ClassPathMaker();
		maker.makeAll(args.length > 0);
	}
}
