package rainbow.db.dao.memory;

import java.util.HashMap;
import java.util.Map;

import rainbow.core.util.ioc.DisposableBean;
import rainbow.db.dao.DaoImpl;
import rainbow.db.dao.model.Entity;
import rainbow.db.database.DatabaseUtils;

public class MemoryDao extends DaoImpl implements DisposableBean {

	public MemoryDao() {
		super(new MemoryDataSource(), DatabaseUtils.dialect("H2"));
	}

	public MemoryDao(Map<String, Entity> model) {
		this();
		String ddl = dialect.toDDL(model.values());
		execSql(ddl);
		setEntityMap(model);
	}

	public void addEntity(Entity entity) {
		if (entityMap.isEmpty())
			entityMap = new HashMap<String, Entity>();
		String ddl = dialect.toDDL(entity);
		execSql(ddl);
		entityMap.put(entity.getName(), entity);
	}

	@Override
	public void destroy() throws Exception {
		((MemoryDataSource) (getJdbcTemplate().getDataSource())).dispose();
	}

}
