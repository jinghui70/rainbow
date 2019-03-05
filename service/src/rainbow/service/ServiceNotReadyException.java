package rainbow.service;

import rainbow.core.model.exception.AppException;

/**
 * 服务不可用时的异常
 * 
 * @author lijinghui
 * 
 */
public class ServiceNotReadyException extends AppException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public ServiceNotReadyException(String serviceId) {
        super(String.format("service [%s] not ready", serviceId));
    }

}
