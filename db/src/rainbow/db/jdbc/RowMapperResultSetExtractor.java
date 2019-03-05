package rainbow.db.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * Adapter implementation of the ResultSetExtractor interface that delegates
 * to a RowMapper which is supposed to create an object for each row.
 * Each object is added to the results List of this ResultSetExtractor.
 *
 */
public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {

	private final RowMapper<T> rowMapper;

	private final int rowsExpected;


	/**
	 * Create a new RowMapperResultSetExtractor.
	 * @param rowMapper the RowMapper which creates an object for each row
	 */
	public RowMapperResultSetExtractor(RowMapper<T> rowMapper) {
		this(rowMapper, 0);
	}

	/**
	 * Create a new RowMapperResultSetExtractor.
	 * @param rowMapper the RowMapper which creates an object for each row
	 * @param rowsExpected the number of expected rows
	 * (just used for optimized collection handling)
	 */
	public RowMapperResultSetExtractor(RowMapper<T> rowMapper, int rowsExpected) {
		Preconditions.checkNotNull(rowMapper, "RowMapper is required");
		this.rowMapper = rowMapper;
		this.rowsExpected = rowsExpected;
	}


	public List<T> extractData(ResultSet rs) throws SQLException {
		List<T> results = (this.rowsExpected > 0 ? new ArrayList<T>(this.rowsExpected) : new ArrayList<T>());
		int rowNum = 0;
		while (rs.next()) {
			results.add(this.rowMapper.mapRow(rs, rowNum++));
		}
		return results;
	}

}
