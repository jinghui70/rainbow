package rainbow.db.refinery;

import java.util.Map;

import rainbow.core.model.object.INameObject;
import rainbow.db.dao.model.Column;

/**
 * 数据加工厂
 * 
 * @author lijinghui
 *
 */
public interface Refinery extends INameObject {

	@Override
	default String getName() {
		return def().getName();
	}

	/**
	 * 返回加工厂描述对象
	 * 
	 * @return
	 */
	RefineryDef def();

	/**
	 * 返回是否能加工指定的Column
	 * 
	 * @param column
	 * @return
	 */
	boolean accept(Column column);

	/**
	 * 加工具体的数据
	 * 
	 * @param column
	 * @param data  加工的数据对象
	 * @param key   加工属性名
	 * @param param 加工参数
	 * @return
	 */
	void refine(Column column, Map<String, Object> data, String key, String param);

}
