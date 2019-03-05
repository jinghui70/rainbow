package rainbow.core.web;

import static com.google.common.base.Preconditions.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closeables;

import rainbow.core.platform.Session;
import rainbow.core.util.ioc.InitializingBean;
import rainbow.core.util.template.Template;

public abstract class UrlHandler implements InitializingBean {

	protected PatternType type;

	protected String string;

	private static Template toLogin = new Template(
			"<html><head></head><body><script>var w=window;while(w!==w.parent){w=w.parent;}w.location='@root@index.html';</script></body></html>");

	public PatternType getType() {
		return type;
	}

	public String getString() {
		return string;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Mapping mapping = checkNotNull(getClass().getAnnotation(Mapping.class), "%s need mapping annotation",
				getClass().getName());
		string = mapping.value();
		if (string.startsWith("*")) {
			type = PatternType.SUFFIX;
			string = string.substring(1);
		} else if (string.endsWith("*")) {
			type = PatternType.PREFIX;
			string = string.substring(0, string.length() - 1);
		} else
			type = PatternType.PATH;
	}

	public boolean match(String url) {
		switch (getType()) {
		case PATH:
			return getString().equals(url);
		case PREFIX:
			return url.startsWith(getString());
		case SUFFIX:
			return url.endsWith(getString());
		default:
			return false;
		}
	}

	protected final void toHomePage(HttpServletResponse httpResponse, String path) throws IOException {
		String root = Strings.repeat("../", CharMatcher.is('/').countIn(path) - 1);
		PrintWriter writer = httpResponse.getWriter();
		try {
			toLogin.output(writer, ImmutableMap.of("root", root));
		} finally {
			Closeables.close(writer, true);
		}
	}

	protected void writeJsonBack(HttpServletResponse httpResponse, Object result) throws IOException {
		httpResponse.setContentType("application/json");
		httpResponse.setCharacterEncoding("UTF-8");
		try(Writer writer = new OutputStreamWriter(httpResponse.getOutputStream(), Charsets.UTF_8)) {
			writer.write(JSON.toJSONStringWithDateFormat(result, "yyyy/MM/dd HH:mm:ss",
					SerializerFeature.QuoteFieldNames, SerializerFeature.SkipTransientField,
					SerializerFeature.WriteEnumUsingToString, SerializerFeature.SortField,
					SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat));
		}
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

	public abstract boolean handle(ServletContext sc, HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			String path) throws IOException, ServletException;

}
