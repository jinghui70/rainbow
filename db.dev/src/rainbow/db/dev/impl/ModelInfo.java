package rainbow.db.dev.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rainbow.core.platform.Platform;
import rainbow.core.util.Utils;
import rainbow.db.dao.Dao;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.database.DatabaseUtils;
import rainbow.db.dev.api.ColumnNode;
import rainbow.db.dev.api.EntityNode;
import rainbow.db.dev.api.EntityNodeX;
import rainbow.db.dev.api.LinkNode;
import rainbow.db.dev.api.Node;
import rainbow.db.dev.api.UnitNode;
import rainbow.db.model.Model;
import rainbow.db.model.Table;
import rainbow.db.model.Unit;

public class ModelInfo {

	private Map<String, EntityNodeX> entityMap = new HashMap<String, EntityNodeX>();

	private Dao dao;

	private Node tree;

	public Node getTree() {
		return tree;
	}

	public EntityNodeX getEntity(String entity) {
		return entityMap.get(entity);
	}

	public ModelInfo(Dao dao, String modelFileName) {
		this.dao = dao;
		Path modelFile = Platform.getHome().resolve("conf/db").resolve(modelFileName);
		Model model = DatabaseUtils.loadModel(modelFile);
		tree = convertUnit(model);
	}

	private UnitNode convertUnit(Unit unit) {
		UnitNode result = new UnitNode(unit.getLabel());
		List<Node> children = new ArrayList<Node>();
		if (unit.getTables() != null) {
			unit.getTables().forEach(table -> {
				children.add(convertTable(table));
			});
		}
		if (unit.getUnits() != null) {
			unit.getUnits().forEach(child -> {
				children.add(convertUnit(child));
			});
		}
		if (!children.isEmpty())
			result.setChildren(children);
		return result;
	}

	private EntityNode convertTable(Table table) {
		Entity entity = dao.getEntity(table.getName());
		EntityNode result = new EntityNode(table.getName(), table.getLabel());

		if (!Utils.isNullOrEmpty(table.getTags())) {
			List<String> tags = new ArrayList<String>(table.getTags().size());
			tags.addAll(table.getTags().keySet());
			Collections.sort(tags);
			result.setTags(tags);
		}
		EntityNodeX x = new EntityNodeX(result);
		x.setColumns(Utils.transform(entity.getColumns(), this::convertColumn));
		if (Utils.isNullOrEmpty(entity.getLinks()))
			x.setLinks(Collections.emptyList());
		else
			x.setLinks(Utils.transform(entity.getLinks(), this::convertLink));
		this.entityMap.put(result.getName(), x);
		return result;
	}

	private ColumnNode convertColumn(Column column) {
		ColumnNode result = new ColumnNode(column.getName(), column.getLabel());
		result.setType(column.getType());
		if (!Utils.isNullOrEmpty(column.getTags())) {
			List<String> tags = new ArrayList<String>(column.getTags().size());
			tags.addAll(column.getTags().keySet());
			Collections.sort(tags);
			result.setTags(tags);
		}
		return result;
	}

	private LinkNode convertLink(Link link) {
		LinkNode result = new LinkNode(link.getName(), link.getLabel());
		result.setMany(link.isMany());
		List<ColumnNode> columns = Utils.transform(link.getTargetEntity().getColumns(), this::convertColumn);
		result.setColumns(columns);
		return result;
	}

}
