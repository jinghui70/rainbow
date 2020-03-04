package rainbow.core.util.converter;

/**
 * 对象构建器
 * 
 * @author lijinghui
 *
 * @param <T>
 */
public interface DataMaker<T> {

	/**
	 * 初始化一个对象
	 * 
	 * @return
	 */
	T makeInstance();

	/**
	 * 为对象的一个属性设值
	 * 
	 * @param object 待设值对象
	 * @param key    属性名
	 * @param value  值
	 */
	void setValue(T object, String key, Object value);

}