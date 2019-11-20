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
import rainbow.service.InvalidServiceException;
import rainbow.service.InvalidServiceMethodException;
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
				writeStreamResult(response, (StreamResult) value);
			} else
				writeJsonBack(response, value);
		} catch (SessionException e) {
			response.sendError(401, e.getKey());
		} catch (InvalidServiceException | InvalidServiceMethodException e) {
			response.sendError(400, e.getMessage());
		} catch (AppException e) {
			response.sendError(500, e.getMessage());
		} catch (Throwable e) {
			throw new ServletException(e);
		}
		baseRequest.setHandled(true);
	}

	/**
	 * 调用具体服务函数，当参数为1个String的时候，可以用rest方式来处理调用
	 * 
	 * @param target
	 * @param param
	 * @return
	 * @throws Throwable
	 */
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
			Object arg = param;
			if (type == String.class) {
				if (Utils.isNullOrEmpty(param) && !queue.isEmpty())
					arg = queue.poll();
			} else
				arg = JSON.parseObject(param, type);
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

	private void writeStreamResult(HttpServletResponse response, StreamResult sr) throws IOException {
		if (sr.getInputStream() == null) {
			response.sendError(404, sr.getName());
		} else if (sr.isDownload())
			writeStreamDownload(response, sr.getInputStream(), sr.getName());
		else {
			String mime = rainbow.web.Utils.getMimeType(sr.getName());
			writeStreamBack(response, sr.getInputStream(), mime);
		}
	}

}