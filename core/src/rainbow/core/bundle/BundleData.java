package rainbow.core.bundle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 对应bundle.xml的bundle描述对象
 * 
 * @author lijinghui
 * 
 */
@XmlRootElement(name = "bundle")
@XmlAccessorType(XmlAccessType.FIELD)
public class BundleData {

	/**
	 * bundle编号
	 */
	protected String id;

	/**
	 * bundle 描述
	 */
	protected String desc;

	@XmlElement(name = "parent")
	protected String[] parents;

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

	public String[] getParents() {
		return parents;
	}

	public void setParents(String[] parents) {
		this.parents = parents;
	}

	public String getFather() {
		return father;
	}

	public void setFather(String father) {
		this.father = father;
	}

}
