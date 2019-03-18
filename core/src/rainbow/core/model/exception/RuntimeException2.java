package rainbow.core.model.exception;

import rainbow.core.util.Utils;

/**
 * 系统业务异常基础类
 * 
 */
public class RuntimeException2 extends RuntimeException {
	/**

	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RuntimeException2(String message, Object... args) {
		super(Utils.format(message, args));
		if (args.length>0) {
			Object last = args[args.length - 1];
			if (last instanceof Throwable) initCause((Throwable) last);
		}
	}

}
