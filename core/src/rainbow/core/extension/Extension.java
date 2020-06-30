package rainbow.core.extension;

import rainbow.core.model.object.NameObject;

/**
 * 扩展对象
 * 
 * @author lijinghui
 * 
 */
public class Extension extends NameObject {

	private String bundle;

	private ExtensionPoint point;

	private int order;

	private Object object;

	public Extension(String bundle, ExtensionPoint point, int order, Object object) {
		this.bundle = bundle;
		this.point = point;
		this.order = order;
		this.object = object;
	}

	public String getBundle() {
		return bundle;
	}

	public ExtensionPoint getExtensionPoint() {
		return point;
	}

	public Object getObject() {
		return object;
	}

	public int getOrder() {
		return order;
	}

}
