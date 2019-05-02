package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import rainbow.core.util.Utils;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;

/**
 * 封装了一个Sql的内容对象
 * 
 * @author lijinghui
 * 
 */
public class Sql implements Appendable {

	private StringBuilder sb = new StringBuilder();

	private List<Object> params;

	private String tempStr = null;

	public Sql() {
		params = new ArrayList<Object>();
	}

	public Sql(String str) {
		this();
		append(str);
	}

	public Sql(int size) {
		params = new ArrayList<Object>(size);
	}

	public String getSql() {
		return sb.toString();
	}

	public void setSql(String sql) {
		sb.setLength(0);
		sb.append(sql);
	}

	public StringBuilder getStringBuilder() {
		return sb;
	}

	public Sql append(Object obj) {
		return this.append(obj.toString());
	}

	public Sql append(Sql sql) {
		checkTemp();
		sb.append(sql.getStringBuilder().toString());
		this.params.addAll(sql.getParams());
		return this;
	}

	@Override
	public Sql append(char ch) {
		checkTemp();
		sb.append(ch);
		return this;
	}

	@Override
	public Sql append(CharSequence csq) {
		checkTemp();
		sb.append(csq);
		return this;
	}

	@Override
	public Sql append(CharSequence csq, int start, int end) {
		checkTemp();
		sb.append(csq, start, end);
		return this;
	}

	public Sql append(String str, int times) {
		checkTemp();
		for (int i = 0; i < times; i++)
			sb.append(str);
		return this;
	}

	/**
	 * 临时字符串用来对付 Where and 逗号这样时有时无 的字符串，当后续有append的时候，会被自动append进去
	 * 
	 * @param str
	 * @return
	 */
	public Sql appendTemp(String str) {
		checkTemp();
		this.tempStr = str;
		return this;
	}

	public Sql appendTempComma() {
		return this.appendTemp(",");
	}

	/**
	 * 取消temp中存储的字符串
	 * 
	 * @return
	 */
	public Sql clearTemp() {
		this.tempStr = null;
		return this;
	}

	private void checkTemp() {
		if (tempStr != null)
			sb.append(tempStr);
		tempStr = null;
	}

	public List<Object> getParams() {
		return params;
	}

	public Sql addParam(Object param) {
		params.add(param);
		return this;
	}

	public Sql addParams(List<Object> params) {
		this.params.addAll(params);
		return this;
	}

	public Object[] getParamArray() {
		if (Utils.isNullOrEmpty(params))
			return Utils.NULL_ARRAY;
		else
			return params.toArray();
	}

	public void resetParams() {
		params.clear();
	}

	public boolean noParams() {
		return params.isEmpty();
	}

	public Sql whereKey(NeoBean neo) {
		sb.append(" WHERE ");
		return keyCondition(neo);
	}

	public Sql keyCondition(final NeoBean neo) {
		Entity entity = checkNotNull(neo.getEntity(), "neobean's entity not set");
		checkArgument(entity.getKeyCount() > 0, "entity {} has no key", entity.getName());
		for (Column column : entity.getKeyColumns()) {
			append(column.getCode()).append("=?").addParam(neo.getObject(column));
			appendTemp(" AND ");
		}
		clearTemp();
		return this;
	}

	public Sql whereKey(Entity entity, Object... values) {
		checkArgument(entity.getKeyCount() > 0, "entity {} has no key", entity.getName());
		checkArgument(entity.getKeyCount() == values.length, "param size({}) not match key size({}) of entity {}",
				values.length, entity.getKeyCount(), entity.getName());
		int index = 0;
		appendTemp(" WHERE ");
		for (Column column : entity.getKeyColumns()) {
			Object param = values[index++];
			param = column.convert(param);
			append(column.getCode()).append("=?").addParam(param);
			appendTemp(" AND ");
		}
		clearTemp();
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getSql());
		sb.append("\n\r");
		int i = 1;
		if (!Utils.isNullOrEmpty(params)) {
			sb.append("params:\n\r");
			for (Object param : params) {
				sb.append("[").append(i++).append("] ").append(param.toString()).append("\n\r");
			}
		}
		return sb.toString();
	}

	public Sql whereCnd(Dao dao, Entity entity, C cnd) {
		if (cnd == null || cnd.isEmpty())
			return this;
		append(" where ");
		cnd.toSql(dao, entity, this);
		return this;
	}

}
