package rainbow.db.refinery;

import java.util.Collections;
import java.util.List;

import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.util.Utils;
import rainbow.db.dao.model.Column;

public class RefineryRegistry {

	public static List<RefineryDef> getRefinery(Column column) {
		List<Refinery> list = ExtensionRegistry.getExtensionObjects(Refinery.class);
		if (list.isEmpty())
			return Collections.emptyList();
		return Utils.transform(list, refinery -> refinery.accept(column));
	}

	public static Refinery getRefinery(String name) {
		return ExtensionRegistry.getExtensionObject(Refinery.class, name);
	}
}
