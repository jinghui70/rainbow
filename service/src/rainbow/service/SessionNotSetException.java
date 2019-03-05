package rainbow.service;

import rainbow.core.model.exception.AppException;

/**
 * 服务不可用时的异常
 * 
 * @author lijinghui
 * 
 */
public class SessionNotSetException extends AppException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public SessionNotSetException(String session) {
        super(String.format("session [%s] not set", session));
    }

}
