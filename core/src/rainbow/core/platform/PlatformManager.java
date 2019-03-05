package rainbow.core.platform;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class PlatformManager implements PlatformManagerMBean {

	public static ObjectName getName() {
		try {
			return ObjectName.getInstance("rainbow:name=platform-manager");
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void shutdown() {
		Platform.shutdown();
	}

}
