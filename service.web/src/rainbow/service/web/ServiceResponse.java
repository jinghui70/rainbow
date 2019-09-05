package rainbow.service.web;

import rainbow.core.model.exception.AppException;

/**
 * 服务调用结果对象
 * 
 * @author lijinghui
 * 
 */
public class ServiceResponse {

	public static final int STATE_SUCCESS = 0;
	public static final int STATE_NO_SESSION = 1;
	public static final int STATE_EXCEPTION = 2;

	private int state;

	private Object data;

	public boolean isSuccess() {
		return state == STATE_SUCCESS;
	}

	public boolean unexpectedException() {
		return state == STATE_EXCEPTION && !(data instanceof AppException); 
	}
	
	public Object getData() {
		return data;
	}

	private ServiceResponse(int state, Object data) {
		this.state = state;
		this.data = data;
	}

	public static ServiceResponse success(Object value) {
		return new ServiceResponse(STATE_SUCCESS, value);
	}

	public static ServiceResponse noSession(String key) {
		return new ServiceResponse(STATE_NO_SESSION, key);
	}

	public static ServiceResponse fail(Throwable e) {
		if (e instanceof AppException)
			return new ServiceResponse(STATE_EXCEPTION, e.getMessage());
		return new ServiceResponse(STATE_EXCEPTION, e);
	}
}
