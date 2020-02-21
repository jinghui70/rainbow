package rainbow.db.refinery;

import rainbow.core.model.object.NameObject;

/**
 * 数据加工厂
 * 
 * @author lijinghui
 *
 */
public class RefineryDef extends NameObject {

	/**
	 * 是否可以输入参数
	 * 
	 * @return
	 */
	private boolean canInput;

	/**
	 * 预置的参数,缺省的就是第一个
	 */
	private String[] params;

	/**
	 * 表示有参数
	 * 
	 * @param name     加工厂名
	 * @param canInput 用户是否可输入
	 * @param params   可选参数列表
	 */
	public RefineryDef(Refinery refinery, boolean canInput, String... params) {
		super(refinery.getName());
		this.params = params;
		this.canInput = canInput;
	}

	public boolean isCanInput() {
		return canInput;
	}

	public String[] getParams() {
		return params;
	}
}
