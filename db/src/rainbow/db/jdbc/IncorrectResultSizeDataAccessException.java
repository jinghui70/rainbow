package rainbow.db.jdbc;

@SuppressWarnings("serial")
public class IncorrectResultSizeDataAccessException extends DataAccessException {

	private int expectedSize;

	private int actualSize;


	/**
	 * Constructor for IncorrectResultSizeDataAccessException.
	 * @param expectedSize the expected result size
	 */
	public IncorrectResultSizeDataAccessException(int expectedSize) {
		super("Incorrect result size: expected " + expectedSize);
		this.expectedSize = expectedSize;
		this.actualSize = -1;
	}

	/**
	 * Constructor for IncorrectResultSizeDataAccessException.
	 * @param expectedSize the expected result size
	 * @param actualSize the actual result size (or -1 if unknown)
	 */
	public IncorrectResultSizeDataAccessException(int expectedSize, int actualSize) {
		super("Incorrect result size: expected " + expectedSize + ", actual " + actualSize);
		this.expectedSize = expectedSize;
		this.actualSize = actualSize;
	}

	/**
	 * Constructor for IncorrectResultSizeDataAccessException.
	 * @param msg the detail message
	 * @param expectedSize the expected result size
	 */
	public IncorrectResultSizeDataAccessException(String msg, int expectedSize) {
		super(msg);
		this.expectedSize = expectedSize;
		this.actualSize = -1;
	}

	/**
	 * Constructor for IncorrectResultSizeDataAccessException.
	 * @param msg the detail message
	 * @param expectedSize the expected result size
	 * @param actualSize the actual result size (or -1 if unknown)
	 */
	public IncorrectResultSizeDataAccessException(String msg, int expectedSize, int actualSize) {
		super(msg);
		this.expectedSize = expectedSize;
		this.actualSize = actualSize;
	}


	/**
	 * Return the expected result size.
	 */
	public int getExpectedSize() {
		return expectedSize;
	}

	/**
	 * Return the actual result size (or -1 if unknown).
	 */
	public int getActualSize() {
		return actualSize;
	}

}
