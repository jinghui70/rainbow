package rainbow.db.query;

import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;

public class QueryField {

	private Link link;
	
	private String name;
	
	private Column column;

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}
	
	public void toSql(Sql sql, QueryAnalyzer context) {
		if (context!=null && context.isLinkSql())
			sql.append(link == null ? 'A' : context.getLinkAlias(link)).append('.');
		sql.append(column.getCode());
	}
	
	public static QueryField parse(String str, Entity entity) {
		QueryField field = new QueryField();
		int inx = str.indexOf('.');
		if (inx > 0) {
			field.link = entity.getLink(str.substring(0, inx));
			str = str.substring(inx + 1);
			field.column = field.link.getTargetEntity().getColumn(str);
		} else
			field.column = entity.getColumn(str);
		return field;
	}

}
