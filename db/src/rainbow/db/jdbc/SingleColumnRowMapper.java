package rainbow.db.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SingleColumnRowMapper<T> implements RowMapper<T> {

	private Class<T> requiredType;

	/**
	 * Create a new SingleColumnRowMapper.
	 * 
	 * @see #setRequiredType
	 */
	public SingleColumnRowMapper() {
	}

	/**
	 * Create a new SingleColumnRowMapper.
	 * 
	 * @param requiredType
	 *            the type that each result object is expected to match
	 */
	public SingleColumnRowMapper(Class<T> requiredType) {
		this.requiredType = requiredType;
	}

	/**
	 * Set the type that each result object is expected to match.
	 * <p>
	 * If not specified, the column value will be exposed as returned by the
	 * JDBC driver.
	 */
	public void setRequiredType(Class<T> requiredType) {
		this.requiredType = requiredType;
	}

	/**
	 * Extract a value for the single column in the current row.
	 * <p>
	 * Validates that there is only one column selected, then delegates to
	 * <code>getColumnValue()</code> and also
	 * <code>convertValueToRequiredType</code>, if necessary.
	 * 
	 * @see java.sql.ResultSetMetaData#getColumnCount()
	 * @see #getColumnValue(java.sql.ResultSet, int, Class)
	 */
	@SuppressWarnings("unchecked")
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		// Validate column count.
		ResultSetMetaData rsmd = rs.getMetaData();
		int nrOfColumns = rsmd.getColumnCount();
		if (nrOfColumns != 1) {
			throw new DataAccessException("Incorrect column size: expected 1 actual " + nrOfColumns);
		}

		// Extract column value from JDBC ResultSet.
		return (T) getColumnValue(rs, 1, this.requiredType);
	}

	/**
	 * Retrieve a JDBC object value for the specified column.
	 * <p>
	 * The default implementation calls
	 * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}. If
	 * no required type has been specified, this method delegates to
	 * <code>getColumnValue(rs, index)</code>, which basically calls
	 * <code>ResultSet.getObject(index)</code> but applies some additional
	 * default conversion to appropriate value types.
	 * 
	 * @param rs
	 *            is the ResultSet holding the data
	 * @param index
	 *            is the column index
	 * @param requiredType
	 *            the type that each result object is expected to match (or
	 *            <code>null</code> if none specified)
	 * @return the Object value
	 * @throws SQLException
	 *             in case of extraction failure
	 * @see rainbow.db.jdbc.JdbcUtils#getResultSetValue(java.sql.ResultSet,
	 *      int, Class)
	 * @see #getColumnValue(java.sql.ResultSet, int)
	 */
	protected Object getColumnValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
		if (requiredType != null) {
			return JdbcUtils.getResultSetValue(rs, index, requiredType);
		} else {
			// No required type specified -> perform default extraction.
			return getColumnValue(rs, index);
		}
	}

	/**
	 * Retrieve a JDBC object value for the specified column, using the most
	 * appropriate value type. Called if no required type has been specified.
	 * <p>
	 * The default implementation delegates to
	 * <code>JdbcUtils.getResultSetValue()</code>, which uses the
	 * <code>ResultSet.getObject(index)</code> method. Additionally, it includes
	 * a "hack" to get around Oracle returning a non-standard object for their
	 * TIMESTAMP datatype. See the <code>JdbcUtils#getResultSetValue()</code>
	 * javadoc for details.
	 * 
	 * @param rs
	 *            is the ResultSet holding the data
	 * @param index
	 *            is the column index
	 * @return the Object value
	 * @throws SQLException
	 *             in case of extraction failure
	 * @see rainbow.db.jdbc.JdbcUtils#getResultSetValue(java.sql.ResultSet,
	 *      int)
	 */
	protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index);
	}

}
