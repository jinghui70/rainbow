package rainbow.service.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rainbow.service.Service;
import rainbow.service.ServiceMethod;

public class RequestContext {

	private String target;

	private HttpServletRequest request;

	private HttpServletResponse response;

	private Service service;

	private ServiceMethod method;

	private Object result;

	public RequestContext(String target, HttpServletRequest request, HttpServletResponse response) {
		super();
		this.target = target;
		this.request = request;
		this.response = response;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public ServiceMethod getMethod() {
		return method;
	}

	public void setMethod(ServiceMethod method) {
		this.method = method;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public String getTarget() {
		return target;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

}
