package rainbow.db.query;

import java.util.List;

public class QueryInfo {

	/**
	 * 需要处理的字段列表
	 */
	private List<String> fields;

	/**
	 * 查询条件,目前只支持=,>=,<=,>,<,like,in
	 */
	private List<String> conditions;

	/**
	 * 排序方式
	 */
	private List<String> orders;

	private int pageSize;

	private int pageNo;

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public List<String> getConditions() {
		return conditions;
	}

	public void setConditions(List<String> conditions) {
		this.conditions = conditions;
	}

	public List<String> getOrders() {
		return orders;
	}

	public void setOrders(List<String> orders) {
		this.orders = orders;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

}
