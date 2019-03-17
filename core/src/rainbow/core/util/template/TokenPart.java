package rainbow.core.util.template;

import java.io.IOException;
import java.io.Writer;

import rainbow.core.util.Utils;

public class TokenPart implements Part {

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public TokenPart(String token) {
		this.token = token;
	}

	@Override
	public void output(Writer writer, ValueProvider vp) throws IOException {
		String value = vp.getValue(token);
		if (!Utils.isNullOrEmpty(value))
			writer.write(value);
	}
}
