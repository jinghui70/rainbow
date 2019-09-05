package rainbow.web.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestErrorHandler extends ErrorHandler {

	public static final Logger logger = LoggerFactory.getLogger(RequestErrorHandler.class);

	@Override
	protected void generateAcceptableResponse(Request baseRequest, HttpServletRequest request,
			HttpServletResponse response, int code, String message, String mimeType) throws IOException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType("application/json");
		Writer writer = response.getWriter();
		writer.write("HTTP ERROR ");
		writer.write(Integer.toString(code));
		writer.write("\nReason:");
		write(writer, message);
		writer.write("\nCaused by:\n");
		Throwable th = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		if (th != null)
			logger.error(th.getMessage(), th);
		while (th != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			th.printStackTrace(pw);
			pw.flush();
			write(writer, sw.getBuffer().toString());
			writer.write("\n");
			th = th.getCause();
		}
		baseRequest.setHandled(true);
	}
}
