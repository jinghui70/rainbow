package rainbow.service;

import java.lang.reflect.Type;

public interface ServiceInvoker {

	Type[] getMethodParamTypes(String serviceId, String methodName);

	Object invoke(String serviceId, String methodName, Object... args) throws Throwable;

}
