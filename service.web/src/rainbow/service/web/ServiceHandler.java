package rainbow.service.web;

import static rainbow.core.util.Preconditions.checkArgument;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;

import rainbow.core.bundle.Bean;
import rainbow.core.model.exception.AppException;
import rainbow.core.platform.SessionException;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.Inject;
import rainbow.service.ServiceInvoker;
import rainbow.service.StreamResult;
import rainbow.web.RequestHandler;

@Bean(extension = RequestHandler.class)
public class ServiceHandler implements RequestHandler {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private ServiceInvoker serviceInvoker;

	@Inject
	public void setServiceInvoker(ServiceInvoker serviceInvoker) {
		this.serviceInvoker = serviceInvoker;
	}

	@Override
	public String getName() {
		return "s";
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String param = Utils.streamToString(request.getInputStream());
		try {
			Object value = callService(target, param);
			if (value != null && value instanceof StreamResult) {
				StreamResult sr = (StreamResult) value;
				writeStreamBack(baseRequest, response, sr.getInputStream(), sr.getName());
			} else
				writeJsonBack(baseRequest, response, value);
		} catch (SessionException e) {
			response.sendError(401, e.getKey());
		} catch (AppException e) {
			response.sendError(500, e.getMessage());
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}

	protected Object callService(String target, String param) throws Throwable {
		Queue<String> queue = splitTarget(target);
		checkArgument(queue.size() >= 2);
		String serviceId = queue.poll();
		String methodName = queue.poll();
		Type[] types = serviceInvoker.getMethodParamTypes(serviceId, methodName);
		switch (types.length) {
		case 0:
			return serviceInvoker.invoke(serviceId, methodName);
		case 1:
			Type type = types[0];
			Object arg = type == String.class ? param : JSON.parseObject(param, type);
			return serviceInvoker.invoke(serviceId, methodName, arg);
		default:
			DefaultJSONParser parser = new DefaultJSONParser(param, ParserConfig.getGlobalInstance());
			Object[] args = parser.parseArray(types);
			parser.close();
			return serviceInvoker.invoke(serviceId, methodName, args);
		}
	}

	private Queue<String> splitTarget(String target) {
		String[] parts = Utils.split(target, '/');
		LinkedList<String> result = new LinkedList<String>();
		for (String p : parts)
			result.add(p);
		return result;
	}

}