package rainbow.service.http;

public class InvalidServiceParamException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidServiceParamException(Throwable cause) {
		super("服务参数错误", cause);
	}

}
