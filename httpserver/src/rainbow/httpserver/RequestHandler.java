package rainbow.httpserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

/**
 * Web请求处理器，请求的入口由name属性标记
 * 
 * @author lijinghui
 *
 */
public interface RequestHandler {

	void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException;

}
