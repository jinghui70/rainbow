package rainbow.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.platform.SessionException;

public class ServiceMethod {

	private static Logger logger = LoggerFactory.getLogger(ServiceMethod.class);

	private Service service;

	private Method method;

	private Map<String, Type> paramTypeMap;

	public Service getService() {
		return service;
	}

	public Method getMethod() {
		return method;
	}

	public Map<String, Type> getParamTypeMap() {
		return paramTypeMap;
	}

	public String getName() {
		return method.getName();
	}

	public Parameter[] getParams() {
		return method.getParameters();
	}

	public int paramCount() {
		return method.getParameterCount();
	}

	public ServiceMethod(Service service, Method method) {
		this.service = service;
		this.method = method;
		paramTypeMap = Arrays.stream(method.getParameters())
				.collect(Collectors.toMap(Parameter::getName, Parameter::getParameterizedType));
	}

	public Object invoke(Object... args) throws Throwable {
		List<ServiceInterceptor> interceptors = ExtensionRegistry.getExtensionObjects(ServiceInterceptor.class);
		Object target = service.getServiceImpl();
		for (ServiceInterceptor interceptor : interceptors) {
			try {
				interceptor.beforeService(this, args);
			} catch (SessionException e) {
				throw e;
			} catch (Throwable e) {
				logger.error("service interceptor[{}] error before invoke", interceptor.getClass().getName(), e);
				if (!interceptor.ignoreBeforeException())
					throw e;
			}
		}
		Throwable t = null;
		Object result = null;
		try {
			result = method.invoke(target, args);
		} catch (InvocationTargetException e) {
			t = e.getTargetException();
		} catch (IllegalAccessException | IllegalArgumentException e) {
			t = e;
		}
		for (ServiceInterceptor interceptor : interceptors) {
			interceptor.afterService(this, args, t == null ? result : t);
		}
		if (t != null)
			throw t;
		return result;
	}

}
