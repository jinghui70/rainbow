package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkNotNull;
import static rainbow.core.util.Preconditions.checkState;

import java.util.Optional;

import com.google.common.base.Objects;

import rainbow.core.util.Utils;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;

public class Field {

	private String function;

	private Column column;

	private String alias;

	private Link link;

	private String patch; // 临时加的补丁以支持NOW和COUNT(1),等待未来函数的进一步支持

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
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

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public String getName() {
		if (alias != null)
			return alias;
		if (link == null)
			return column.getName();
		return new StringBuilder(link.getName()).append('.').append(column.getName()).toString();
	}

	public void toSelectSql(Sql sql, SelectBuildContext context) {
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

	public void toSql(Sql sql, SelectBuildContext context) {
		if (patch != null) {
			sql.append(patch);
			return;
		}
		if (function != null)
			sql.append(function).append("(");
		if (alias != null) {
			sql.append(alias);
		} else {
			if (context.isLinkSql())
				sql.append(link == null ? 'A' : context.getLinkAlias(link)).append('.');
			sql.append(column.getCode());
		}
		if (function != null)
			sql.append(')');
	}

	public boolean match(String linkStr, String nameStr) {
		if (linkStr == null && Objects.equal(alias, nameStr))
			return true;
		if (!Objects.equal(column.getName(), nameStr))
			return false;
		if (link == null)
			return linkStr == null;
		else
			return Objects.equal(link.getName(), linkStr);
	}

	public static Field fromColumn(Column column) {
		Field field = new Field();
		field.column = column;
		return field;
	}

	/**
	 * 这个是用于select字段的解析
	 * 
	 * @param str
	 * @param entity
	 * @return
	 */
	public static Field parse(String str, Entity entity) {
		Field field = new Field();
		String[] f = Utils.split(str, ':');
		if (f.length > 1) {
			str = f[0];
			field.alias = f[1];
		}
		if (str.equals(Dao.NOW) || str.equalsIgnoreCase(Dao.COUNT)) {
			field.patch = str;
			return field;
		}
		int inx = str.indexOf('(');
		if (inx > 0) {
			field.function = str.substring(0, inx);
			str = str.substring(inx + 1);
			inx = str.indexOf(')');
			str = str.substring(0, inx);
		}
		inx = str.indexOf('.');
		if (inx > 0) {
			field.link = entity.getLink(str.substring(0, inx));
			checkNotNull(field.link, "link {} of entity {} not defined", str, entity.getName());
			str = str.substring(inx + 1);
			field.column = field.link.getTargetEntity().getColumn(str);
		} else
			field.column = entity.getColumn(str);
		return field;
	}

	/**
	 * 这个是用于条件字段的解析,这种字段应该没有别名，但是可能会用到select里面的别名
	 * 
	 * @param str
	 * @param context
	 * @return
	 */
	public static Field parse(String str, SelectBuildContext context) {
		Field field = new Field();
		int inx = str.indexOf('(');
		if (inx > 0) {
			field.function = str.substring(0, inx);
			str = str.substring(inx + 1);
			inx = str.indexOf(')');
			str = str.substring(0, inx);
		}
		Entity entity = context.getEntity();
		String[] f = Utils.split(str, '.');
		if (f.length > 1) {
			field.link = entity.getLink(f[0]);
			checkNotNull(field.link, "link {} of entity {} not defined", str, entity.getName());
			field.column = field.link.getTargetEntity().getColumn(f[1]);
		} else {
			field.column = entity.getColumn(str);
			if (field.column == null) {
				Optional<Field> sf = context.alias2selectField(str);
				checkState(sf.isPresent(), "bad field {}", str);
				field.alias = str;
				field.column = sf.get().column;
			}
		}
		return field;
	}

}
