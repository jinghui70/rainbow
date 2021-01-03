package rainbow.db.query;

import java.util.List;

import rainbow.core.util.json.JSON;

/**
 * 通用的查询请求
 * 
 * @author lijinghui
 *
 */
public class QueryRequest extends QueryInfo {

	/**
	 * 查询对象
	 */
	private String entity;

	/**
	 * 如果是树对象，是否以树的方式(非叶子节点有children属性)返回
	 */
	private boolean tree;

	/**
	 * 收缩为子对象的链接
	 */
	private List<String> shrinks;

	/**
	 * 链接对象的查询信息
	 */
	private List<LinkQueryInfo> listProps;

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public boolean isTree() {
		return tree;
	}

	public void setTree(boolean tree) {
		this.tree = tree;
	}

	public List<String> getShrinks() {
		return shrinks;
	}

	public void setShrinks(List<String> shrinks) {
		this.shrinks = shrinks;
	}

	public List<LinkQueryInfo> getListProps() {
		return listProps;
	}

	public void setListProps(List<LinkQueryInfo> listProps) {
		this.listProps = listProps;
	}

	public static QueryRequest parse(String str) {
		return JSON.parseObject(str, QueryRequest.class);
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
