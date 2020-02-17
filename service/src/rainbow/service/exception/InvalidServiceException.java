package rainbow.service.exception;

import rainbow.core.util.Utils;

/**
 * 服务不可用时的异常
 * 
 * @author lijinghui
 * 
 */
public class InvalidServiceException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public InvalidServiceException(String service) {
        super(Utils.format("service not exist: {}", service));
    }

}
