package rainbow.db.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link RowMapper} implementation that creates a <code>java.util.Map</code>
 * for each row, representing all columns as key-value pairs: one entry for each
 * column, with the column name as key.
 *
 * <p>
 * The Map implementation to use and the key to use for each column in the
 * column Map can be customized through overriding {@link #getColumnKey},
 * respectively.
 *
 * <p>
 * <b>Note:</b> By default, ColumnMapRowMapper will try to build a linked Map
 * with case-insensitive keys, to preserve column order as well as allow any
 * casing to be used for column names. This requires Commons Collections on the
 * classpath (which will be autodetected). Else, the fallback is a standard
 * linked HashMap, which will still preserve column order but requires the
 * application to specify the column names in the same casing as exposed by the
 * driver.
 *
 * @author Juergen Hoeller
 * @since 1.2
 * 
 *        以上是抄袭Spring的东西，但我们没用LinkedCaseInsensitiveMap，自己实现一个map
 * 
 */
public class ColumnMapRowMapper implements RowMapper<Map<String, Object>> {

	public static ColumnMapRowMapper instance = new ColumnMapRowMapper();

	public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		@SuppressWarnings("serial")
		Map<String, Object> mapOfColValues = new LinkedHashMap<String, Object>() {
			@Override
			public Object get(Object key) {
				if (key instanceof String) {
					return super.get(((String) key).toLowerCase());
				}
				return null;
			}

			@Override
			public boolean containsKey(Object key) {
				if (key instanceof String) {
					return super.containsKey(((String) key).toLowerCase());
				}
				return false;
			}

			@Override
			public Object put(String key, Object value) {
				if (key != null)
					key = key.toLowerCase();
				return super.put(key, value);
			}

			@Override
			public Object remove(Object key) {
				if (key instanceof String) {
					key = ((String) key).toLowerCase();
				}
				return super.remove(key);
			}
		};
		for (int i = 1; i <= columnCount; i++) {
			String key = lookupColumnName(rsmd, i);
			Object obj = getColumnValue(rs, i);
			mapOfColValues.put(key, obj);
		}
		return mapOfColValues;
	}

	/**
	 * Determine the column name to use. The column name is determined based on
	 * a lookup using ResultSetMetaData.
	 * <p>
	 * This method implementation takes into account recent clarifications
	 * expressed in the JDBC 4.0 specification:
	 * <p>
	 * <i>columnLabel - the label for the column specified with the SQL AS
	 * clause. If the SQL AS clause was not specified, then the label is the
	 * name of the column</i>.
	 * 
	 * @return the column name to use
	 * @param resultSetMetaData
	 *            the current meta data to use
	 * @param columnIndex
	 *            the index of the column for the look up
	 * @throws SQLException
	 *             in case of lookup failure
	 */
	public static String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if (name == null || name.length() < 1) {
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}

	/**
	 * Retrieve a JDBC object value for the specified column.
	 * <p>
	 * The default implementation uses the <code>getObject</code> method.
	 * Additionally, this implementation includes a "hack" to get around Oracle
	 * returning a non standard object for their TIMESTAMP datatype.
	 * 
	 * @param rs
	 *            is the ResultSet holding the data
	 * @param index
	 *            is the column index
	 * @return the Object returned
	 * @see rainbow.db.jdbc.JdbcUtils#getResultSetValue
	 */
	protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index);
	}

}
