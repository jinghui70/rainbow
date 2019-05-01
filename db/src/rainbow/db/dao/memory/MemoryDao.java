package rainbow.db.dao.memory;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import rainbow.core.util.ioc.DisposableBean;
import rainbow.db.dao.DaoImpl;
import rainbow.db.dao.DaoUtils;
import rainbow.db.dao.model.Entity;

public class MemoryDao extends DaoImpl implements DisposableBean {

	private MemoryDataSource dataSource;

	public MemoryDao() {
		try {
			dataSource = new MemoryDataSource();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		super.setDataSource(dataSource);
	}

	public MemoryDao(Map<String, Entity> model) {
		this();
		String ddl = DaoUtils.transform(model);
		execSql(ddl);
		setEntityMap(model);
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
