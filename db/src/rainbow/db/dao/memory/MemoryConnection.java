package rainbow.db.dao.memory;

import java.sql.SQLException;
import java.util.Properties;

import org.h2.jdbc.JdbcConnection;

public class MemoryConnection extends JdbcConnection {

	public MemoryConnection() throws SQLException {
		super(new JdbcConnection("jdbc:h2:mem:", new Properties()));
	}

	/* (non-Javadoc)
	 * 什么也不做
	 */
	@Override
	public synchronized void close() throws SQLException {
	}

	public void destroy() throws SQLException {
		super.close();
	}
}
