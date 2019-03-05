package rainbow.db.jdbc;

import java.util.List;
import java.util.Map;

/**
 * Interface specifying a basic set of JDBC operations.
 * Implemented by {@link JdbcTemplate}. Not often used directly, but a useful
 * option to enhance testability, as it can easily be mocked or stubbed.
 *
 * <p>Alternatively, the standard JDBC infrastructure can be mocked.
 * However, mocking this interface constitutes significantly less work.
 * As an alternative to a mock objects approach to testing data access code,
 * consider the powerful integration testing support provided in the
 * <code>org.springframework.test</code> package, shipped in
 * <code>spring-mock.jar</code>.
 *
 * @see JdbcTemplate
 */
public interface JdbcOperations {

	/**
	 * Issue a single SQL execute, typically a DDL statement.
	 * @param sql static SQL to execute
	 * @throws DataAccessException if there is any problem
	 */
	void execute(String sql) throws DataAccessException;

	/**
	 * Execute a query given static SQL, mapping each row to a Java object
	 * via a RowMapper.
	 * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
	 * execute a static query with a PreparedStatement, use the overloaded
	 * <code>query</code> method with <code>null</code> as argument array.
	 * @param sql SQL query to execute
	 * @param rowMapper object that will map one object per row
	 * @return the result List, containing mapped objects
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #query(String, Object[], RowMapper)
	 */
	<T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException;

	/**
	 * Execute a query given static SQL, mapping a single result row to a Java
	 * object via a RowMapper.
	 * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
	 * execute a static query with a PreparedStatement, use the overloaded
	 * <code>queryForObject</code> method with <code>null</code> as argument array.
	 * @param sql SQL query to execute
	 * @param rowMapper object that will map one object per row
	 * @return the single mapped object
	 * @throws IncorrectResultSizeDataAccessException if the query does not
	 * return exactly one row
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #queryForObject(String, Object[], RowMapper)
	 */
	<T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException;

	/**
	 * Execute a query for a result object, given static SQL.
	 * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
	 * execute a static query with a PreparedStatement, use the overloaded
	 * <code>queryForObject</code> method with <code>null</code> as argument array.
	 * <p>This method is useful for running static SQL with a known outcome.
	 * The query is expected to be a single row/single column query; the returned
	 * result will be directly mapped to the corresponding object type.
	 * @param sql SQL query to execute
	 * @param requiredType the type that the result object is expected to match
	 * @return the result object of the required type, or <code>null</code> in case of SQL NULL
	 * @throws IncorrectResultSizeDataAccessException if the query does not return
	 * exactly one row, or does not return exactly one column in that row
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #queryForObject(String, Object[], Class)
	 */
	<T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException;

	/**
	 * Execute a query that results in a long value, given static SQL.
	 * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
	 * execute a static query with a PreparedStatement, use the overloaded
	 * <code>queryForLong</code> method with <code>null</code> as argument array.
	 * <p>This method is useful for running static SQL with a known outcome.
	 * The query is expected to be a single row/single column query that results
	 * in a long value.
	 * @param sql SQL query to execute
	 * @return the long value, or 0 in case of SQL NULL
	 * @throws IncorrectResultSizeDataAccessException if the query does not return
	 * exactly one row, or does not return exactly one column in that row
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #queryForLong(String, Object[])
	 */
	long queryForLong(String sql) throws DataAccessException;

	/**
	 * Execute a query that results in an int value, given static SQL.
	 * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
	 * execute a static query with a PreparedStatement, use the overloaded
	 * <code>queryForInt</code> method with <code>null</code> as argument array.
	 * <p>This method is useful for running static SQL with a known outcome.
	 * The query is expected to be a single row/single column query that results
	 * in an int value.
	 * @param sql SQL query to execute
	 * @return the int value, or 0 in case of SQL NULL
	 * @throws IncorrectResultSizeDataAccessException if the query does not return
	 * exactly one row, or does not return exactly one column in that row
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #queryForInt(String, Object[])
	 */
	int queryForInt(String sql) throws DataAccessException;

	/**
	 * Execute a query for a result list, given static SQL.
	 * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
	 * execute a static query with a PreparedStatement, use the overloaded
	 * <code>queryForList</code> method with <code>null</code> as argument array.
	 * <p>The results will be mapped to a List (one entry for each row) of
	 * result objects, each of them matching the specified element type.
	 * @param sql SQL query to execute
	 * @param elementType the required type of element in the result list
	 * (for example, <code>Integer.class</code>)
	 * @return a List of objects that match the specified element type
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #queryForList(String, Object[], Class)
	 * @see SingleColumnRowMapper
	 */
	<T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException;

	List<Map<String, Object>> queryForList(String sql) throws DataAccessException;
	
	List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException;

	/**
	 * Issue a single SQL update operation (such as an insert, update or delete statement).
	 * @param sql static SQL to execute
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem.
	 */
	int update(String sql) throws DataAccessException;

	/**
	 * Query given SQL to create a prepared statement from SQL and a list of
	 * arguments to bind to the query, reading the ResultSet on a per-row basis
	 * with a RowCallbackHandler.
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type);
	 * may also contain {@link SqlParameterValue} objects which indicate not
	 * only the argument value but also the SQL type and optionally the scale
	 * @param rch object that will extract results, one row at a time
	 * @throws DataAccessException if the query fails
	 */
	void query(String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException;

	/**
	 * Query given SQL to create a prepared statement from SQL and a list
	 * of arguments to bind to the query, mapping each row to a Java object
	 * via a RowMapper.
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type);
	 * may also contain {@link SqlParameterValue} objects which indicate not
	 * only the argument value but also the SQL type and optionally the scale
	 * @param rowMapper object that will map one object per row
	 * @return the result List, containing mapped objects
	 * @throws DataAccessException if the query fails
	 */
	<T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException;

	/**
	 * Query given SQL to create a prepared statement from SQL and a list
	 * of arguments to bind to the query, mapping a single result row to a
	 * Java object via a RowMapper.
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type);
	 * may also contain {@link SqlParameterValue} objects which indicate not
	 * only the argument value but also the SQL type and optionally the scale
	 * @param rowMapper object that will map one object per row
	 * @return the single mapped object
	 * @throws IncorrectResultSizeDataAccessException if the query does not
	 * return exactly one row
	 * @throws DataAccessException if the query fails
	 */
	<T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper)
			throws DataAccessException;

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * list of arguments to bind to the query, expecting a result object.
	 * <p>The query is expected to be a single row/single column query; the returned
	 * result will be directly mapped to the corresponding object type.
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type);
	 * may also contain {@link SqlParameterValue} objects which indicate not
	 * only the argument value but also the SQL type and optionally the scale
	 * @param requiredType the type that the result object is expected to match
	 * @return the result object of the required type, or <code>null</code> in case of SQL NULL
	 * @throws IncorrectResultSizeDataAccessException if the query does not return
	 * exactly one row, or does not return exactly one column in that row
	 * @throws DataAccessException if the query fails
	 * @see #queryForObject(String, Class)
	 */
	<T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException;

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * list of arguments to bind to the query, resulting in a long value.
	 * <p>The query is expected to be a single row/single column query that
	 * results in a long value.
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type);
	 * may also contain {@link SqlParameterValue} objects which indicate not
	 * only the argument value but also the SQL type and optionally the scale
	 * @return the long value, or 0 in case of SQL NULL
	 * @throws IncorrectResultSizeDataAccessException if the query does not return
	 * exactly one row, or does not return exactly one column in that row
	 * @throws DataAccessException if the query fails
	 * @see #queryForLong(String)
	 */
	long queryForLong(String sql, Object... args) throws DataAccessException;

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * list of arguments to bind to the query, resulting in an int value.
	 * <p>The query is expected to be a single row/single column query that
	 * results in an int value.
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type);
	 * may also contain {@link SqlParameterValue} objects which indicate not
	 * only the argument value but also the SQL type and optionally the scale
	 * @return the int value, or 0 in case of SQL NULL
	 * @throws IncorrectResultSizeDataAccessException if the query does not return
	 * exactly one row, or does not return exactly one column in that row
	 * @throws DataAccessException if the query fails
	 * @see #queryForInt(String)
	 */
	int queryForInt(String sql, Object... args) throws DataAccessException;

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * list of arguments to bind to the query, expecting a result list.
	 * <p>The results will be mapped to a List (one entry for each row) of
	 * result objects, each of them matching the specified element type.
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type);
	 * may also contain {@link SqlParameterValue} objects which indicate not
	 * only the argument value but also the SQL type and optionally the scale
	 * @param elementType the required type of element in the result list
	 * (for example, <code>Integer.class</code>)
	 * @return a List of objects that match the specified element type
	 * @throws DataAccessException if the query fails
	 * @see #queryForList(String, Class)
	 * @see SingleColumnRowMapper
	 */
	<T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws DataAccessException;

	/**
	 * Issue a single SQL update operation (such as an insert, update or delete statement)
	 * via a prepared statement, binding the given arguments.
	 * @param sql SQL containing bind parameters
	 * @param args arguments to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type);
	 * may also contain {@link SqlParameterValue} objects which indicate not
	 * only the argument value but also the SQL type and optionally the scale
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	int update(String sql, Object... args) throws DataAccessException;

	/**
	 * Issue an update statement using a PreparedStatementSetter to set bind parameters,
	 * with given SQL. Simpler than using a PreparedStatementCreator as this method
	 * will create the PreparedStatement: The PreparedStatementSetter just needs to
	 * set parameters.
	 * @param sql SQL containing bind parameters
	 * @param pss helper that sets bind parameters. If this is {@code null}
	 * we run an update with static SQL.
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	int update(String sql, PreparedStatementSetter pss) throws DataAccessException;

	/**
	 * Issue multiple update statements on a single PreparedStatement,
	 * using batch updates and a BatchPreparedStatementSetter to set values.
	 * <p>Will fall back to separate updates on a single PreparedStatement
	 * if the JDBC driver does not support batch updates.
	 * @param sql defining PreparedStatement that will be reused.
	 * All statements in the batch will use the same SQL.
	 * @param pss object to set parameters on the PreparedStatement
	 * created by this method
	 * @return an array of the number of rows affected by each statement
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	void batchUpdate(String sql, BatchParamSetter pss, int batchSize) throws DataAccessException;
}
