package rainbow.core.util.template;

import java.io.IOException;
import java.io.Writer;

public interface Part {

	void output(Writer writer, ValueProvider vp) throws IOException;

}
