package rainbow.db.incrementer;


/**
 * 数值增一器，主要用来产生ID
 * 
 * @author lijinghui
 * 
 */
public interface Incrementer {

	/**
	 * Increment the data store field's max value as int.
	 * 
	 * @return int next data store value such as <b>max + 1</b>
	 */
	int nextIntValue();

	/**
	 * Increment the data store field's max value as long.
	 * 
	 * @return int next data store value such as <b>max + 1</b>
	 */
	long nextLongValue();

	/**
	 * Increment the data store field's max value as String.
	 * 
	 * @return next data store value such as <b>max + 1</b>
	 */
	String nextStringValue();

}
