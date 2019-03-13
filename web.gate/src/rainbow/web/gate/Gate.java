package rainbow.web.gate;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.bundle.Bean;

@Bean
public class Gate extends AbstractHandler {

	private static final Logger logger = LoggerFactory.getLogger(Gate.class);

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		logger.debug("capture [{}]:{}", baseRequest.getDispatcherType(), target);
		switch (baseRequest.getDispatcherType()) {
		case REQUEST:
			handleRequest(target, request, response);
			break;
		case ERROR:
			doError(target, baseRequest, request, response);
			break;
		default:
			break;
		}
	}

	protected void handleRequest(String target, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try (PrintWriter writer = response.getWriter()) {
			writer.write(target);
		}
	}
}