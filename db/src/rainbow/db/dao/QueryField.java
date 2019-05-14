package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkNotNull;
import static rainbow.core.util.Preconditions.checkState;

import java.util.Optional;

import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;

/**
 * 这是条件、orderby groupby部分的字段，这种字段应该没有别名，但是可能会用到select里面的别名
 * 
 * @author lijinghui
 *
 */
public class QueryField {

	private Column column;

	private String alias;

	private Link link;

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

	public boolean isManyLink() {
		return link != null && link.isMany();
	}

	public String getName() {
		if (alias != null)
			return alias;
		if (link == null)
			return column.getName();
		return new StringBuilder(link.getName()).append('.').append(column.getName()).toString();
	}

	public void toSql(Sql sql, SelectBuildContext context) {
		if (alias != null) {
			sql.append(alias);
		} else {
			if (context.isLinkSql())
				sql.append(link == null ? 'A' : context.getLinkAlias(link)).append('.');
			sql.append(column.getCode());
		}
	}

	/**
	 * 解析
	 * 
	 * @param str
	 * @param context
	 * @return
	 */
	public static QueryField parse(String str, SelectBuildContext context) {
		QueryField field = new QueryField();
		Entity entity = context.getEntity();
		int inx = str.indexOf('.');
		if (inx > 0) {
			field.link = entity.getLink(str.substring(0, inx));
			checkNotNull(field.link, "link {} of entity {} not defined", str, entity.getName());
			str = str.substring(inx + 1);
			field.column = field.link.getTargetEntity().getColumn(str);
		} else {
			field.column = entity.getColumn(str);
			if (field.column == null) {
				Optional<SelectField> sf = context.alias2selectField(str);
				checkState(sf.isPresent(), "bad field {}", str);
				field.alias = str;
				field.column = sf.get().getColumn();
			}
		}
		return field;
	}

}
