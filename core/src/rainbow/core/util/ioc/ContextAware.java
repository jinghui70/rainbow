package rainbow.core.util.ioc;

/**
 * Interface to be implemented by any object that wishes to be notified of the Context that it runs in.
 * 
 * @author lijinghui
 *
 */
public interface ContextAware {

	public void setContext(Context context);

}
