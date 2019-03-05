package rainbow.service;

import com.google.common.base.Strings;

/**
 * 服务调用结果对象
 * 
 * @author lijinghui
 * 
 */
public class ServiceResult {

	public static final int STATE_SUCCESS = 0;
	public static final int STATE_NO_SESSION = 1;
	public static final int STATE_EXCEPTION = 2;

	private Object result;

	private int state;
	
	private ServiceResult(Object result, int state) {
		this.result = result;
		this.state = state;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isSuccess() {
		return state == STATE_SUCCESS;
	}

	public static ServiceResult success(Object value) {
		return new ServiceResult(value, STATE_SUCCESS);
	}

	public static ServiceResult noSession(SessionNotSetException e) {
		return new ServiceResult(e.getMessage(), STATE_NO_SESSION);
	}

	public static ServiceResult exception(Throwable e) {
		String msg = e.getMessage();
		if ("DataAccessException".equals(e.getClass().getSimpleName())) {
			if (Strings.isNullOrEmpty(msg)) {
				msg = e.getCause().getMessage();
			}
			msg = "数据库错误：" + msg;
		} else {
			if (Strings.isNullOrEmpty(msg)) {
				msg = e.toString();
			}
		}
		return new ServiceResult(msg, STATE_EXCEPTION);
	}

}
