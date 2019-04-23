package rainbow.db.dao.query;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.util.function.Function;

import rainbow.core.util.Utils;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;

/**
 * 
 * 字段描述
 * 
 * @author lijinghui
 *
 */
public class Field {

	private String name;

	private String alias;

	private Column column;

	private Link link;
	
	private Field subField;

	public Field(String field, Entity entity, QueryJoiner joiner) {
		checkArgument(Utils.hasContent(field), "bad field name:{}", field);
		String[] f = Utils.split(field, '|');
		if (f.length == 1) {
			this.name = field;
			this.alias = name;
		} else {
			this.name = f[0];
			this.alias = f[1];
		}
		f = Utils.split(this.name, '.');
		if (f.length == 1) {
			this.column = entity.getColumn(name);
		} else {
			this.link = entity.getLink(f[0]);
			checkNotNull(link, "link define '{}' of entity '{}' not found", f[0], entity.getName());
			checkArgument(link.isOne(), "link type should be one-one: {}", name);
			this.column = link.getLinkEntity().getColumn(f[1]);
		}
		checkNotNull(column, "field not exist: {}", field);
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	public Column getColumn() {
		return column;
	}

	public Link getLink() {
		return link;
	}

	public boolean isLink() {
		return link != null;
	}

	public String getOutputName() {
		return alias == null ? name : alias;
	}

	public void appendFieldToSql(Sql sql, Function<Link, String> linkToAlias) {
		sql.append(linkToAlias.apply(link)).append(column.getDbName());
	}

}
