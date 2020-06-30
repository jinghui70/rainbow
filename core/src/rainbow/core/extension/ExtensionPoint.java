package rainbow.core.extension;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.base.Objects;

import rainbow.core.model.IAdaptable;
import rainbow.core.model.object.INameObject;
import rainbow.core.util.Utils;

/**
 * 系统扩展点
 * 
 * @author lijinghui
 * 
 */
public class ExtensionPoint {

	private static Comparator<Extension> c = new Comparator<Extension>() {
		@Override
		public int compare(Extension e1, Extension e2) {
			if (e1.getOrder() == 0) {
				return e2.getOrder() == 0 ? 0 : 1;
			} else if (e2.getOrder() == 0)
				return -1;
			return Integer.compare(e1.getOrder(), e2.getOrder());
		}
	};

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

	Extension addExtension(String bundle, String name, int order, Object object) {
		Object eo = null;
		if (clazz.isInstance(object))
			eo = object;
		else if (object instanceof IAdaptable)
			eo = ((IAdaptable) object).getAdapter(clazz);
		checkNotNull(eo, "invalid extension object {}, register Extension {} failed", object.getClass().getName(),
				clazz.getSimpleName());

		if (Utils.isNullOrEmpty(name)) {
			if (eo instanceof INameObject) {
				name = ((INameObject) eo).getName();
			} else {
				name = eo.getClass().getName();
			}
		}
		for (Extension e : extensions) {
			checkArgument(!name.equals(e.getName()), "duplicated extension name '{}' of {}", name,
					clazz.getSimpleName());
		}
		Extension extension = new Extension(bundle, this, order, eo);
		extension.setName(name);
		extensions.add(extension);
		extensions.sort(c);
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
	public Optional<Extension> getExtension(String name) {
		return extensions.parallelStream().filter(e -> Objects.equal(e.getName(), name)).findFirst();
	}

	/**
	 * 获取指定名字的扩展对象
	 * 
	 * @param name
	 * @return
	 */
	public Object getExtensionObject(String name) {
		return getExtension(name).map(Extension::getObject).orElse(null);
	}

}
