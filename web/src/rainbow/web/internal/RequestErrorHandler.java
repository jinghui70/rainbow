package rainbow.web.internal;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestErrorHandler extends ErrorHandler {

	public static final Logger logger = LoggerFactory.getLogger(RequestErrorHandler.class);

	@Override
	protected Writer getAcceptableWriter(Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		return response.getWriter();
	}

}
