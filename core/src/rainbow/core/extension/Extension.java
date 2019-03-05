package rainbow.core.extension;

import rainbow.core.model.object.SimpleNameObject;

/**
 * 扩展对象
 * 
 * @author lijinghui
 * 
 */
public class Extension extends SimpleNameObject {

	private String bundle;

	private ExtensionPoint point;

	private Object object;

	public Extension(String bundle, ExtensionPoint point, Object object) {
		this.bundle = bundle;
		this.point = point;
		this.object = object;
	}

	public String getBundle() {
		return bundle;
	}

	public ExtensionPoint getExtensionPoint() {
		return point;
	}

	public Object getObject() {
		if (object instanceof Factory)
			return ((Factory) object).createInstance();
		return object;
	}

}
