package rainbow.db.dao;

import rainbow.db.database.DataSourceConfig;

/**
 * 数据源访问配置对象
 * 
 * @author lijinghui
 *
 */
public class DaoConfig extends DataSourceConfig {

	/**
	 * 描述
	 */
	private String comment;

	/**
	 * 对应的模型文件名
	 */
	private String model;

	/**
	 * 如果物理数据源与另一个相同，则指向那个数据源配置名，那么除了模型文件名和描述，其它属性都没有意义
	 */
	private String ref;

	/**
	 * 数据源类型，MySql、H2等，用来确定数据库方言
	 */
	private String type;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
