package rainbow.db.jdbc;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * Abstract base class for Spring's {@link javax.sql.DataSource}
 * implementations, taking care of the padding.
 *
 */
public abstract class AbstractDataSource implements DataSource {

	/**
	 * Returns 0, indicating the default system timeout is to be used.
	 */
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	/**
	 * Setting a login timeout is not supported.
	 */
	public void setLoginTimeout(int timeout) throws SQLException {
		throw new UnsupportedOperationException("setLoginTimeout");
	}

	/**
	 * LogWriter methods are not supported.
	 */
	public PrintWriter getLogWriter() {
		throw new UnsupportedOperationException("getLogWriter");
	}

	/**
	 * LogWriter methods are not supported.
	 */
	public void setLogWriter(PrintWriter pw) throws SQLException {
		throw new UnsupportedOperationException("setLogWriter");
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	// ---------------------------------------------------------------------
	// Implementation of JDBC 4.0's Wrapper interface
	// ---------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		checkNotNull(iface, "Interface argument must not be null");
		if (!DataSource.class.equals(iface)) {
			throw new SQLException("DataSource of type [" + getClass().getName()
					+ "] can only be unwrapped as [javax.sql.DataSource], not as [" + iface.getName());
		}
		return (T) this;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return DataSource.class.equals(iface);
	}

}
