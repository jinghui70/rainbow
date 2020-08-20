package rainbow.db.internal;

import static rainbow.core.util.Preconditions.checkNotNull;
import static rainbow.core.util.Preconditions.checkState;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import rainbow.core.bundle.Bean;
import rainbow.core.bundle.ConfigAwareObject;
import rainbow.core.bundle.Extension;
import rainbow.core.model.IAdaptable;
import rainbow.core.platform.Platform;
import rainbow.core.util.Utils;
import rainbow.core.util.converter.Converters;
import rainbow.core.util.encrypt.Cipher;
import rainbow.core.util.encrypt.EncryptUtils;
import rainbow.core.util.ioc.DisposableBean;
import rainbow.core.util.ioc.InjectProvider;
import rainbow.db.DaoManager;
import rainbow.db.dao.Dao;
import rainbow.db.dao.DaoImpl;
import rainbow.db.dao.model.Entity;
import rainbow.db.database.DaoConfig;
import rainbow.db.database.DatabaseUtils;

@Bean
@Extension(point = InjectProvider.class)
public class DaoManagerImpl extends ConfigAwareObject implements DaoManager, IAdaptable, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(DaoManagerImpl.class);

	private Map<String, Dao> daoMap = Collections.emptyMap();

	private Dao defaultDao;

	@Override
	public Dao getDao(String name) {
		return daoMap.get(name);
	}

	private Path loadConfig(String filename) throws FileNotFoundException {
		Path file = bundleConfig.getConfigFile(filename);
		return Files.exists(file) ? file : null;
	}

	public void init() throws Exception {
		Path file = null;
		if (Platform.isDev())
			file = loadConfig("database.yaml.dev");
		if (file == null)
			file = loadConfig("database.yaml");
		if (file == null)
			return;

		List<DaoConfig> configs = new ArrayList<DaoConfig>();
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		boolean changed = false;

		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);

		try (InputStream is = Files.newInputStream(file)) {
			for (Object o : yaml.loadAll(is)) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) o;
				DaoConfig config = Converters.map2Object(map, DaoConfig.class);
				if (encryptThing(map, config))
					changed = true;
				configs.add(config);
				maps.add(map);
			}
		}
		if (changed) {
			try (Writer writer = Files.newBufferedWriter(file)) {
				yaml.dumpAll(maps.iterator(), writer);
			}
		}
		daoMap = new HashMap<String, Dao>();
		for (DaoConfig config : configs) {
			logger.info("creating dao: {}...", config.getName());
			Dao dao = createDao(config);
			if (defaultDao == null)
				defaultDao = dao;
			daoMap.put(config.getName(), dao);
		}
	}

	@Override
	public void destroy() throws Exception {
		daoMap.forEach((id, dao) -> {
			DataSource dataSource = dao.getJdbcTemplate().getDataSource();
			if (dataSource instanceof Closeable) {
				try {
					((Closeable) dataSource).close();
				} catch (IOException e) {
					logger.error("closing dataSource[{}] error", id, e);
				}
			}
		});
	}

	private boolean encryptThing(Map<String, Object> map, DaoConfig config) {
		if (!Utils.isNullOrEmpty(config.getPhysicSource()))
			return false;
		String cipherType = config.getCipher();
		if (Utils.isNullOrEmpty(cipherType))
			return false;
		Cipher cipher = EncryptUtils.getCipher(cipherType);
		if (config.isEncrypted()) {
			config.setUsername(cipher.decode(config.getUsername()));
			config.setPassword(cipher.decode(config.getPassword()));
			return false;
		} else {
			map.put("encrypted", true);
			map.put("username", cipher.encode(config.getUsername()));
			map.put("password", cipher.encode(config.getPassword()));
			return true;
		}
	}

	private Dao createDao(DaoConfig config) {
		Map<String, Entity> entityMap = null;
		String modelFileName = config.getModel();
		if (!Utils.isNullOrEmpty(modelFileName)) {
			Path modelFile = bundleConfig.getConfigFile(modelFileName);
			checkState(Files.exists(modelFile), "database model file not exist:{}", modelFileName);
			entityMap = DatabaseUtils.resolveModel(modelFile);
		}
		if (!Utils.isNullOrEmpty(config.getPhysicSource())) {
			Dao refDao = checkNotNull(daoMap.get(config.getPhysicSource()), "physic datasource [{}] not defined!",
					config.getPhysicSource());
			return new DaoImpl(refDao.getJdbcTemplate().getDataSource(), refDao.getDialect(), entityMap);
		}
		return DatabaseUtils.createDao(config, entityMap);
	}

	@Override
	public Object getAdapter(Class<?> adapter) {
		if (adapter != InjectProvider.class)
			return null;
		return new InjectProvider() {
			@Override
			public Class<?> getInjectClass() {
				return Dao.class;
			}

			@Override
			public Object getInjectObject(String name, String destClassName) {
				if (daoMap.size() == 1 || "dao".equals(name))
					return defaultDao;
				return daoMap.get(name);
			}
		};
	}

}
