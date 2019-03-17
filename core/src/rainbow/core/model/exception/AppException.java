package rainbow.core.model.exception;

import rainbow.core.util.Utils;

/**
 * 系统业务异常基础类
 * 
 */
public class AppException extends RuntimeException {
	/**

	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AppException() {
		super();
	}

	public AppException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppException(String message) {
		super(message);
	}

	public AppException(Throwable cause) {
		super(cause);
	}

	public AppException(String message, Object... args) {
		super(Utils.format(message, args));
	}

}
