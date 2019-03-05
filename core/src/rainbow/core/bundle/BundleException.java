package rainbow.core.bundle;

public class BundleException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1876496596063351851L;

    public BundleException(String msg) {
        super(msg);
    }

    public BundleException(String message, Throwable cause) {
        super(message, cause);
    }

    public BundleException(String msg, Object... args) {
        super(String.format(msg, args));
    }

    public BundleException(Throwable cause, String msg, Object... args) {
        super(String.format(msg, args), cause);
    }

}
