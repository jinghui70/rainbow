package rainbow.db.jdbc;

/**
 * Root of the hierarchy of data access exceptions 
 *
 * <p>As this class is a runtime exception, there is no need for user code
 * to catch it or subclasses if any error is to be considered fatal
 * (the usual case).
 *
 */
@SuppressWarnings("serial")
public class DataAccessException extends RuntimeException {

	/**
	 * Constructor for DataAccessException.
	 * @param msg the detail message
	 */
	public DataAccessException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for DataAccessException.
	 * @param msg the detail message
	 * @param cause the root cause (usually from using a underlying
	 * data access API such as JDBC)
	 */
	public DataAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}

    public DataAccessException(Throwable cause) {
        super(null, cause);
    }

}
