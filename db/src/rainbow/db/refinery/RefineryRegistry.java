package rainbow.db.refinery;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import rainbow.core.extension.ExtensionRegistry;
import rainbow.db.dao.model.Column;

public class RefineryRegistry {

	public static List<RefineryDef> getRefinery(Column column) {
		List<Refinery> list = ExtensionRegistry.getExtensionObjects(Refinery.class);
		if (list.isEmpty())
			return Collections.emptyList();
		return list.stream().filter(refinery -> refinery.accept(column)).map(Refinery::def)
				.collect(Collectors.toList());
	}

	public static Refinery getRefinery(String name) {
		return ExtensionRegistry.getExtensionObject(Refinery.class, name);
	}
}
