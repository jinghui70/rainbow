package rainbow.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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

	default void writeJsonBack(HttpServletResponse response, Object result) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try (Writer writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
			String content = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect,
					SerializerFeature.WriteEnumUsingName, SerializerFeature.WriteDateUseDateFormat);
			// content = AES.encode(request, content);
			writer.write(content);
		}
	}

	/**
	 * 返回流内容
	 * 
	 * @param response
	 * @param stream
	 * @param mimeType
	 * @throws IOException
	 */
	default void writeStreamBack(HttpServletResponse response, InputStream stream, String mimeType) throws IOException {
		response.setContentType(mimeType);
		OutputStream outStream = response.getOutputStream();
		try (InputStream is = stream) {
			int len = 0;
			byte[] buffer = new byte[8192];
			while ((len = is.read(buffer)) > 0) {
				outStream.write(buffer, 0, len);
			}
		}
	}

	/**
	 * 处理下载的流
	 * 
	 * @param baseRequest
	 * @param response
	 * @param stream
	 * @param name        文件名，如果只有后缀，应该以点'.'开始
	 * @throws IOException
	 */
	default void writeStreamDownload(HttpServletResponse response, InputStream stream, String name) throws IOException {
		try {
			if (name.charAt(0) == '.')
				response.setHeader("Content-Disposition", "attachment");
			else
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=\"%s\"", URLEncoder.encode(name, "UTF-8")));
		} catch (UnsupportedEncodingException e) {
		}
		writeStreamBack(response, stream, name);
	}

	/**
	 * 下载一个文件的响应
	 * 
	 * @param response
	 * @param file
	 * @throws IOException
	 */
	default void writeFileDownload(HttpServletResponse response, Path file) throws IOException {
		long size = Files.size(file);
		response.setHeader("Content-Length", Long.toString(size));
		writeStreamDownload(response, Files.newInputStream(file), file.getFileName().toString());
	}

}
