package rainbow.db.dao.object;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.base.Function;

import rainbow.core.model.IAdaptable;
import rainbow.core.model.object.INameObject;
import rainbow.db.dao.Dao;
import rainbow.db.object.ObjectType;
import rainbow.db.object.ObjectTypeAdapter;

/**
 * 封装一个具体对象的数据库操作类，该对象对应的数据表只有一个主键字段，如果对应多个字段，可以派生此类
 * 
 * @author lijinghui
 * 
 * @param <I>
 *            主键对象类型
 * @param <T>
 *            对象类型
 */
public class KeyObjectDao<I, T> extends ObjectDao<T> implements Function<I, T>, IAdaptable {

	protected Class<I> keyClazz;

	/**
	 * 构造函数,需要派生子类作为Bean在容器中生成，
	 * 
	 * @param clazz
	 */
	protected KeyObjectDao(Class<T> clazz) {
		super(clazz);
	}

	/**
	 * 这个构造函数用于直接创建使用
	 * 
	 * @param dao
	 * @param clazz
	 *            对象类
	 */
	public KeyObjectDao(Dao dao, Class<T> clazz) {
		super(dao, clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setClass(Class<T> clazz) {
		super.setClass(clazz);
		// 解析KeyClass
		Type type = clazz;
		Class<?> tmpClass;
		do {
			tmpClass = (Class<?>) type;
			type = tmpClass.getGenericSuperclass();
		} while (!(type instanceof ParameterizedType));
		ParameterizedType pt = (ParameterizedType) type;
		keyClazz = (Class<I>) pt.getActualTypeArguments()[0];
		checkNotNull(keyClazz, "cannot retrieve key class of [%s]", clazz.getSimpleName());
	}

	/**
	 * 返回指定主键的对象
	 * 
	 * @param id
	 * @return
	 */
	public T fetch(I key) {
		return super.fetch(key);
	}

	@Override
	public T apply(I input) {
		return fetch(input);
	}
	
	@Override
	public Object getAdapter(Class<?> adapter) {
		if (adapter == ObjectType.class) {
			return getObjectType();
		}
		return null;
	}

	/**
	 * 返回ObjectType适配对象
	 * 
	 * @return
	 */
	protected ObjectType getObjectType() {
		if (INameObject.class.isAssignableFrom(clazz)) {
			return new ObjectTypeAdapter() {
				@Override
				public String getName() {
					return entityName;
				}

				@SuppressWarnings("unchecked")
				@Override
				public String getObjectName(Object key) {
					T obj = fetch((I) key);
					return ((INameObject) obj).getName();
				}

			};
		}
		return null;
	}

}
