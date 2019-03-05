package rainbow.core.util.template;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Template {

	private static final Logger logger = LoggerFactory.getLogger(Template.class);

	private List<Part> parts;

	public Template(String template) {
		this(template, "@");
	}

	public Template(String template, String flag) {
		this(template, flag, flag);
	}

	public Template(String template, String openFlag, String closeFlag) {
		parts = new Parser(template, openFlag, closeFlag).parse();
	}

	public void output(Writer writer, ValueProvider vp) {
		try {
			for (Part part : parts)
				part.output(writer, vp);
		} catch (IOException e) {
			logger.error("when template output things", e);
			throw new RuntimeException(e);
		}
	}

	public void output(Writer writer, final Map<String, String> tokenMap) {
		output(writer, new ValueProviderAdapter() {
			@Override
			public String getValue(String token) {
				return tokenMap.get(token);
			}
		});
	}

	public String output(ValueProvider vp) {
		StringWriter writer = new StringWriter();
		output(writer, vp);
		return writer.toString();
	}

	public String output(final Map<String, String> tokenMap) {
		StringWriter writer = new StringWriter();
		output(writer, tokenMap);
		return writer.toString();
	}

}
