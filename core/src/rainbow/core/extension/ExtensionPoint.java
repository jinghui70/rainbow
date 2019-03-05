package rainbow.core.extension;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import rainbow.core.model.IAdaptable;
import rainbow.core.model.exception.AppException;
import rainbow.core.model.object.INameObject;

/**
 * 系统扩展点
 * 
 * @author lijinghui
 * 
 */
public class ExtensionPoint {

	/**
	 * 扩展点的bundle
	 */
	private String bundle;

	/**
	 * 扩展点接口类
	 */
	private Class<?> clazz;

	/**
	 * 扩展点上的所有扩展
	 */
	private List<Extension> extensions = new CopyOnWriteArrayList<Extension>();

	public ExtensionPoint(String bundle, Class<?> clazz) {
		this.bundle = bundle;
		this.clazz = clazz;
	}

	public String getBundle() {
		return bundle;
	}

	Extension addExtension(String bundle, String name, Object object) {
		Object eo = null;
		if (clazz.isInstance(object))
			eo = object;
		else if (object instanceof IAdaptable)
			eo = ((IAdaptable) object).getAdapter(clazz);
		else if (object instanceof Factory) {
			if (clazz.isAssignableFrom(((Factory) object).getClazz())) {
				eo = object;
			}
		}
		checkNotNull(eo, "invalid extension object[%s], register Extension[%s] failed", object.getClass().getName(),
				clazz.getSimpleName());

		if (name == null) {
			if (eo instanceof INameObject) {
				name = ((INameObject) eo).getName();
			} else {
				name = eo.getClass().getName();
			}
		}
		for (Extension e : extensions) {
			checkArgument(!name.equals(e.getName()), "duplicated extension name[%s] of [%s]", name,
					clazz.getSimpleName());
		}
		Extension extension = new Extension(bundle, this, eo);
		extension.setName(name);
		extensions.add(extension);
		return extension;
	}

	void removeExtension(Extension extension) {
		extensions.remove(extension);
	}

	public List<Extension> getExtensions() {
		return extensions;
	}

	/**
	 * 获取指定名字的扩展
	 * 
	 * @param name
	 * @return
	 */
	public Extension getExtension(String name) {
		for (Extension extension : extensions) {
			if (extension.getName().equals(name))
				return extension;
		}
		return null;
	}

	/**
	 * 获取指定名字的扩展对象
	 * 
	 * @param name
	 * @return
	 */
	public Object getExtensionObject(String name) throws AppException {
		Extension extension = getExtension(name);
		if (extension == null)
			return null;
		return extension.getObject();
	}
}
