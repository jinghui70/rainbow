package rainbow.core.platform;

public class SessionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String key;

	public SessionException(String key) {
		super(String.format("session value [%s] needed", key));
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
}
