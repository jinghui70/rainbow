package rainbow.core.util.template;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import rainbow.core.util.Utils;

public class SwitchPart implements Part {

	private String name;

	private Map<String, List<Part>> map;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, List<Part>> getMap() {
		return map;
	}

	public void setMap(Map<String, List<Part>> map) {
		this.map = map;
	}

	public SwitchPart(String name, Map<String, List<Part>> map) {
		this.name = name;
		this.map = map;
	}

	@Override
	public void output(Writer writer, ValueProvider vp) throws IOException {
		String key = vp.getSwitchKey(name);
		List<Part> parts = map.get(key);
		if (!Utils.isNullOrEmpty(parts))
			for (Part child : parts)
				child.output(writer, vp);
	}

}
