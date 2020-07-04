package rainbow.service.http;

public interface HttpServiceInterceptor {

	void beforeService(RequestContext context);

	void afterService(RequestContext context);

}
