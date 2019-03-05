package rainbow.service;

import java.lang.reflect.Method;

public interface ServiceInvoker {

	boolean hasService(String serviceId);
	
	boolean hasMethod(String serviceId, String methodName);

	Method getMethod(String serviceId, String methodName);
	
	ServiceResult invoke(ServiceRequest request);
}
