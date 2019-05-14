package rainbow.db.refinery;

import java.util.List;

import rainbow.core.model.object.NameObject;

/**
 * 数据加工厂
 * 
 * @author lijinghui
 *
 */
public class RefineryDef extends NameObject {

	/**
	 * 如果有参数的话，是不是只能在预置的列表里面挑
	 * 
	 * @return
	 */
	private boolean listOnly;

	/**
	 * 预置的参数,缺省的就是第一个
	 */
	private List<String> list;

	/**
	 * 构造函数，表示无参数加工厂
	 */
	public RefineryDef(String name) {
		super(name);
	}

	/**
	 * 表示有参数
	 * 
	 * @param name 加工厂名
	 * @param list 可选参数列表
	 * @param listOnly 是否只能从列表选取，false表示用户可输入
	 */
	public RefineryDef(String name, List<String> list, boolean listOnly) {
		super(name);
		this.list = list;
		this.listOnly = listOnly;
	}
	
	public boolean isListOnly() {
		return listOnly;
	}

	public List<String> getList() {
		return list;
	}

}
