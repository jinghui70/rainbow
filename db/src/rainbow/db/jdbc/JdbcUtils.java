package rainbow.db.jdbc;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic utility methods for working with JDBC. Mainly for internal use within
 * the framework, but also useful for custom JDBC access code.
 */
public abstract class JdbcUtils {

	/**
	 * Constant that indicates an unknown (or unspecified) SQL type.
	 * 
	 * @see java.sql.Types
	 */
	public static final int TYPE_UNKNOWN = Integer.MIN_VALUE;

	private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

	/**
	 * Close the given JDBC Connection and ignore any thrown exception. This is
	 * useful for typical finally blocks in manual JDBC code.
	 * 
	 * @param con
	 *            the JDBC Connection to close (may be <code>null</code>)
	 */
	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
				logger.debug("Could not close JDBC Connection", ex);
			} catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw
				// RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC Connection", ex);
			}
		}
	}

	/**
	 * Close the given JDBC Statement and ignore any thrown exception. This is
	 * useful for typical finally blocks in manual JDBC code.
	 * 
	 * @param stmt
	 *            the JDBC Statement to close (may be <code>null</code>)
	 */
	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
				logger.debug("Could not close JDBC Statement", ex);
			} catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw
				// RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC Statement", ex);
			}
		}
	}

	/**
	 * Close the given JDBC ResultSet and ignore any thrown exception. This is
	 * useful for typical finally blocks in manual JDBC code.
	 * 
	 * @param rs
	 *            the JDBC ResultSet to close (may be <code>null</code>)
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				logger.debug("Could not close JDBC ResultSet", ex);
			} catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw
				// RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC ResultSet", ex);
			}
		}
	}

	/**
	 * Retrieve a JDBC column value from a ResultSet, using the specified value
	 * type.
	 * <p>
	 * Uses the specifically typed ResultSet accessor methods, falling back to
	 * {@link #getResultSetValue(java.sql.ResultSet, int)} for unknown types.
	 * <p>
	 * Note that the returned value may not be assignable to the specified
	 * required type, in case of an unknown type. Calling code needs to deal
	 * with this case appropriately, e.g. throwing a corresponding exception.
	 * 
	 * @param rs
	 *            is the ResultSet holding the data
	 * @param index
	 *            is the column index
	 * @param requiredType
	 *            the required value type (may be <code>null</code>)
	 * @return the value object
	 * @throws SQLException
	 *             if thrown by the JDBC API
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getResultSetValue(ResultSet rs, int index, Class<T> requiredType) throws SQLException {
		if (requiredType == null) {
			return (T) getResultSetValue(rs, index);
		}

		Object value = null;
		boolean wasNullCheck = false;

		// Explicitly extract typed value, as far as possible.
		if (String.class.equals(requiredType)) {
			value = rs.getString(index);
		} else if (boolean.class.equals(requiredType) || Boolean.class.equals(requiredType)) {
			value = rs.getBoolean(index);
			wasNullCheck = true;
		} else if (byte.class.equals(requiredType) || Byte.class.equals(requiredType)) {
			value = rs.getByte(index);
			wasNullCheck = true;
		} else if (short.class.equals(requiredType) || Short.class.equals(requiredType)) {
			value = rs.getShort(index);
			wasNullCheck = true;
		} else if (int.class.equals(requiredType) || Integer.class.equals(requiredType)) {
			value = rs.getInt(index);
			wasNullCheck = true;
		} else if (long.class.equals(requiredType) || Long.class.equals(requiredType)) {
			value = rs.getLong(index);
			wasNullCheck = true;
		} else if (float.class.equals(requiredType) || Float.class.equals(requiredType)) {
			value = rs.getFloat(index);
			wasNullCheck = true;
		} else if (double.class.equals(requiredType) || Double.class.equals(requiredType)
				|| Number.class.equals(requiredType)) {
			value = rs.getDouble(index);
			wasNullCheck = true;
		} else if (byte[].class.equals(requiredType)) {
			value = rs.getBytes(index);
		} else if (java.sql.Date.class.equals(requiredType)) {
			value = rs.getDate(index);
		} else if (java.sql.Time.class.equals(requiredType)) {
			value = rs.getTime(index);
		} else if (java.sql.Timestamp.class.equals(requiredType) || java.util.Date.class.equals(requiredType)) {
			value = rs.getTimestamp(index);
		} else if (BigDecimal.class.equals(requiredType)) {
			value = rs.getBigDecimal(index);
		} else if (Blob.class.equals(requiredType)) {
			value = rs.getBlob(index);
		} else if (Clob.class.equals(requiredType)) {
			value = rs.getClob(index);
		} else {
			// Some unknown type desired -> rely on getObject.
			value = getResultSetValue(rs, index);
		}

		// Perform was-null check if demanded (for results that the
		// JDBC driver returns as primitives).
		if (wasNullCheck && value != null && rs.wasNull()) {
			value = null;
		}
		return (T) value;
	}

	/**
	 * Retrieve a JDBC column value from a ResultSet, using the most appropriate
	 * value type. The returned value should be a detached value object, not
	 * having any ties to the active ResultSet: in particular, it should not be
	 * a Blob or Clob object but rather a byte array respectively String
	 * representation.
	 * <p>
	 * Uses the <code>getObject(index)</code> method, but includes additional
	 * "hacks" to get around Oracle 10g returning a non-standard object for its
	 * TIMESTAMP datatype and a <code>java.sql.Date</code> for DATE columns
	 * leaving out the time portion: These columns will explicitly be extracted
	 * as standard <code>java.sql.Timestamp</code> object.
	 * 
	 * @param rs
	 *            is the ResultSet holding the data
	 * @param index
	 *            is the column index
	 * @return the value object
	 * @throws SQLException
	 *             if thrown by the JDBC API
	 * @see java.sql.Blob
	 * @see java.sql.Clob
	 * @see java.sql.Timestamp
	 */
	public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
		Object obj = rs.getObject(index);
		if (obj == null)
			return null;
		if (obj instanceof Blob) {
			obj = rs.getBytes(index);
		} else if (obj instanceof Clob) {
			obj = rs.getString(index);
		} else if (obj instanceof java.sql.Date) {
			if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
				obj = rs.getTimestamp(index);
			}
		}
		return obj;
	}

	/**
	 * Check whether the given SQL type is numeric.
	 * 
	 * @param sqlType
	 *            the SQL type to be checked
	 * @return whether the type is numeric
	 */
	public static boolean isNumeric(int sqlType) {
		return Types.BIT == sqlType || Types.BIGINT == sqlType || Types.DECIMAL == sqlType || Types.DOUBLE == sqlType
				|| Types.FLOAT == sqlType || Types.INTEGER == sqlType || Types.NUMERIC == sqlType
				|| Types.REAL == sqlType || Types.SMALLINT == sqlType || Types.TINYINT == sqlType;
	}

	/**
	 * Return whether the given JDBC driver supports JDBC 2.0 batch updates.
	 * <p>Typically invoked right before execution of a given set of statements:
	 * to decide whether the set of SQL statements should be executed through
	 * the JDBC 2.0 batch mechanism or simply in a traditional one-by-one fashion.
	 * <p>Logs a warning if the "supportsBatchUpdates" methods throws an exception
	 * and simply returns {@code false} in that case.
	 * @param con the Connection to check
	 * @return whether JDBC 2.0 batch updates are supported
	 * @see java.sql.DatabaseMetaData#supportsBatchUpdates()
	 */
	public static boolean supportsBatchUpdates(Connection con) {
		try {
			DatabaseMetaData dbmd = con.getMetaData();
			if (dbmd != null) {
				if (dbmd.supportsBatchUpdates()) {
					logger.debug("JDBC driver supports batch updates");
					return true;
				}
				else {
					logger.debug("JDBC driver does not support batch updates");
				}
			}
		}
		catch (SQLException ex) {
			logger.debug("JDBC driver 'supportsBatchUpdates' method threw exception", ex);
		}
		return false;
	}
}
