package rainbow.db.refinery;

import rainbow.core.model.object.INameObject;
import rainbow.db.dao.model.Column;

/**
 * 数据加工厂
 * 
 * @author lijinghui
 *
 */
public interface Refinery extends INameObject {

	/**
	 * 返回是否能加工指定的Column，如果可以加工，返回加工描述用来确定参数，否则返回空
	 * 
	 * @param column
	 * @return
	 */
	RefineryDef accept(Column column);

	/**
	 * 加工具体的数据
	 * 
	 * @param column
	 * @param data   加工的数据对象
	 * @param key    加工属性名
	 * @param param  加工参数
	 * @return
	 */
	Object refine(Column column, Object data, String param);

	default RefineryDef makeDef(boolean canInput, String... params) {
		return new RefineryDef(this, canInput, params);
	}

}
