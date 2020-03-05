package rainbow.service.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import rainbow.core.bundle.Bean;
import rainbow.core.model.exception.AppException;
import rainbow.core.platform.SessionException;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.Inject;
import rainbow.httpserver.HttpUtils;
import rainbow.httpserver.RequestHandler;
import rainbow.service.Service;
import rainbow.service.ServiceMethod;
import rainbow.service.ServiceParam;
import rainbow.service.ServiceRegistry;
import rainbow.service.StreamResult;
import rainbow.service.exception.InvalidServiceException;

@Bean(extension = RequestHandler.class)
public class ServiceHandler implements RequestHandler {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private ServiceRegistry serviceRegistry;

	@Inject
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public String getName() {
		return "service";
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			String[] parts = Utils.split(target, '/');
			if (parts.length < 2)
				throw new InvalidServiceException(target);
			Service service = serviceRegistry.getService(parts[0]);
			ServiceMethod method = service.getMethod(parts[1]);

			Object value = null;
			if (method.paramCount() == 0) {
				value = method.invoke();
			} else {
				Object[] args = new Object[method.paramCount()];
				if (method.paramCount() == 1 && parts.length == 3) {
					args[0] = parts[2];
				} else {
					String param = Utils.streamToString(request.getInputStream());
					JSONObject jo = JSON.parseObject(param);
					args = new Object[method.paramCount()];
					int i = 0;
					for (ServiceParam p : method.getParams()) {
						args[i++] = jo.getObject(p.getName(), p.getType());
					}
				}
				value = method.invoke(args);
			}
			if (value != null && value instanceof StreamResult) {
				writeStreamResult(response, (StreamResult) value);
			} else
				HttpUtils.writeJsonBack(response, value);
		} catch (SessionException e) {
			response.sendError(401, e.getKey());
		} catch (InvalidServiceException e) {
			response.sendError(400, e.getMessage());
		} catch (AppException e) {
			response.sendError(500, e.getMessage());
		} catch (Throwable e) {
			String error = errorText(e);
			logger.error(error, e);
			response.sendError(500, error);
		}
		baseRequest.setHandled(true);
	}

	private String errorText(Throwable e) {
		StringBuilder sb = new StringBuilder();
		while (e != null) {
			String m = e.getMessage();
			if (m != null) {
				sb.append(m).append("-");
			}
			sb.append(e.getClass().getSimpleName()).append('\n');
			e = e.getCause();
		}
		return sb.toString();
	}

	private void writeStreamResult(HttpServletResponse response, StreamResult sr) throws IOException {
		if (sr.getInputStream() == null) {
			response.sendError(404, sr.getName());
		} else if (sr.isDownload())
			HttpUtils.writeStreamDownload(response, sr.getInputStream(), sr.getName());
		else {
			String mime = rainbow.httpserver.HttpUtils.getMimeType(sr.getName());
			HttpUtils.writeStreamBack(response, sr.getInputStream(), mime);
		}
	}

}