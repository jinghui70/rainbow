package rainbow.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import rainbow.core.model.object.INameObject;

/**
 * Web请求处理器，请求的入口由name属性标记
 * 
 * @author lijinghui
 *
 */
public interface RequestHandler extends INameObject {

	void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException;

	default void writeJsonBack(Request baseRequest, HttpServletResponse response, Object result) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try (Writer writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
			String content = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect,
					SerializerFeature.WriteEnumUsingName, SerializerFeature.WriteDateUseDateFormat);
			// content = AES.encode(request, content);
			writer.write(content);
		}
		baseRequest.setHandled(true);
	}

	default void writeStreamBack(Request baseRequest, HttpServletResponse response, StreamResult result)
			throws IOException {
		response.setContentType("applicatoin/octet-stream");
		response.addHeader("Content-Disposition",
				String.format("attachment; filename=\"%s\"", URLEncoder.encode(result.getName(), "UTF-8")));
		OutputStream outStream = response.getOutputStream();
		try (InputStream is = result.getInputStream()) {
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = is.read(buffer)) > 0) {
				outStream.write(buffer, 0, len);
			}
		}
		baseRequest.setHandled(true);
	}
}
