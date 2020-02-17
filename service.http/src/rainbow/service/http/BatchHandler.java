package rainbow.service.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import rainbow.core.bundle.Bean;
import rainbow.httpserver.RequestHandler;

@Bean(extension = RequestHandler.class)
public class BatchHandler implements RequestHandler {

	@Override
	public String getName() {
		return "batch";
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	}

}