package rainbow.core.util.template;

import java.io.IOException;
import java.io.Writer;

public class TextPart implements Part {
	
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public TextPart(String content) {
		this.content = content;
	}

	@Override
	public void output(Writer writer, ValueProvider vp) throws IOException {
		writer.write(content);
	}

}
