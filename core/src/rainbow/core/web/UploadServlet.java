package rainbow.core.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;

import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.platform.Session;
import rainbow.core.util.Utils;

public class UploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5802815156149763747L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UploadResult result = processUpload(req);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		try (Writer writer = new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8)) {
			writer.write(JSON.toJSONStringWithDateFormat(result, "yyyy/MM/dd HH:mm:ss",
					SerializerFeature.QuoteFieldNames, SerializerFeature.SkipTransientField,
					SerializerFeature.WriteEnumUsingToString, SerializerFeature.SortField,
					SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat));
		}
	}

	private String getPath(HttpServletRequest httpRequest) {
		String path = httpRequest.getPathInfo();
		if (path == null)
			return httpRequest.getServletPath();
		return path;
	}

	protected void prepareSession(HttpSession session) {
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

	private UploadResult processUpload(HttpServletRequest httpRequest) {
		String path = getPath(httpRequest);
		if (Utils.isNullOrEmpty(path))
			return UploadResult.error("错误的上传地址->" + path);
		UploadHandler handler = ExtensionRegistry.getExtensionObject(UploadHandler.class, path.substring(1));
		if (handler == null) {
			return UploadResult.error("错误的上传地址->" + path);
		}
		HttpSession session = httpRequest.getSession();
		prepareSession(session);

		try {
			Object result = handler.doWork(getServletContext(), httpRequest);
			return UploadResult.ok(result);
		} catch (IllegalArgumentException e) {
			return UploadResult.error(e.getMessage());
		} catch (IllegalStateException e) {
			return UploadResult.error(e.getMessage());
		} catch (Throwable e) {
			return UploadResult.error(Throwables.getStackTraceAsString(e));
		}
	}

}