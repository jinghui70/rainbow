package rainbow;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

/**
 * 平台启动入口
 * 
 * @author lijinghui
 * 
 */
public final class Launcher {

	private static final int WEB_DIR = 1;
	private static final int WEB_PORT = 2;

	private static File home;

	private Launcher() {
	}

	public static void main(String args[]) {
		String homeStr = System.getProperty("RAINBOW_HOME");
		if (homeStr == null) {
			homeStr = new File(System.getProperty("user.dir")).getParent();
			System.setProperty("RAINBOW_HOME", homeStr);
		}
		home = new File(homeStr);

		if (args.length > 0)
			try {
				procArgs(args);
			} catch (Throwable e) {
				System.out.println(e.getMessage());
				return;
			}

		startPlatform();
	}

	private static void procArgs(String args[]) {
		int i = 0;
		while (i < args.length) {
			String optionStr = args[i++];
			int option = checkOption(optionStr);
			if (i == args.length)
				throw new IllegalArgumentException(String.format("option %s need param", optionStr));
			String param = args[i++];
			switch (option) {
			case WEB_DIR:
				File webdir = new File(home, param);
				if (webdir.isDirectory() && webdir.exists())
					System.setProperty("RAINBOW_WEB_DIR", param);
				else
					throw new RuntimeException("invalid web dir:" + param);
				break;
			case WEB_PORT:
				try {
					int port = Integer.parseInt(param);
					if (port < 1 || port > 65535)
						throw new RuntimeException();
				} catch (Throwable e) {
					throw new IllegalArgumentException("port should between 1 - 65535");
				}
				System.setProperty("RAINBOW_WEB_PORT", param);
				break;
			}
		}
	}

	private static int checkOption(String option) {
		if ("-p".equals(option))
			return WEB_PORT;
		else if ("-d".equals(option))
			return WEB_DIR;
		throw new IllegalArgumentException("invalid option: " + option);
	}

	private static void startPlatform() {
		try {
			System.out.println("Starting Rainbow Platform ..."); // NOPMD
			System.out.println("-----------------------------------------------------------------------------"); // NOPMD

			String os = System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
					+ System.getProperty("os.version"); // NOPMD
			String vm = System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.name") + " "
					+ System.getProperty("java.vm.version"); // NOPMD

			System.out.println("Operating system: " + os); // NOPMD
			System.out.println("Java VM: " + vm); // NOPMD
			System.out.println("Locale: " + Locale.getDefault()); // NOPMD
			System.out.println("file.encoding = " + System.getProperty("file.encoding")); // NOPMD

			ClassLoader platformClassLoader = Launcher.class.getClassLoader();
			File[] jarFiles = new File(home, "lib").listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar");
				}
			});
			URL[] urls = new URL[jarFiles.length + 1];
			urls[0] = new File(home, "conf").toURI().toURL();
			for (int i = 0; i < jarFiles.length; i++) {
				urls[i + 1] = jarFiles[i].toURI().toURL();
			}
			platformClassLoader = new URLClassLoader(urls, Launcher.class.getClassLoader());
			Thread.currentThread().setContextClassLoader(platformClassLoader);

			Class<?> Platform = platformClassLoader.loadClass("rainbow.core.platform.Platform");
			Method startup = Platform.getMethod("startup");
			startup.invoke(Platform);
		} catch (InvocationTargetException e) {
			System.err.println("Platform startup error !!!"); // NOPMD
			System.err.println("-----------------------------------------------------------------------------"); // NOPMD
			e.getTargetException().printStackTrace(System.err);
		} catch (Throwable e) {
			System.err.println("Platform startup error !!!"); // NOPMD
			System.err.println("-----------------------------------------------------------------------------"); // NOPMD
			e.printStackTrace(System.err);
		}
	}
}
