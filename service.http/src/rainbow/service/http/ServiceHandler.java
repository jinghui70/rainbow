package rainbow.service.http;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import rainbow.core.bundle.Bean;
import rainbow.core.bundle.Extension;
import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.model.exception.AppException;
import rainbow.core.platform.SessionException;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.Inject;
import rainbow.core.util.json.JSON;
import rainbow.httpserver.HttpUtils;
import rainbow.httpserver.RequestHandler;
import rainbow.service.Service;
import rainbow.service.ServiceMethod;
import rainbow.service.ServiceRegistry;
import rainbow.service.StreamResult;
import rainbow.service.exception.InvalidServiceException;

@Bean
@Extension(name = "service")
public class ServiceHandler implements RequestHandler {

	public static final TypeReference<Map<String, String>> mapType = new TypeReference<Map<String, String>>() {
	};

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private ServiceRegistry serviceRegistry;

	@Inject
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		RequestContext context = new RequestContext(target, request, response);
		try {
			String[] parts = Utils.split(target, '/');
			if (parts.length < 2)
				throw new InvalidServiceException(target);
			// 读取服务对应的服务函数
			Service service = serviceRegistry.getService(parts[0]);
			ServiceMethod method = service.getMethod(parts[1]);
			context.setService(service);
			context.setMethod(method);

			// 服务调用前拦截处理
			List<HttpServiceInterceptor> interceptors = ExtensionRegistry
					.getExtensionObjects(HttpServiceInterceptor.class);
			if (!interceptors.isEmpty()) {
				for (HttpServiceInterceptor interceptor : interceptors)
					interceptor.beforeService(context);
			}

			Object value = null;
			if (method.paramCount() == 0) {
				value = method.invoke();
			} else {
				Object[] args = parseParam(method, parts, request);
				value = method.invoke(args);
			}
			// 服务调用后拦截处理
			if (!interceptors.isEmpty()) {
				context.setResult(value);
				for (HttpServiceInterceptor interceptor : interceptors)
					interceptor.afterService(context);
				value = context.getResult(); // 拦截器是可以改变结果的
			}
			if (value != null && value instanceof StreamResult) {
				writeStreamResult(response, (StreamResult) value);
			} else
				HttpUtils.writeJsonBack(response, value);
		} catch (SessionException e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getKey());
		} catch (InvalidServiceException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (InvalidServiceParamException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorText(e));
		} catch (AppException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (Throwable e) {
			String error = errorText(e);
			logger.error(error, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, error);
		}
		baseRequest.setHandled(true);
	}

	/**
	 * 分析调用参数
	 * 
	 * @param method
	 * @param parts
	 * @return
	 */
	private Object[] parseParam(ServiceMethod method, String[] parts, HttpServletRequest request) {
		try {
			if (method.paramCount() == 1 && parts.length == 3) {
				Type type = method.getParams()[0].getType();
				if (type == String.class)
					return new String[] { parts[2] };
				return new Object[] { Integer.parseInt(parts[2]) };
			}
			String param = Utils.streamToString(request.getInputStream());
			if (param.startsWith("json="))
				param = param.substring(5);

			Map<String, Object> map = JSON.toMap(param, method.getParamTypeMap());
			Object[] args = new Object[method.paramCount()];
			int i = 0;
			for (Parameter p : method.getParams()) {
				args[i++] = map.get(p.getName());
			}
			return args;
		} catch (Throwable e) {
			throw new InvalidServiceParamException(e);
		}
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
		if (sr.getInputStream() == null && sr.getStreamWriter() == null) {
			response.sendError(404, sr.getName());
		} else {
			String mime = sr.getContentType();
			if (mime == null)
				mime = HttpUtils.getMimeType(sr.getName());
			response.setContentType(mime);
			if (sr.isDownload()) {
				StringBuilder tmpstr = new StringBuilder("attachment");
				if (Utils.hasContent(sr.getName()) && sr.getName().charAt(0) != '.')
					tmpstr.append("; filename=\"").append(URLEncoder.encode(sr.getName(), "UTF-8")).append("\"");
				response.setHeader("Content-Disposition", tmpstr.toString());
			}
			if (sr.getInputStream() != null) {
				Utils.copy(sr.getInputStream(), response.getOutputStream());
			} else {
				try (BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
					sr.getStreamWriter().write(bos);
				}
			}
		}
	}

}