package rainbow.core.platform;

import java.io.File;
import java.io.IOException;

public final class DevRainbow {

	private DevRainbow() {
	}

	public static void main(String[] args) throws IOException {
		File home = new File(System.getProperty("user.dir")).getParentFile();
		System.setProperty("RAINBOW_HOME", new File(home, "rainbow").toString());
		Platform.startup();
	}
}
