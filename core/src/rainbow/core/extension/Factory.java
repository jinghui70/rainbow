package rainbow.core.extension;

import rainbow.core.model.object.INameObject;

public class Factory implements INameObject {

	protected Class<?> clazz;

	public Factory(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public String getName() {
		return clazz.getSimpleName();
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Object createInstance() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
