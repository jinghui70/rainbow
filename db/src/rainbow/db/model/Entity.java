package rainbow.db.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Entity", propOrder = { "name", "dbName", "cnName", "columns", "indexes", "comment" })
public class Entity implements Cloneable {

	/**
	 * 实体名
	 */
	@XmlElement(required = true)
	private String name;

	/**
	 * 实体的数据库名字
	 */
	@XmlElement(required = true)
	private String dbName;

	/**
	 * 实体的中文名字
	 */
	@XmlElement(required = true)
	private String cnName;

	/**
	 * 实体的属性列表
	 */
	@XmlElementWrapper(name = "columns")
	@XmlElement(name = "column", required = true)
	private List<Column> columns;

	/**
	 * 实体的索引列表
	 */
	@XmlElementWrapper(name = "indexes")
	@XmlElement(name = "index", required = true)
	private List<Index> indexes;

	@XmlElement
	private String comment;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public List<Column> getColumns() {
		if (columns == null)
			columns = new ArrayList<Column>();
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Index> getIndexes() {
		if (indexes == null)
			indexes = new ArrayList<Index>();
		return indexes;
	}

	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public Entity clone() {
		try {
			Entity entity = (Entity) super.clone();
			List<Column> newColumns = new ArrayList<Column>(getColumns().size());
			for (Column column : getColumns()) {
				newColumns.add(column.clone());
			}
			entity.setColumns(newColumns);
			List<Index> newIndexes = new ArrayList<Index>(getIndexes().size());
			for (Index index : getIndexes()) {
				newIndexes.add(index.clone());
			}
			entity.setIndexes(newIndexes);
			return entity;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return new StringBuilder("Entity [name=").append(name).append("]").toString();
	}

}
