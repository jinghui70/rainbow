package rainbow.service;

/**
 * 服务拦截器接口
 * 
 * @author lijinghui
 * 
 */
public interface ServiceInterceptor {

	void beforeService(ServiceMethod method, Object[] args);

	/**
	 * 如果beforeService 发生异常，是否继续执行服务请求
	 * 
	 * @return ture 忽略发生的异常， false 抛出异常，停止服务调用
	 */
	boolean ignoreBeforeException();

	/**
	 * 服务调用后拦截函数
	 * 
	 * @param service
	 * @param method
	 * @param args
	 * @param result 服务执行结果对象，如果有错是个Throwable对象
	 */
	void afterService(ServiceMethod method, Object[] args, Object result);

}
