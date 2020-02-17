package rainbow.db.query;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QueryResult {
	
	/**
	 * 数据列表
	 */
	private List<Map<String, Object>> data = Collections.emptyList();
	
	/**
	 * 分页需要的总数数据
	 */
	private Integer count;

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
}
