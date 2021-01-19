package rainbow.db.database;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.util.Utils;
import rainbow.core.util.json.JSON;
import rainbow.db.dao.Dao;
import rainbow.db.dao.DaoConfig;
import rainbow.db.dao.DaoImpl;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.model.Model;
import rainbow.db.model.Unit;

public class DatabaseUtils {

	public static Dialect dialect(String type) {
		return ExtensionRegistry.getExtensionObject(Dialect.class, type)
				.orElseThrow(() -> new RuntimeException(Utils.format("Dialect [{}] not found!", type)));
	}

	public static HikariDataSource createDataSource(DataSourceConfig config) {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(config.getJdbcUrl());
		hikariConfig.setUsername(config.getUsername());
		hikariConfig.setPassword(config.getPassword());
		Map<String, Object> property = config.getProperty();
		if (Utils.hasContent(property)) {
			property.forEach((key, value) -> hikariConfig.addDataSourceProperty(key, value));
		}
		return new HikariDataSource(hikariConfig);
	}

	public static Dao createDao(DaoConfig config, Map<String, Entity> entityMap) {
		Dialect dialect = dialect(config.getType());
		DataSource dataSource = createDataSource(config);
		return new DaoImpl(dataSource, dialect, entityMap);
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
		return JSON.parseObject(modelFile, Model.class);
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
				if (Utils.hasContent(e.getLinkFields()))
					e.getLinkFields().forEach(link -> {
						entity.addLink(new Link(model, entity, link));
					});
			});
		if (unit.getUnits() != null)
			unit.getUnits().forEach(u -> loadLink(model, u));
	}
}
