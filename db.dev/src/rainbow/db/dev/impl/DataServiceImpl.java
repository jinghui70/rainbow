package rainbow.db.dev.impl;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rainbow.core.bundle.Bean;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.Inject;
import rainbow.db.DaoManager;
import rainbow.db.dao.Dao;
import rainbow.db.dao.DaoConfig;
import rainbow.db.dao.Sql;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.dev.api.DataService;
import rainbow.db.dev.api.EntityNodeX;
import rainbow.db.dev.api.Node;
import rainbow.db.jdbc.ColumnMapRowMapper;
import rainbow.db.query.QueryAnalyzer;
import rainbow.db.query.QueryRequest;
import rainbow.db.refinery.RefineryDef;
import rainbow.db.refinery.RefineryRegistry;

@Bean
public class DataServiceImpl implements DataService {

	private Map<String, ModelInfo> modelMap = new HashMap<String, ModelInfo>();

	@Inject
	private DaoManager daoManager;

	private ModelInfo getModel(String name) {
		ModelInfo m = modelMap.get(name);
		if (m == null) {
			m = readModel(name);
			modelMap.put(name, m);
		}
		return m;
	}

	synchronized private ModelInfo readModel(String name) {
		ModelInfo result = modelMap.get(name);
		if (result == null) {
			DaoConfig config = daoManager.loadConfig(name);
			Dao dao = daoManager.getDao(name);
			result = new ModelInfo(dao, config.getModel());
		}
		return result;
	}

	@Override
	public Node dataTree(String model) {
		ModelInfo m = getModel(model);
		return m.getTree();
	}

	@Override
	public EntityNodeX entity(String model, String name) {
		ModelInfo m = getModel(model);
		return m.getEntity(name);
	}

	@Override
	public List<RefineryDef> getRefinery(String model, String entityName, String columnName) {
		Dao dao = daoManager.getDao(model);
		Entity entity = dao.getEntity(entityName);
		checkNotNull(entity, "entity [{}] not exist", entityName);
		int inx = columnName.indexOf('.');
		Column column = null;
		if (inx == -1) {
			column = entity.getColumn(columnName);
		} else {
			Link link = entity.getLink(columnName.substring(0, inx));
			checkNotNull(link, "linkColumn [{}] of entity [{}] not exist", columnName, entityName);
			column = link.getTargetEntity().getColumn(columnName.substring(inx + 1));
		}
		checkNotNull(column, "column [{}] of entity [{}] not exist", columnName, entityName);
		return RefineryRegistry.getRefinery(column);
	}

	@Override
	public Object query(String model, QueryRequest query) {
		Dao dao = daoManager.getDao(model);
		QueryAnalyzer analyzer = new QueryAnalyzer(query, dao);
		return analyzer.doQuery();
	}

	@Override
	public Object sql(String model, String text) {
		Dao dao = daoManager.getDao(model);
		boolean select = false;
		try {
			Objects.requireNonNull(text);
			String begin = text.substring(0, 6).toUpperCase();
			if ("SELECT".equals(begin))
				select = true;
		} catch (Throwable e) {
			return "不是有效的SQL语句";
		}
		Sql sql = new Sql(text);
		if (select) {
			return sql.queryForList(dao, ColumnMapRowMapper.instance);
		} else {
			int count = sql.execute(dao);
			return Utils.format("影响了{}条数据", count);
		}
	}

	@Override
	public List<String> dataSources() {
		return daoManager.getDaoNames();
	}
}
