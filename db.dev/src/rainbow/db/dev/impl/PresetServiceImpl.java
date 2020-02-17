package rainbow.db.dev.impl;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import rainbow.core.bundle.Bean;
import rainbow.core.model.exception.AppException;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.ActivatorAwareObject;
import rainbow.db.dao.Dao;
import rainbow.db.dao.NeoBean;
import rainbow.db.dao.Select;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dev.PresetService;

@Bean
public class PresetServiceImpl extends ActivatorAwareObject implements PresetService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Dao dao;

	private static Type MAP_TYPE = new TypeReference<Map<String, Object>>() {
	}.getType();

	/**
	 * 返回数据组列表
	 * 
	 * @return
	 */
	public List<String> types() {
		List<String> dataSets = activator.getConfig().getList("dataSets");
		if (Utils.isNullOrEmpty(dataSets)) {
			dataSets = Arrays.asList("测试数据", "预置数据");
		}
		return dataSets;
	}

	/**
	 * 从预置数据文件中获取指定实体数据
	 * 
	 * @param dataSet
	 * @param entityName
	 * @return
	 */
	public List<Map<String, Object>> load(String dataSet, String entityName) {
		Path file = getFile(dataSet, entityName);
		if (!Files.exists(file))
			return Collections.emptyList();
		try {
			List<String> lines = Files.readAllLines(file);
			return Utils.transform(lines, line -> JSON.parseObject(line, MAP_TYPE));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean save(String presetType, String entityName, List<Map<String, Object>> data) {
		Path file = getFile(presetType, entityName);
		try {
			if (Utils.isNullOrEmpty(data)) {
				Files.deleteIfExists(file);
			} else {
				NeoBean neo = dao.newNeoBean(entityName);
				try (Writer writer = Files.newBufferedWriter(file)) {
					for (Map<String, Object> item : data) {
						neo.init(null);
						for (String key : item.keySet()) {
							Column column = neo.getEntity().getColumn(key);
							checkNotNull(column, "column {} of entity {} not exist", key, entityName);
							Object v = item.get(key);
							if (column.dataClass() == String.class && Utils.isNullOrEmpty(v.toString())
									&& !column.isMandatory())
								v = null;
							if (v != null)
								neo.setValue(column, v);
						}
						writer.write(Utils.toJson(neo.toMap()));
						writer.write('\r');
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	private Path getFile(String presetType, String entityName) {
		Path path = activator.getConfigureFile(presetType);
		if (!Files.exists(path))
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				throw new RuntimeException("创建目录失败", e);
			}
		path = path.resolve(entityName + ".json");
		return path;
	}

	/**
	 * @param dataSet
	 * @param entities
	 */
	public void importFromDb(String dataSet, List<String> entities) {
		logger.debug("import db data to data set: {}", dataSet);
		entities.forEach(entityName -> {
			logger.debug("import {}...", entityName);
			Entity entity = dao.getEntity(entityName);
			if (entity == null) {
				logger.warn("entity {} not defined", entityName);
				return;
			}
			String orderByStr = null;
			if (entity.hasColumn("globalOrder"))
				orderByStr = "globalOrder";
			else if (entity.hasColumn("orderNum"))
				orderByStr = "orderNum";
			else if (entity.hasColumn("code"))
				orderByStr = "code";
			Select select = dao.select().from(entity);
			if (orderByStr != null)
				select.orderBy(orderByStr);
			List<Map<String, Object>> list = select.queryForMapList();
			Path file = getFile(dataSet, entityName);
			try {
				if (list.isEmpty()) {
					Files.deleteIfExists(file);
					return;
				}
				try (Writer writer = Files.newBufferedWriter(file)) {
					for (Map<String, Object> item : list) {
						writer.write(Utils.toJson(item));
						writer.write('\r');
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

	}

	public void exportToDb(String dataSet, List<String> entities) {
		logger.debug("tansfer data of dataset {} to db", dataSet);
		entities.forEach(entityName -> {
			Path file = getFile(dataSet, entityName);
			if (Files.exists(file)) {
				logger.debug("procession {}...", entityName);
				Entity entity = dao.getEntity(entityName);
				if (entity == null) {
					throw new AppException("entity {} not defined", entityName);
				} else {
					dao.clear(entityName);
					try {
						List<String> lines = Files.readAllLines(file);
						List<NeoBean> neoList = Utils.transform(lines, line -> {
							Map<String, Object> map = JSON.parseObject(line, MAP_TYPE);
							return dao.makeNeoBean(entityName, map);
						});
						dao.insert(neoList);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		});
	}

	public List<String> hasPreset(String dataSet) throws IOException {
		Path root = activator.getConfigureFile(dataSet);
		if (!Files.exists(root)) {
			return Collections.emptyList();
		}
		return Files.list(root) //
				.map(Path::getFileName) //
				.map(Object::toString) //
				.filter(s -> s.endsWith(".json")) //
				.sorted() //
				.collect(Collectors.toList());
	}

	public boolean initDatabase(String dataSet) {
		return true;
	}
}
