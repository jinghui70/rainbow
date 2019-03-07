package rainbow.service.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import rainbow.core.bundle.Bean;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.Inject;
import rainbow.service.ServiceInvoker;
import rainbow.service.ServiceRequest;
import rainbow.service.ServiceResult;

@Bean
public class ServiceHandler extends AbstractHandler {

	private static final Logger logger = LoggerFactory.getLogger(ServiceHandler.class);

	private ServiceInvoker serviceInvoker;

	@Inject
	public void setServiceInvoker(ServiceInvoker serviceInvoker) {
		this.serviceInvoker = serviceInvoker;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ServiceResult result = null;
		ServiceRequest serviceRequest = null;
		
		String paramStr = CharStreams.toString(request.getReader());
		//HttpSession session = request.getSession();
		try {
			serviceRequest = buildRequest(target, paramStr);
		} catch (Throwable e) {
			String msg = String.format("parsing request %s-[%s] failed", target, paramStr);
			logger.error(msg, e);
			throw new ServletException(msg, e);
		}

		//prepareSession(session);
		result = serviceInvoker.invoke(serviceRequest);
		if (result.isSuccess()) {
			writeJsonBack(response, result.getResult());
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
			try (DefaultJSONParser parser = new DefaultJSONParser(requestStr, ParserConfig.getGlobalInstance())) {
				if (types.length == 1) {
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

	private void writeJsonBack(HttpServletResponse httpResponse, Object result) throws IOException {
		httpResponse.setContentType("application/json");
		httpResponse.setCharacterEncoding("UTF-8");
		try(Writer writer = new OutputStreamWriter(httpResponse.getOutputStream(), Charsets.UTF_8)) {
			writer.write(JSON.toJSONStringWithDateFormat(result, "yyyy/MM/dd HH:mm:ss",
					SerializerFeature.QuoteFieldNames, SerializerFeature.SkipTransientField,
					SerializerFeature.WriteEnumUsingToString, SerializerFeature.SortField,
					SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat));
		}
	}

}