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
	private Integer count;

	/**
	 * 查询的结果列表
	 */
	private List<T> data;

	public PageData() {
	    data = Collections.emptyList();
	}

	public PageData(int count, List<T> data) {
		this.count = count;
		this.data = data;
	}

	public PageData(List<T> data) {
		this.data = data;
	}
	
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

}
