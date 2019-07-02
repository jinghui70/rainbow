package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.util.Objects;

import rainbow.core.util.Utils;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.model.DataType;

public class SelectField {

	private String function;

	private String alias;

	private Link link;

	private String refinery;

	private String refineryParam;

	private Column column;

	private String patch; // 临时加的补丁以支持NOW和COUNT(1),等待未来函数的进一步支持

	private SelectField() {
	}

	public String getFunction() {
		return function;
	}

	public Column getColumn() {
		return column;
	}

	public String getAlias() {
		return alias;
	}

	public Link getLink() {
		return link;
	}

	public boolean isManyLink() {
		return link != null && link.isMany();
	}

	public String getRefinery() {
		return refinery;
	}

	public String getRefineryParam() {
		return refineryParam;
	}

	public String getName() {
		if (alias != null)
			return alias;
		if (link == null)
			return column.getName();
		return new StringBuilder(link.getName()).append('.').append(column.getName()).toString();
	}

	public String getNameWithOutLink() {
		return alias == null ? column.getName() : alias;
	}

	public void toSql(Sql sql, Select context) {
		if (patch != null) {
			sql.append(patch);
			return;
		}
		if (function != null)
			sql.append(function).append("(");
		if (context.isLinkSql())
			sql.append(link == null ? 'A' : context.getLinkAlias(link)).append('.');
		sql.append(column.getCode());
		if (function != null)
			sql.append(')');
		if (alias != null)
			sql.append(" AS ").append(alias);
	}

	public void toGroupBySql(Sql sql, Select context) {
		if (alias != null) {
			sql.append(alias);
		} else {
			if (context.isLinkSql())
				sql.append(link == null ? 'A' : context.getLinkAlias(link)).append('.');
			sql.append(column.getCode());
		}
	}

	public static SelectField fromColumn(Column column) {
		SelectField field = new SelectField();
		field.column = column;
		return field;
	}

	private void parseRefinery(String str) {
		this.refinery = Utils.substringBefore(str, "(");
		this.refineryParam = Utils.substringBetween(str, "(", ")");
	}

	/**
	 * 这个是用于select字段的解析
	 * 
	 * @param str
	 * @param entity
	 * @return
	 */
	public static SelectField parse(String str, Select context) {
		SelectField field = new SelectField();
		// 首先判断是否有加工
		String[] f = Utils.split(str, '|');
		if (f.length > 1) {
			str = f[0];
			field.parseRefinery(f[1]);
		}
		// 判断是否有别名
		int inx = str.indexOf(':');
		if (inx > 0) {
			field.alias = str.substring(inx + 1);
			str = str.substring(0, inx);
		}
		// 判断是否是目前函数补丁
		if (str.equals(Dao.NOW) || str.equalsIgnoreCase(Dao.COUNT)) {
			field.patch = str;
			return field;
		}
		// 判断函数
		inx = str.indexOf('(');
		if (inx > 0) {
			field.function = str.substring(0, inx);
			str = str.substring(inx + 1);
			inx = str.indexOf(')');
			str = str.substring(0, inx);
		}
		// 判断是否链接字段
		inx = str.indexOf('.');
		if (inx > 0) {
			field.link = context.parseLink(str.substring(0, inx));
			checkNotNull(field.link, "link {} not defined", str);
			str = str.substring(inx + 1);
			field.column = field.link.getTargetEntity().getColumn(str);
			checkNotNull(field.column, "link column {} not defined", str);
		} else {
			Entity entity = context.getEntity();
			field.column = checkNotNull(entity.getColumn(str), "column {} of entity {} not defined", str,
					entity.getName());
		}
		return field;
	}

	public boolean matchGroupBy(String name) {
		if (Objects.equals(alias, name))
			return true;
		if (function != null)
			return false;

		String linkStr = null;
		String nameStr = null;
		String[] f = Utils.split(name, '.');
		if (f.length == 1) {
			nameStr = f[0];
		} else {
			linkStr = f[0];
			nameStr = f[1];
		}
		if (!Objects.equals(column.getName(), nameStr))
			return false;
		if (link == null)
			return linkStr == null;
		else
			return Objects.equals(link.getName(), linkStr);
	}

	public DataType getType() {
		if (function != null) {
			switch (function.toUpperCase()) {
			case "COUNT":
				return DataType.INT;
			}
		}
		return column.getType();
	}
}
