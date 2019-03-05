package rainbow.service.web;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.google.common.io.CharStreams;

import rainbow.core.bundle.Bean;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.Inject;
import rainbow.core.web.Mapping;
import rainbow.core.web.UrlHandler;
import rainbow.service.ServiceInvoker;
import rainbow.service.ServiceRequest;
import rainbow.service.ServiceResult;

@Bean(extension = UrlHandler.class)
@Mapping("/api/*")
public class ServiceHandler extends UrlHandler {

	private static final Logger logger = LoggerFactory.getLogger(ServiceHandler.class);

	private ServiceInvoker serviceInvoker;

	@Inject
	public void setServiceInvoker(ServiceInvoker serviceInvoker) {
		this.serviceInvoker = serviceInvoker;
	}

	@Override
	public boolean handle(ServletContext sc, HttpServletRequest servletRequest, HttpServletResponse servletResponse,
			String path) throws IOException, ServletException {
		ServiceResult result = null;
		ServiceRequest request = null;
		String paramStr = CharStreams.toString(servletRequest.getReader());
		HttpSession session = servletRequest.getSession();
		try {
			request = buildRequest(path, paramStr);
		} catch (Throwable e) {
			String msg = String.format("parsing request %s-[%s] failed", path, paramStr);
			logger.error(msg, e);
			throw new ServletException(msg, e);
		}
		prepareSession(session);
		result = serviceInvoker.invoke(request);
		if (result.isSuccess()) {
			writeJsonBack(servletResponse, result.getResult());
			return true;
		}
		throw new ServletException((String) result.getResult());
	}

	private ServiceRequest buildRequest(String path, String requestStr) {
		ServiceRequest request = new ServiceRequest();
		String[] req = Utils.split(path, '/');
		request.setService(req[2]);
		request.setMethod(req[3]);

		Method method = serviceInvoker.getMethod(request.getService(), request.getMethod());
		Type[] types = method.getGenericParameterTypes();
		if (types.length > 0) {
			try(DefaultJSONParser parser = new DefaultJSONParser(requestStr, ParserConfig.getGlobalInstance())){
				if (types.length==1) {
					Object[] args = new Object[1];
					args[0] = parser.parseObject(types[0]);
					request.setArgs(args);
				} else {
					request.setArgs(parser.parseArray(types));
				}
			}
		}
		return request;
	}

}