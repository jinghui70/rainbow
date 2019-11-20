package rainbow.core.platform;

import rainbow.core.model.exception.AppException;

public class SessionException extends AppException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String key;

	public SessionException(String key) {
		super("session value [{}] needed", key);
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
}
