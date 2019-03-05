package rainbow.core.platform;

public class SessionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SessionException(String key, String error) {
		super(String.format("rainbow session value [%s] %s", key, error));
	}
	
	public SessionException() {
		super("rainbow session is empty");
	}
	
}
