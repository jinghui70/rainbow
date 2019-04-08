package rainbow.db.dao;

import java.util.List;
import java.util.Map;

public class DaoImplPatchEntity {
	
	private String name;
	
	private Map<String, Object> tags;
	
	private List<DaoImplPatchEntity> columns;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getTags() {
		return tags;
	}

	public void setTags(Map<String, Object> tags) {
		this.tags = tags;
	}

	public List<DaoImplPatchEntity> getColumns() {
		return columns;
	}

	public void setColumns(List<DaoImplPatchEntity> columns) {
		this.columns = columns;
	}

}
