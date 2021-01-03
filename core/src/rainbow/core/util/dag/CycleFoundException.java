package rainbow.core.util.dag;

/**
 * DAG 环路检测异常
 * 
 * @author lijinghui
 *
 */
public class CycleFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CycleFoundException(String message) {
		super(message);
	}

}
