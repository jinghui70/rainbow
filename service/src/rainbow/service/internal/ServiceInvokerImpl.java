package rainbow.service.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.bundle.Bean;
import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.platform.SessionException;
import rainbow.core.util.ioc.Inject;
import rainbow.service.ServiceInterceptor;
import rainbow.service.ServiceInvoker;

@Bean
public class ServiceInvokerImpl implements ServiceInvoker {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private ServiceRegistry serviceRegistry;

	@Inject
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public Type[] getMethodParamTypes(String serviceId, String methodName) {
		Method method = serviceRegistry.getMethod(serviceId, methodName);
		return method.getGenericParameterTypes();
	}

	@Override
	public Object invoke(String serviceId, String methodName, Object... args) throws Throwable {
		List<ServiceInterceptor> interceptors = ExtensionRegistry.getExtensionObjects(ServiceInterceptor.class);
		Service service = serviceRegistry.getService(serviceId);
		Method method = service.getMethod(methodName);
		Object target = service.getServiceImpl();
		for (ServiceInterceptor interceptor : interceptors) {
			try {
				interceptor.beforeService(service, method, args);
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
			interceptor.afterService(service, method, args, t == null ? result : t);
		}
		if (t != null)
			throw t;
		return result;
	}
}
