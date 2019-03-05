package rainbow.service;

import rainbow.core.util.Utils;

/**
 * 服务请求封装对象,请求的参数可以是数组，也可以是Map
 * 
 * @author lijinghui
 * 
 */
public class ServiceRequest {

	/** 服务名 */
	private String service;

	/** 请求方法 */
	private String method;

	/** 请求参数 */
	private Object[] args = Utils.NULL_ARRAY;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public ServiceRequest(String service, String method, Object[] args) {
		this.service = service;
		this.method = method;
		this.args = args;
	}

	public ServiceRequest() {
	}

	@Override
	public String toString() {
		return service + "/" + method;
	}

}