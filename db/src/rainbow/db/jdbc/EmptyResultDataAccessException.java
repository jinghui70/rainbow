package rainbow.db.jdbc;

@SuppressWarnings("serial")
public class EmptyResultDataAccessException extends IncorrectResultSizeDataAccessException {

	/**
	 * Constructor for EmptyResultDataAccessException.
	 * @param expectedSize the expected result size
	 */
	public EmptyResultDataAccessException(int expectedSize) {
		super(expectedSize, 0);
	}

	/**
	 * Constructor for EmptyResultDataAccessException.
	 * @param msg the detail message
	 * @param expectedSize the expected result size
	 */
	public EmptyResultDataAccessException(String msg, int expectedSize) {
		super(msg, expectedSize, 0);
	}

}
