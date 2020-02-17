package rainbow.db.query;

import static rainbow.core.util.Preconditions.*;
import rainbow.core.util.Utils;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;

public class SelectField {

	private String alias;

	private Link link;

	private String refinery;

	private String refineryParam;

	private Column column;

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

	public String getRefinery() {
		return refinery;
	}

	public void setRefinery(String refinery) {
		this.refinery = refinery;
	}

	public String getRefineryParam() {
		return refineryParam;
	}

	public void setRefineryParam(String refineryParam) {
		this.refineryParam = refineryParam;
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
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

	public void toSql(Sql sql, QueryAnalyzer context) {
		if (context != null && context.isLinkSql())
			sql.append(link == null ? 'A' : context.getLinkAlias(link)).append('.');
		sql.append(column.getCode());
		if (alias != null)
			sql.append(" AS ").append(alias);
	}

	private void parseRefinery(String str) {
		this.refinery = Utils.substringBefore(str, "(");
		this.refineryParam = Utils.substringBetween(str, "(", ")");
	}

	public static SelectField parse(String str, Entity entity) {
		SelectField field = new SelectField();
		String[] f = Utils.split(str, '|');
		if (f.length > 1) {
			str = f[0];
			field.parseRefinery(f[1]);
		}
		int inx = str.indexOf(':');
		if (inx > 0) {
			field.alias = str.substring(inx + 1);
			str = str.substring(0, inx);
		}
		inx = str.indexOf('.');
		if (inx > 0) {
			field.link = entity.getLink(str.substring(0, inx));
			str = str.substring(inx + 1);
			field.column = field.link.getTargetEntity().getColumn(str);
		} else
			field.column = checkNotNull(entity.getColumn(str), "column {} of entity {} not defined", str, entity.getName());
		return field;
	}

}