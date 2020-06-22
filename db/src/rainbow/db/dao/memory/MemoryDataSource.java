package rainbow.db.dao.memory;

import java.sql.Connection;
import java.sql.SQLException;

import rainbow.db.dataSource.AbstractDataSource;

public class MemoryDataSource extends AbstractDataSource {

	private MemoryConnection con;

	public MemoryDataSource() throws SQLException {
		con = new MemoryConnection();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return con;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return con;
	}

	public void dispose() {
		if (con != null) {
			try {
				con.destroy();
			} catch (SQLException e) {
			}
		}
	}

}
