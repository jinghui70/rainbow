package rainbow.db.jdbc;

import java.sql.ResultSet;
import java.util.function.Consumer;

/**
 * Represents a returned {@link java.sql.ResultSet} from a stored procedure call.
 *
 * <p>A {@link ResultSetExtractor}, {@link RowCallbackHandler} or {@link RowMapper}
 * must be provided to handle any returned rows.
 *
 * <p>Returned {@link java.sql.ResultSet ResultSets} - like all stored procedure
 * parameters - <b>must</b> have names.
 *
 * @author Thomas Risberg
 * @author Juergen Hoeller
 */
public class SqlReturnResultSet extends ResultSetSupportingSqlParameter {

	/**
	 * Create a new instance of the {@link SqlReturnResultSet} class.
	 * @param name name of the parameter, as used in input and output maps
	 * @param extractor ResultSetExtractor to use for parsing the {@link java.sql.ResultSet}
	 */
	public SqlReturnResultSet(String name, ResultSetExtractor<?> extractor) {
		super(name, 0, extractor);
	}

	/**
	 * Create a new instance of the {@link SqlReturnResultSet} class.
	 * @param name name of the parameter, as used in input and output maps
	 * @param handler RowCallbackHandler to use for parsing the {@link java.sql.ResultSet}
	 */
	public SqlReturnResultSet(String name, Consumer<ResultSet> consumer) {
		super(name, 0, consumer);
	}

	/**
	 * Create a new instance of the {@link SqlReturnResultSet} class.
	 * @param name name of the parameter, as used in input and output maps
	 * @param mapper RowMapper to use for parsing the {@link java.sql.ResultSet}
	 */
	public SqlReturnResultSet(String name, RowMapper<?> mapper) {
		super(name, 0, mapper);
	}

	/**
	 * Return whether this parameter is an implicit return parameter used during the
	 * results preocessing of the CallableStatement.getMoreResults/getUpdateCount.
	 * <p>This implementation always returns <code>true</code>.
	 */
	@Override
	public boolean isResultsParameter() {
		return true;
	}
}
