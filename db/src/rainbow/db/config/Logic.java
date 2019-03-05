package rainbow.db.config;

import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.base.MoreObjects;

/**
 * 系统配置的逻辑数据源
 * 
 * @author lijinghui
 * 
 */
public class Logic {

	/**
	 * 对应的模型名称，为空则与id相
	 */
	@XmlAttribute
	private String model;

	@XmlAttribute
	private String physic;

	@XmlAttribute
	private String id;
	
	private String tableSpace;
	
	private String indexSpace;
	
	private String schema;
	
	private String pageSize;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPhysic() {
		return physic;
	}

	public void setPhysic(String physic) {
		this.physic = physic;
	}

	public String getTableSpace() {
		return tableSpace;
	}

	public void setTableSpace(String tableSpace) {
		this.tableSpace = tableSpace;
	}

	public String getIndexSpace() {
		return indexSpace;
	}

	public void setIndexSpace(String indexSpace) {
		this.indexSpace = indexSpace;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("physic", physic).toString();
	}

}
