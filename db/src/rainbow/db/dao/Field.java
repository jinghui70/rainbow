package rainbow.db.dao;

import static rainbow.core.util.Preconditions.*;
import java.util.function.Function;

import rainbow.core.util.Utils;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;

public class Field {

	private String function;

	private Column column;

	private String alias;

	private Link link;

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

	public void toSql(Sql sql, Function<Link, String> linkToAlias) {
		sql.append(linkToAlias.apply(link));
		sql.append(column.getCode());
		if (alias != null)
			sql.append(" AS ").append(alias);
	}

	public String getName() {
		return alias != null ? alias : column.getName();
	}

	public static Field fromColumn(Column column) {
		Field field = new Field();
		field.column = column;
		return field;
	}

	public static Field parse(String str, Entity entity) {
		Field field = new Field();
		String[] f = Utils.split(str, ':');
		if (f.length > 1) {
			str = f[0];
			field.alias = f[1];
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

}
