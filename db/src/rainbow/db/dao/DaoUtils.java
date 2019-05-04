package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import rainbow.core.util.Utils;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.model.Model;
import rainbow.db.model.Tag;
import rainbow.db.model.TagType;
import rainbow.db.model.Unit;

public abstract class DaoUtils {

	public static Object getResultSetValue(ResultSet rs, int index, Column column) throws SQLException {
		Object value = null;
		boolean wasNullCheck = false;
		switch (column.getType()) {
		case SMALLINT:
			value = rs.getShort(index);
			wasNullCheck = true;
			break;
		case INT:
			value = rs.getInt(index);
			wasNullCheck = true;
			break;
		case LONG:
			value = rs.getLong(index);
			wasNullCheck = true;
			break;
		case DOUBLE:
			value = rs.getDouble(index);
			wasNullCheck = true;
			break;
		case NUMERIC:
			value = rs.getBigDecimal(index);
			break;
		case DATE:
			value = rs.getDate(index);
			break;
		case TIME:
			value = rs.getTime(index);
			break;
		case TIMESTAMP:
			value = rs.getTimestamp(index);
			break;
		case CHAR:
		case VARCHAR:
		case CLOB:
			value = rs.getString(index);
			break;
		case BLOB:
			value = rs.getBytes(index);
			break;
		default:
			value = rs.getObject(index);
			break;
		}
		if (wasNullCheck && value != null && rs.wasNull()) {
			value = null;
		}
		return value;
	}

	public static HashMap<String, Entity> loadModel(Model model) {
		HashMap<String, Entity> result = new HashMap<String, Entity>();
		loadUnit(result, model);
		Map<String, Link> linkTags = model.getFieldTags().stream().filter(tag -> tag.getType() == TagType.LINK)
				.collect(Collectors.toMap(Tag::getName, tag -> {
					Link link = new Link();
					Entity targetEntity = checkNotNull(result.get(tag.getLinkTable()));
					Column targetColumn = checkNotNull(targetEntity.getColumn(tag.getLinkField()));
					link.setTargetEntity(targetEntity);
					link.setTargetColumns(ImmutableList.of(targetColumn));
					return link;
				}));
		loadLink(result, linkTags, model);
		return result;
	}

	private static void loadUnit(Map<String, Entity> model, Unit unit) {
		if (unit.getTables() != null)
			unit.getTables().stream().map(Entity::new).forEach(e -> model.put(e.getName(), e));
		if (unit.getUnits() != null)
			unit.getUnits().forEach(u -> loadUnit(model, u));
	}

	private static void loadLink(Map<String, Entity> model, final Map<String, Link> linkTags, Unit unit) {
		if (unit.getTables() != null)
			unit.getTables().forEach(e -> {
				Entity entity = model.get(e.getName());

				Map<String, Link> links = new HashMap<String, Link>();

				// linkField
				if (!Utils.isNullOrEmpty(e.getLinkFields()))
					e.getLinkFields().forEach(link -> {
						links.put(link.getName(), new Link(model, entity, link));
					});

				// linkTag
				if (!linkTags.isEmpty()) {
					entity.getColumns().stream().forEach(column -> {
						if (Utils.isNullOrEmpty(column.getTags()))
							return;
						for (String tagName : column.getTags().keySet()) {
							Link taglink = linkTags.get(tagName);
							if (taglink != null) {
								Link link = new Link();
								link.setName(column.getName());
								link.setLabel(column.getLabel());
								link.setColumns(ImmutableList.of(column));
								link.setTargetEntity(taglink.getTargetEntity());
								link.setTargetColumns(taglink.getTargetColumns());
								links.put(link.getName(), link);
							}
						}
					});
				}
				if (!links.isEmpty())
					entity.setLinks(links);
			});
		if (unit.getUnits() != null)
			unit.getUnits().forEach(u -> loadLink(model, linkTags, u));
	}

	public static String transform(Map<String, Entity> model) {
		StringBuilder sb = new StringBuilder();
		model.values().forEach(entity -> {
			sb.append("CREATE TABLE ").append(entity.getCode()).append("(");
			entity.getColumns().forEach(field -> {
				sb.append(field.getCode()).append("\t").append(field.getType());
				switch (field.getType()) {
				case CHAR:
				case VARCHAR:
					sb.append("(").append(field.getLength()).append(")");
					break;
				case NUMERIC:
					sb.append("(").append(field.getLength()).append(",").append(field.getPrecision()).append(")");
					break;
				default:
					break;
				}
				if (field.isMandatory())
					sb.append(" NOT NULL");
				sb.append(",");
			});
			if (entity.getKeyCount() == 0) {
				sb.setLength(sb.length() - 1);
			} else {
				sb.append("	CONSTRAINT PK_").append(entity.getCode()).append(" PRIMARY KEY(");
				for (Column c : entity.getKeyColumns()) {
					sb.append(c.getCode()).append(",");
				}
				sb.setLength(sb.length() - 1);
			}
			sb.append("));");
		});
		return sb.toString();
	}
}