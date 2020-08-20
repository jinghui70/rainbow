package rainbow.core.bundle;

import java.util.List;

/**
 * bundle描述对象
 * 
 * @author lijinghui
 * 
 */
public class BundleData {

	/**
	 * bundle编号
	 */
	protected String id;

	/**
	 * bundle 描述
	 */
	protected String desc;

	protected List<String> requires;

	protected String father;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<String> getRequires() {
		return requires;
	}

	public void setRequires(List<String> requires) {
		this.requires = requires;
	}

	public String getFather() {
		return father;
	}

	public void setFather(String father) {
		this.father = father;
	}

}
