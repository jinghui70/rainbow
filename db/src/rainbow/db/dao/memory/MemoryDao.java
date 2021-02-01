package rainbow.db.dao.memory;

import java.util.HashMap;

import rainbow.core.util.ioc.DisposableBean;
import rainbow.db.dao.DaoImpl;
import rainbow.db.dao.model.Entity;
import rainbow.db.database.DatabaseUtils;
import rainbow.db.model.Model;
import rainbow.db.model.Table;

public class MemoryDao extends DaoImpl implements DisposableBean {

	public MemoryDao() {
		super(new MemoryDataSource(), DatabaseUtils.dialect("H2"), null);
	}

	public MemoryDao(Model model) {
		this();
		String ddl = dialect.toDDL(model, false);
		execSql(ddl);
		setEntityMap(DatabaseUtils.resolveModel(model));
	}

	public void addTable(Table table) {
		String ddl = dialect.toDDL(table);
		execSql(ddl);
		if (entityMap.isEmpty())
			entityMap = new HashMap<String, Entity>();
		Entity entity = new Entity(table);
		entityMap.put(entity.getName(), entity);
	}

	@Override
	public void destroy() throws Exception {
		((MemoryDataSource) (getJdbcTemplate().getDataSource())).dispose();
	}

}
