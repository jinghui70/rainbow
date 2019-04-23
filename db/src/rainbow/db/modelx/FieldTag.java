package rainbow.db.modelx;

import java.util.List;

import rainbow.core.model.object.NameObject;
import rainbow.core.util.Utils;

public class FieldTag extends NameObject {
	
	private List<TagProperty> properties;

	public List<TagProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<TagProperty> properties) {
		this.properties = properties;
	}

	public boolean hasProperty() {
		return !Utils.isNullOrEmpty(properties);
	}

}
