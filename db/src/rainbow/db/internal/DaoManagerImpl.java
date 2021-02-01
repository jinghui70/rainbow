package rainbow.db.internal;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.bundle.Bean;
import rainbow.core.bundle.ConfigAwareObject;
import rainbow.core.bundle.Extension;
import rainbow.core.model.IAdaptable;
import rainbow.core.util.Utils;
import rainbow.core.util.encrypt.Cipher;
import rainbow.core.util.encrypt.EncryptUtils;
import rainbow.core.util.ioc.DisposableBean;
import rainbow.core.util.ioc.InitializingBean;
import rainbow.core.util.ioc.InjectProvider;
import rainbow.core.util.json.JSON;
import rainbow.db.DaoManager;
import rainbow.db.dao.Dao;
import rainbow.db.dao.DaoConfig;
import rainbow.db.dao.DaoImpl;
import rainbow.db.dao.model.Entity;
import rainbow.db.database.DatabaseUtils;
import rainbow.db.database.Dialect;

@Bean
@Extension(point = InjectProvider.class)
public class DaoManagerImpl extends ConfigAwareObject
		implements DaoManager, IAdaptable, InitializingBean, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(DaoManagerImpl.class);

	private Map<String, Dao> daoMap;

	private List<String> daoNames = Collections.emptyList();

	private String defaultDao;

	@Override
	public void afterPropertiesSet() throws Exception {
		Path path = bundleConfig.getConfigPath();
		if (Files.exists(path)) {
			daoNames = Files.list(path).map(Path::getFileName) //
					.map(Path::toString) //
					.filter(s -> s.endsWith(".json")) //
					.map(s -> Utils.substringBefore(s, ".json")) //
					.collect(Collectors.toList());
			int size = daoNames.size();
			if (size == 1)
				defaultDao = daoNames.get(0);
			if (size > 0) {
				daoMap = new HashMap<>(size);
				logger.info("{} db config found: {}", size, daoNames);
			}
		}
	}

	@Override
	public List<String> getDaoNames() {
		return daoNames;
	}

	@Override
	public Dao getDao(String name) {
		Dao dao = daoMap.get(name);
		if (dao == null) {
			dao = createDao(name);
			daoMap.put(name, dao);
		}
		return dao;
	}

	private synchronized Dao createDao(String name) {
		if (daoMap.get(name) != null) {
			return daoMap.get(name);
		}
		DaoConfig config = loadConfig(name);
		Map<String, Entity> entityMap = loadModel(config.getModel());
		logger.info("connecting to db [{}]", name);
		if (config.getRef() == null) {
			DataSource dataSource = DatabaseUtils.createDataSource(config);
			Dialect dialect = DatabaseUtils.dialect(config.getType());
			return new DaoImpl(dataSource, dialect, entityMap);
		} else {
			Dao refDao = getDao(config.getRef());
			return new DaoImpl(refDao, entityMap);
		}
	}

	@Override
	public DaoConfig loadConfig(String name) {
		Path file = checkNotNull(bundleConfig.getConfigFile(name + ".json"), "dao config file [{}.json] not exist",
				name);
		DaoConfig config = JSON.parseObject(file, DaoConfig.class);
		if (Utils.isNullOrEmpty(config.getRef()) && Utils.hasContent(config.getCipher())) {
			Cipher cipher = EncryptUtils.getCipher(config.getCipher());
			if (config.isEncrypted()) {
				config.setUsername(cipher.decode(config.getUsername()));
				config.setPassword(cipher.decode(config.getPassword()));
			} else {
				String username = config.getUsername();
				String password = config.getPassword();
				config.setEncrypted(true);
				config.setUsername(cipher.encode(username));
				config.setPassword(cipher.encode(password));
				JSON.toJSON(config, file, true);
				config.setUsername(username);
				config.setPassword(password);
			}
		}
		return config;
	}

	private Map<String, Entity> loadModel(String name) {
		Path file = checkNotNull(bundleConfig.getConfigFile(name), "model file [{}] not exist", name);
		return DatabaseUtils.resolveModel(file);
	}

	@Override
	public void destroy() throws Exception {
		daoMap.forEach((id, dao) -> {
			logger.info("closeing dataSource: {}", id);
			try {
				dao.close();
			} catch (IOException e) {
				logger.error("closeing dao [{}] error", id, e);
			}
		});
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
				if ("dao".equals(name)) {
					if (defaultDao != null)
						return getDao(defaultDao);
					else
						return null;
				}
				return getDao(name);
			}
		};
	}

}
