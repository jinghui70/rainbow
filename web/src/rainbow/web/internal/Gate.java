package rainbow.web.internal;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import rainbow.core.bundle.Bean;
import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.platform.Session;
import rainbow.web.RequestHandler;

@Bean
public class Gate extends AbstractHandler {

	private static final Logger logger = LoggerFactory.getLogger(Gate.class);

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		switch (baseRequest.getDispatcherType()) {
		case REQUEST:
			logger.debug("capture REQUEST:{}", target);
			handleRequest(target, baseRequest, request, response);
			break;
		case ERROR:
			doError(target, baseRequest, request, response);
			break;
		default:
			break;
		}

	}

	private void handleRequest(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		if (target.startsWith("/"))
			target = target.substring(1);
		if (target.isEmpty())
			return;
		int inx = target.indexOf('/');
		String routeString = target;
		if (inx > 0) {
			routeString = target.substring(0, inx);
			target = target.substring(inx + 1);
		} else {
			routeString = target;
			target = "";
		}
		RequestHandler route = ExtensionRegistry.getExtensionObject(RequestHandler.class, routeString);
		if (route == null)
			return;
		// 获取Session
		prepareSession(request.getSession());
		route.handle(target, baseRequest, request, response);
	}

	private void prepareSession(HttpSession session) {
		ImmutableMap.Builder<String, Object> sessionValueBuilder = ImmutableMap.builder();
		Enumeration<String> enumeration = session.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			Object value = session.getAttribute(key);
			sessionValueBuilder.put(key, value);
		}
		Map<String, Object> sessionValue = sessionValueBuilder.build();
		Session.set(sessionValue);
	}

}