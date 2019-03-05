package rainbow.db.dao;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Property {

	private Property parent;

	private Method readMethod;

	private Method writeMethod;

	private Class<?> type;

	private Class<?> subType;

	private int index = -1;

	public Property(Class<?> type) {
		this.type = type;
	}

	public Property(Property parent, int index) {
		this.parent = parent;
		this.type = parent.getSubType();
		this.index = index;
	}

	public Property getParent() {
		return parent;
	}

	public void setParent(Property parent) {
		this.parent = parent;
	}

	public Method getReadMethod() {
		return readMethod;
	}

	public void setReadMethod(Method readMethod) {
		this.readMethod = readMethod;
	}

	public Method getWriteMethod() {
		return writeMethod;
	}

	public void setWriteMethod(Method writeMethod) {
		this.writeMethod = writeMethod;
	}

	public Class<?> getType() {
		return type;
	}

	public Class<?> getSubType() {
		return subType;
	}

	public void setSubType(Class<?> subType) {
		this.subType = subType;
	}

	public int getIndex() {
		return index;
	}

	public boolean hasSubType() {
		return subType != null;
	}

	/**
	 * 返回父属性所指向的子对象，如果是数组，应该在类实例初始化的时候就创建好，否则这里不知道长度无法创建。
	 * 
	 * @param obj
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws Exception
	 */
	private Object getSubObject(Object obj)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Object result = parent.readMethod.invoke(obj);
		if (result == null) {
			result = parent.type.newInstance();
			parent.writeMethod.invoke(obj, result);
		}
		return result;
	}

	public void setValue(Object obj, Object value) {
		try {
			if (parent == null) {
				writeMethod.invoke(obj, value);
			} else {
				obj = getSubObject(obj);
				if (index > 0)
					Array.set(obj, index - 1, value);
				else
					writeMethod.invoke(obj, value);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	public Object getValue(Object obj) {
		try {
			if (parent == null)
				return readMethod.invoke(obj);
			obj = getSubObject(obj);
			if (index > 0) {
				return Array.get(obj, index - 1);
			} else
				return readMethod.invoke(obj);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

}
