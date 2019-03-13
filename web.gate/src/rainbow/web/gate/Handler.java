package rainbow.web.gate;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Charsets;

import rainbow.core.model.object.INameObject;

public interface Handler extends INameObject {

	static void writeJsonBack(HttpServletResponse response, Object result) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try (Writer writer = new OutputStreamWriter(response.getOutputStream(), Charsets.UTF_8)) {
			writer.write(JSON.toJSONStringWithDateFormat(result, "yyyy/MM/dd HH:mm:ss",
					SerializerFeature.QuoteFieldNames, SerializerFeature.SkipTransientField,
					SerializerFeature.WriteEnumUsingToString, SerializerFeature.SortField,
					SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat));
		}
	}

	boolean handle(String path, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException;

}
