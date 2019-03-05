package rainbow.core.util.template;

import rainbow.core.util.Utils;

public class ValueProviderAdapter implements ValueProvider {

	@Override
	public String getValue(String token) {
		return Utils.NULL_STR;
	}

	@Override
	public void startLoop(String loopName) {
	}

	@Override
	public boolean loopNext(String loopName) {
		return false;
	}

	@Override
	public String getSwitchKey(String switchName) {
		return Utils.NULL_STR;
	}

	@Override
	public boolean ifValue(String ifName) {
		return true;
	}

}
