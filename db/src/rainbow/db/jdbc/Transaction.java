package rainbow.db.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class Transaction {

	private Connection conn;

	private int level = 0;

	private int count = 0;

	private int oldLevel;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		if (this.level <= 0)
			this.level = level;
	}

	public int getCount() {
		return count;
	}

	void beginNestTranscation() {
		count++;
	}

	public Connection getConnection(DataSource dataSource) throws SQLException {
		if (conn == null) {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			oldLevel = conn.getTransactionIsolation();
			conn.setTransactionIsolation(level);
		}
		return conn;
	}

	void commit() throws SQLException {
		if (count == 1 && conn != null) {
			conn.commit();
			close();
		}
		count--;
	}

	void rollback() {
		if (count == 1 && conn != null) {
			try {
				conn.rollback();
			} catch (Throwable e) {
			}
			close();
		}
		count--;
	}

	private void close() {
		try {
			conn.setAutoCommit(true);
			conn.setTransactionIsolation(oldLevel);
		} catch (Throwable e) {
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

}
