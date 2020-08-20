package rainbow.db.database;

import rainbow.core.model.object.INameObject;

/**
 * 数据源访问配置对象
 * 
 * @author lijinghui
 *
 */
public class DaoConfig extends DataSourceConfig implements INameObject {

	private String name;

	/**
	 * 数据源类型，MySql、H2等，用来确定数据库方言
	 */
	private String type;

	/**
	 * 对应的模型文件名
	 */
	private String model;

	/**
	 * 数据库连接等同于另一格数据源配置
	 */
	private String physicSource;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPhysicSource() {
		return physicSource;
	}

	public void setPhysicSource(String physicSource) {
		this.physicSource = physicSource;
	}

}
