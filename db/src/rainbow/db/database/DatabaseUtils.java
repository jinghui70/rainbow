package rainbow.db.database;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.util.Utils;
import rainbow.db.dao.Dao;
import rainbow.db.dao.DaoImpl;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.model.Model;
import rainbow.db.model.Unit;

public class DatabaseUtils {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

	public static Dialect dialect(String type) {
		return ExtensionRegistry.getExtensionObject(Dialect.class, type);
	}

	public static HikariDataSource createDataSource(DataSourceConfig config) {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(config.getJdbcUrl());
		hikariConfig.setUsername(config.getUsername());
		hikariConfig.setPassword(config.getPassword());
		Map<String, Object> property = config.getProperty();
		if (!Utils.isNullOrEmpty(property)) {
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
}
