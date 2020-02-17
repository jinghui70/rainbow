package rainbow.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.platform.SessionException;
import rainbow.service.annotation.Comment;

public class ServiceMethod {

	private static Logger logger = LoggerFactory.getLogger(ServiceMethod.class);

	private Service service;

	private Method method;

	private String comment;

	private ServiceParam[] params;

	public Service getService() {
		return service;
	}

	public Method getMethod() {
		return method;
	}

	public String getComment() {
		return comment;
	}

	public ServiceParam[] getParams() {
		return params;
	}

	public int paramCount() {
		return params.length;
	}

	public ServiceMethod(Service service, Method method) {
		this.service = service;
		this.method = method;
		Comment entry = method.getAnnotation(Comment.class);
		if (entry != null)
			this.comment = entry.value();
		Parameter[] ps = method.getParameters();
		params = new ServiceParam[ps.length];
		if (ps.length > 0) {
			for (int i = 0; i < ps.length; i++) {
				params[i] = new ServiceParam(ps[i]);
			}
		}
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
