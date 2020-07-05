package rainbow.httpserver.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.platform.Platform;
import rainbow.core.platform.Session;
import rainbow.core.util.Utils;
import rainbow.httpserver.HttpUtils;
import rainbow.httpserver.RequestHandler;

public class Gate extends AbstractHandler {

	private static final Logger logger = LoggerFactory.getLogger(Gate.class);

	private String rootPath;

	private Path webdir;

	public Gate(String rootPath) {
		if (rootPath.charAt(0) != '/')
			rootPath = "/" + rootPath;
		if (rootPath.charAt(rootPath.length() - 1) != '/')
			rootPath = rootPath + '/';
		this.rootPath = rootPath;

		webdir = Platform.getHome().resolve("web");
		if (!Files.exists(webdir, LinkOption.NOFOLLOW_LINKS))
			webdir = null;
	}

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
		if (target.startsWith(rootPath))
			target = target.substring(rootPath.length());
		else if (Objects.equals(rootPath, target + '/'))
			target = "";
		else
			return;

		RequestHandler route = null;
		int inx = -1;
		if (Utils.isNullOrEmpty(target)) {
			target = "index.html";
		} else {
			inx = target.indexOf('/');
			String routeString = inx > 0 ? target.substring(0, inx) : target;
			route = ExtensionRegistry.getExtensionObject(RequestHandler.class, routeString);
		}
		if (route == null) {
			if (webdir != null) {
				Path file = webdir.resolve(target);
				if (Files.exists(file) && !Files.isDirectory(file)) {
					response.setContentType(HttpUtils.getMimeType(file.getFileName().toString()));
					Utils.copy(Files.newInputStream(file), response.getOutputStream());
					baseRequest.setHandled(true);
					return;
				}
			}
		} else {
			// 获取Session
			prepareSession(request.getSession());
			target = inx > 0 ? target.substring(inx + 1) : Utils.NULL_STR;
			route.handle(target, baseRequest, request, response);
		}
	}

	private void prepareSession(HttpSession session) {
		Map<String, Object> sessionValue = new HashMap<String, Object>();
		Enumeration<String> enumeration = session.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			Object value = session.getAttribute(key);
			sessionValue.put(key, value);
		}
		if (!sessionValue.isEmpty())
			Session.set(sessionValue);
	}

}