package rainbow.db.dao.memory;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class MemoryDataSource implements DataSource, Closeable {

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

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

}
