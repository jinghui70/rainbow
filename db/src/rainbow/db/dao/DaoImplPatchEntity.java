package rainbow.db.dao;

import java.util.List;
import java.util.Map;

public class DaoImplPatchEntity {
	
	private String name;
	
	private Map<String, Object> tagMap;
	
	private List<DaoImplPatchEntity> columns;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getTagMap() {
		return tagMap;
	}

	public void setTagMap(Map<String, Object> tagMap) {
		this.tagMap = tagMap;
	}

	public List<DaoImplPatchEntity> getColumns() {
		return columns;
	}

	public void setColumns(List<DaoImplPatchEntity> columns) {
		this.columns = columns;
	}

}
