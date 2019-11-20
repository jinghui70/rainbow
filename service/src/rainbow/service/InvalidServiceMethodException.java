package rainbow.service;

import rainbow.core.util.Utils;

/**
 * 服务不可用时的异常
 * 
 * @author lijinghui
 * 
 */
public class InvalidServiceMethodException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidServiceMethodException(String serviceId, String methodName) {
		super(Utils.format("invalid method {}.{}", serviceId, methodName));
	}

}
