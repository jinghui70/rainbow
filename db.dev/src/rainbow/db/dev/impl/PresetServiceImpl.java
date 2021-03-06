package rainbow.db.dev.impl;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

import rainbow.core.bundle.Bean;
import rainbow.core.bundle.ConfigAwareObject;
import rainbow.core.model.exception.AppException;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.Inject;
import rainbow.core.util.json.JSON;
import rainbow.db.dao.Dao;
import rainbow.db.dao.NeoBean;
import rainbow.db.dao.Select;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dev.PresetService;

@Bean
public class PresetServiceImpl extends ConfigAwareObject implements PresetService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Dao dao;

	@Inject
	public void setDao(Dao dao) {
		this.dao = dao;
	}

	/**
	 * 返回数据组列表
	 * 
	 * @return
	 */
	@Override
	public List<String> types() {
		return Arrays.asList("测试数据", "预置数据");
	}

	/**
	 * 从预置数据文件中获取指定实体数据
	 * 
	 * @param dataSet
	 * @param entityName
	 * @return
	 */
	@Override
	public List<Map<String, Object>> load(String dataSet, String entityName) {
		Path file = getFile(dataSet, entityName);
		if (!Files.exists(file))
			return Collections.emptyList();
		try {
			List<String> lines = Files.readAllLines(file);
			return Utils.transform(lines, line -> JSON.parseObject(line));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean save(String presetType, String entityName, List<Map<String, Object>> data) {
		Path file = getFile(presetType, entityName);
		try {
			if (Utils.isNullOrEmpty(data)) {
				Files.deleteIfExists(file);
			} else {
				Entity entity = dao.getEntity(entityName);
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				try (Writer writer = Files.newBufferedWriter(file)) {
					for (Map<String, Object> item : data) {
						map.clear();
						for (Column column : entity.getColumns()) {
							Object v = item.get(column.getName());
							if (column.dataClass() == String.class && Objects.equal("", v) && !column.isMandatory())
								v = null;
							if (v != null)
								map.put(column.getName(), v);
						}
						writer.write(JSON.toJSONString(map));
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
		Path path = bundleConfig.getConfigFile(presetType);
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
	@Override
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
			else
				select.orderByKey();
			List<Map<String, Object>> list = select.queryForList();
			Path file = getFile(dataSet, entityName);
			try {
				if (list.isEmpty()) {
					Files.deleteIfExists(file);
					return;
				}
				try (Writer writer = Files.newBufferedWriter(file)) {
					for (Map<String, Object> item : list) {
						writer.write(JSON.toJSONString(item));
						writer.write('\r');
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

	}

	@Override
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
							Map<String, Object> map = JSON.parseObject(line);
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

	@Override
	public List<String> hasPreset(String dataSet) throws IOException {
		Path root = bundleConfig.getConfigFile(dataSet);
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

	@Override
	public boolean initDatabase(String dataSet) {
		return true;
	}
}
