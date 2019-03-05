package rainbow.core.model;

/**
 * An interface for an adaptable object.
 * <p>
 * Adaptable objects can be dynamically extended to provide different interfaces
 * (or "adapters"). Adapters are created by adapter factories, which are in turn
 * managed by type by adapter managers.
 * </p>
 * For example,
 * 
 * <pre>
 *     IAdaptable a = [some adaptable];
 *     IFoo x = (IFoo)a.getAdapter(IFoo.class);
 *     if (x != null)
 *         [do IFoo things with x]
 * </pre>
 * <p>
 * This interface can be used without OSGi running.
 * </p>
 * <p>
 * </p>
 * 
 */
public interface IAdaptable {
    /**
     * Returns an object which is an instance of the given class associated with
     * this object. Returns <code>null</code> if no such object can be found.
     * 
     * @param adapter
     *            the adapter class to look up
     * @return a object castable to the given class, or <code>null</code> if
     *         this object does not have an adapter for the given class
     */
    public Object getAdapter(Class<?> adapter);
}