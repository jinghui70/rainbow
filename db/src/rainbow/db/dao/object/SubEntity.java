package rainbow.db.dao.object;

/**
 * IdObject的子对象描述
 * 
 * @author lijinghui
 * 
 */
public class SubEntity {

	/**
	 * 子对象名
	 */
	private String name;

	/**
	 * 子对象指向id的属性名
	 */
	private String property;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public SubEntity(String name, String property) {
		this.name = name;
		this.property = property;
	}

}
