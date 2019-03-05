package rainbow.core.util.template;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import rainbow.core.util.Utils;

public class IfPart implements Part {
	
	private String name;
	
	private List<Part> trueParts;
	
	private List<Part> falseParts;

	public String getName() {
		return name;
	}

	public List<Part> getTrueParts() {
		return trueParts;
	}

	public void setTrueParts(List<Part> trueParts) {
		this.trueParts = trueParts;
	}

	public List<Part> getFalseParts() {
		return falseParts;
	}

	public void setFalseParts(List<Part> falseParts) {
		this.falseParts = falseParts;
	}
	
	public IfPart(String name) {
		this.name = name;
	}

	@Override
	public void output(Writer writer, ValueProvider vp) throws IOException {
		List<Part> parts = vp.ifValue(name) ? trueParts : falseParts;
		if (!Utils.isNullOrEmpty(parts))
			for (Part child : parts)
				child.output(writer, vp);
	}

}
