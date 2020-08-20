package rainbow.db.dao.memory;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import rainbow.db.dataSource.AbstractDataSource;

public class MemoryDataSource extends AbstractDataSource implements Closeable {

	private MemoryConnection con;

	public MemoryDataSource() {
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (con == null)
			con = new MemoryConnection();
		return con;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return con;
	}

	public void dispose() {
	}

	@Override
	public void close() throws IOException {
		if (con != null) {
			try {
				con.destroy();
			} catch (SQLException e) {
			}
		}
	}

}
