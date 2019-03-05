package rainbow.db.dao;

import java.util.Collections;
import java.util.List;

/**
 * 分页查询用数据封装对象
 * 
 * @param <T>
 */
public class PageData<T> {

	/**
	 * 查询的总记录数
	 */
	private int total;

	/**
	 * 查询的结果列表
	 */
	private List<T> rows;

	public PageData() {
	    total = 0;
	    rows = Collections.emptyList();
	}

	public PageData(int count, List<T> data) {
		this.total = count;
		this.rows = data;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	
	public boolean isEmpty() {
		return rows==null || rows.isEmpty();
	}

}
