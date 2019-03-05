package rainbow.db.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import rainbow.core.util.Utils;
import rainbow.db.dao.model.Entity;
import rainbow.db.model.Column;
import rainbow.db.model.ColumnType;

public class Field {

	private String function;

	private String tableAlias;

	private Column column;

	private String alias;

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getTableAlias() {
		return tableAlias;
	}

	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Field(String tableAlias, Column column) {
		this.tableAlias = tableAlias;
		this.column = column;
	}

	public Field(String str, Entity entity) {
		String name = setString(str);
		column = entity.getColumn(name);
		checkNotNull(column, "column [%s] not defined in entity[%s]", name, entity.getName());
	}

	public Field(String str, ColumnFinder finder) {
		String name = setString(str);
		column = finder.find(tableAlias, name);
	}

	private String setString(String str) {
		String name;
		str = str.trim();
		int inx = str.toLowerCase().indexOf(" as ");
		if (inx > 0) {
			alias = str.substring(inx + 4);
			str = str.substring(0, inx);
		}
		inx = str.indexOf('(');
		if (inx > 0) {
			function = str.substring(0, inx);
			str = str.substring(inx + 1);
			inx = str.indexOf(')');
			checkArgument(inx > 0, "bad select field,')' not found->%s", str);
			str = str.substring(0, inx);
		}
		inx = str.indexOf('.');
		if (inx > 0) {
			tableAlias = str.substring(0, inx);
			name = str.substring(inx + 1);
		} else
			name = str;
		return name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean hasFunction = Utils.hasContent(function);
		if (hasFunction)
			sb.append(function).append("(");
		if (Utils.hasContent(tableAlias))
			sb.append(tableAlias).append(".");
		sb.append(column.getDbName());
		if (hasFunction)
			sb.append(")");
		if (Utils.hasContent(alias))
			sb.append(" AS ").append(alias);
		return sb.toString();
	}

	public ColumnType getDataType() {
		return column.getType();
	}
	
	private String fullName() {
		if (tableAlias == null)
			return column.getName();
		return String.format("%s.%s", tableAlias, column.getName());
	}

	private String fullSqlName() {
		if (tableAlias == null)
			return column.getDbName();
		return String.format("%s.%s", tableAlias, column.getDbName());
	}

	/**
	 * 判断一个属性是否和自己匹配,如果是，返回用来构建sql的字段名
	 * 
	 * @param property
	 * @return
	 */
	public String match(String property) {
		if (Objects.equal(property, alias))
			return alias;
		if (Objects.equal(fullName(), property)) {
			return fullSqlName();
		}
		return null;
	}

}
