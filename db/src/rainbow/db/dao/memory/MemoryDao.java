package rainbow.db.dao.memory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import rainbow.core.util.ioc.DisposableBean;
import rainbow.db.dao.DaoImpl;
import rainbow.db.dao.DaoUtils;
import rainbow.db.dao.model.Entity;
import rainbow.db.model.Model;

public class MemoryDao extends DaoImpl implements DisposableBean {

	private MemoryDataSource dataSource;

	public MemoryDao() {
		try {
			dataSource = new MemoryDataSource();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		super.setDataSource(dataSource);
		setEntityMap(new HashMap<String, Entity>());
	}

	public MemoryDao(Entity... entity) {
		this();
		setEntity(entity);
	}

	public MemoryDao(Model model) {
		this();
		setModel(model);
	}

	public void setModel(Model model) {
		String ddl = DaoUtils.transform(model);
		execSql(ddl);
		for (rainbow.db.model.Entity e : model.getEntities()) {
			entityMap.put(e.getName(), new Entity(e));
		}
	}

	public void setEntity(Entity... entities) {
		if (entities.length == 0)
			return;
		Model model = new Model();
		List<rainbow.db.model.Entity> list = new ArrayList<rainbow.db.model.Entity>(entities.length);
		model.setEntities(list);
		for (Entity entity : entities) {
			list.add(entity.getOrigin());
			entityMap.put(entity.getName(), entity);
		}
		String ddl = DaoUtils.transform(model);
		execSql(ddl);
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		// do nothing
	}

	@Override
	public void destroy() throws Exception {
		dataSource.dispose();
	}

}
