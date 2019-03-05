package rainbow.db.dao;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;

import rainbow.core.util.Consumer;
import rainbow.core.util.Utils;
import rainbow.core.util.converter.Converters;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.model.Entity;
import rainbow.db.model.Column;

/**
 * 封装了一个Sql的内容对象
 * 
 * @author lijinghui
 * 
 */
public class Sql implements Appendable {

	private StringBuilder sb = new StringBuilder();

	private List<Object> params;

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
		sb.append(obj.toString());
		return this;
	}
	
	public Sql append(Sql sql) {
		sb.append(sql.getStringBuilder());
		this.params.addAll(sql.getParams());
		return this;
	}
	
	public Sql append(char ch) {
		sb.append(ch);
		return this;
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
		checkArgument(entity.getKeyCount() > 0, "entity[%s] has no key", entity.getName());
		Utils.join(" AND ", sb, entity.getKeys(), new Consumer<Column>() {
			@Override
			public void consume(Column column) {
				append(column.getDbName()).append("=?").addParam(neo.getObject(column));
			}
		});
		return this;
	}
	
	public Sql whereKey(Entity entity, Object... values) {
		checkArgument(entity.getKeyCount() > 0, "entity[%s] has no key", entity.getName());
		checkArgument(entity.getKeyCount() == values.length, "param size(%s) not match key size(%s) of entity [%s]",
				values.length, entity.getKeyCount(), entity.getName());
		int index = 0;
		for (Column column : entity.getKeys()) {
			if (index == 0) {
				sb.append(" where ");
			} else {
				sb.append(" and ");
			}
			Object param = values[index++];
			param = Converters.convert(param, column.getType().dataClass());
			append(column.getDbName()).append("=?").addParam(param);
		}
		return this;
	}

	public Sql whereCnd(Function<String, Field> fieldFunction, C cnd) {
		if (cnd == null || cnd.isEmpty())
			return this;
		append(" where ");
		cnd.toSql(fieldFunction, this);
		return this;
	}

	/**
	 * 取前几行
	 * 
	 * @param dao
	 * @param limit
	 * @return
	 */
	public Sql limit(Dao dao, int limit) {
		if (limit > 0)
			setSql(dao.getDatabaseDialect().wrapLimitSql(getSql(), limit));
		return this;
	}

	/**
	 * 分页
	 * 
	 * @param dao
	 * @param pager
	 * @return
	 */
	public void paging(Dao dao, Pager pager) {
		if (pager != null) {
			if (pager.getPage() == 1)
				setSql(dao.getDatabaseDialect().wrapLimitSql(getSql(), pager.getLimit()));
			else
				setSql(dao.getDatabaseDialect().wrapPagedSql(getSql(), pager));
		}
	}

	/**
	 * 分页
	 * 
	 * @param dao
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public void paging(Dao dao, int pageNo, int pageSize) {
		paging(dao, Pager.make(pageNo, pageSize));
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

	@Override
	public Sql append(CharSequence csq) {
		sb.append(csq);
		return this;
	}

	@Override
	public Sql append(CharSequence csq, int start, int end) {
		sb.append(csq, start, end);
		return this;
	}

}
