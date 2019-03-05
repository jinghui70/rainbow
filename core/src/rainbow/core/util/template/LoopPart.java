package rainbow.core.util.template;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class LoopPart implements Part {

	private String name;

	private List<Part> children;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Part> getChildren() {
		return children;
	}

	public void setChildren(List<Part> children) {
		this.children = children;
	}

	public LoopPart(String name, List<Part> children) {
		this.name = name;
		this.children = children;
	}

	@Override
	public void output(Writer writer, ValueProvider vp) throws IOException {
		vp.startLoop(name);
		while (vp.loopNext(name)) {
			for (Part child : children) {
				child.output(writer, vp);
			}
		}
	}

}
