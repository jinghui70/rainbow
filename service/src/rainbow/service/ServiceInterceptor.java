package rainbow.service;

import java.lang.reflect.Method;

/**
 * 服务拦截器接口
 * 
 * @author lijinghui
 * 
 */
public interface ServiceInterceptor {

    void beforeService(Class<?> service, Method method, ServiceRequest request);
    
    /**
     * 如果beforeService 发生异常，是否继续执行服务请求
     * 
     * @return ture 忽略发生的异常， false 抛出异常，停止服务调用
     */
    boolean ignoreBeforeException();

    void afterService(Class<?> service, Method method, ServiceRequest request, Object result);
    
    boolean ignoreAfterException();
}
