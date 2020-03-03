package rainbow.db.dao;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import rainbow.core.model.object.Tree;
import rainbow.core.model.object.TreeNode;
import rainbow.core.util.Utils;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.model.DataType;
import rainbow.db.model.Model;
import rainbow.db.model.Unit;

public abstract class DaoUtils {

	private static final Logger logger = LoggerFactory.getLogger(DaoUtils.class);

	public static Object getResultSetValue(ResultSet rs, int index, DataType dataType) throws SQLException {
		Object value = null;
		boolean wasNullCheck = false;
		switch (dataType) {
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

	/**
	 * 读取rdmx文件并解析
	 * 
	 * @param modelFile
	 * @return
	 */
	public static HashMap<String, Entity> resolveModel(Path modelFile) {
		Model model = loadModel(modelFile);
		return resolveModel(model);
	}

	/**
	 * 读取rdmx文件
	 * 
	 * @param modelFile
	 * @return
	 */
	public static Model loadModel(Path modelFile) {
		try (InputStream is = Files.newInputStream(modelFile)) {
			return JSON.parseObject(is, StandardCharsets.UTF_8, Model.class);
		} catch (Exception e) {
			logger.error("load rdmx file {} faild", modelFile.toString());
			throw new RuntimeException(e);
		}

	}

	/**
	 * 解析一个model
	 * 
	 * @param model
	 * @return
	 */
	public static HashMap<String, Entity> resolveModel(Model model) {
		HashMap<String, Entity> result = new HashMap<String, Entity>();
		loadUnit(result, model);
		loadLink(result, model);
		return result;
	}

	private static void loadUnit(Map<String, Entity> model, Unit unit) {
		if (unit.getTables() != null)
			unit.getTables().stream().map(Entity::new).forEach(e -> model.put(e.getName(), e));
		if (unit.getUnits() != null)
			unit.getUnits().forEach(u -> loadUnit(model, u));
	}

	private static void loadLink(Map<String, Entity> model, Unit unit) {
		if (unit.getTables() != null)
			unit.getTables().forEach(e -> {
				Entity entity = model.get(e.getName());
				// linkField
				if (!Utils.isNullOrEmpty(e.getLinkFields()))
					e.getLinkFields().forEach(link -> {
						entity.addLink(new Link(model, entity, link));
					});
			});
		if (unit.getUnits() != null)
			unit.getUnits().forEach(u -> loadLink(model, u));
	}

	/**
	 * 转换一堆实体为DDL
	 * 
	 * @param entities
	 * @return
	 */
	public static String transform(Collection<Entity> entities) {
		StringBuilder sb = new StringBuilder();
		entities.forEach(entity -> doTransform(sb, entity));
		return sb.toString();
	}

	/**
	 * 转换一个实体为DDL字符串
	 * 
	 * @param entity
	 * @return
	 */
	public static String transform(Entity entity) {
		StringBuilder sb = new StringBuilder();
		doTransform(sb, entity);
		return sb.toString();
	}

	private static void doTransform(StringBuilder sb, Entity entity) {
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
			sb.append(")");
		}
		sb.append(");");
	}

	/**
	 * 把一个NeoBean列表转为树结构
	 * 
	 * @param data
	 * @param strict 严格模式根结点的pid必须为空
	 * @return
	 */
	public static Tree<NeoBean> makeTree(List<NeoBean> data, boolean strict) {
		Map<String, TreeNode<NeoBean>> map = new HashMap<String, TreeNode<NeoBean>>();
		List<TreeNode<NeoBean>> roots = new LinkedList<TreeNode<NeoBean>>();
		data.forEach(v -> map.put(v.getString("id"), new TreeNode<NeoBean>(v)));
		data.forEach(v -> {
			String id = v.getString("id");
			String pid = v.getString("pid");
			TreeNode<NeoBean> node = map.get(id);
			if (Utils.isNullOrEmpty(pid))
				roots.add(node);
			else {
				TreeNode<NeoBean> parent = map.get(pid);
				if (parent == null) {
					if (!strict)
						roots.add(node);
				} else
					parent.addChild(node);
			}
		});
		return new Tree<NeoBean>(roots, map);
	}

}