package rainbow.core.web;

public class UploadResult {

	private boolean success;

	private Object result;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public UploadResult(boolean success, Object result) {
		this.success = success;
		this.result = result;
	}

	public static UploadResult error(String message) {
		return new UploadResult(false, message);
	}

	public static UploadResult ok(Object result) {
		return new UploadResult(true, result);
	}
}
